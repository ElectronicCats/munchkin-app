package com.example.munchkin_app.data.usb

import android.hardware.usb.UsbDeviceConnection
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.experimental.and

class UsbSerialManager(
    private val port: UsbSerialPort,
    private val connection: UsbDeviceConnection
) {
    private val TAG = "UsbSerialManager"

    private val readExecutor = Executors.newSingleThreadExecutor()
    private val writeExecutor = Executors.newSingleThreadExecutor()

    // Buffer compartido para acumulación de bytes entrantes
    private val buffer = mutableListOf<Byte>()

    // Control de lectura continua
    private val reading = AtomicBoolean(false)

    var onProtobufReceived: ((ByteArray) -> Unit)? = null
    var onStatus: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    /**
     * Inicia la lectura continua. Llamar solo una vez tras abrir el puerto.
     */
    fun startReading() {
        if (reading.getAndSet(true)) {
            onStatus?.invoke("⚠️ Lectura ya iniciada")
            return
        }
        onStatus?.invoke("✅ Lectura iniciada (background)")

        readExecutor.submit {
            val temp = ByteArray(512)
            try {
                while (reading.get()) {
                    val len = try {
                        // Timeout relativamente corto para no bloquear indefinidamente
                        port.read(temp, 500)
                    } catch (e: IOException) {
                        val msg = "❌ Error en read(): ${e.message}"
                        Log.e(TAG, msg, e)
                        onError?.invoke(msg)
                        break
                    }

                    if (len <= 0) {
                        // nada recibido en este ciclo
                        continue
                    }

                    // Añadimos los bytes leídos al buffer
                    synchronized(buffer) {
                        for (i in 0 until len) buffer.add(temp[i])
                    }

                    // Loguea los bytes leídos (hex) para depuración
                    val hex = temp.copyOf(len).joinToString(" ") { String.format("%02X", it) }
                    Log.d(TAG, "Bytes leídos ($len): $hex")

                    // Procesa buffer
                    processBuffer()
                }
            } finally {
                reading.set(false)
                onStatus?.invoke("ℹ️ Lectura finalizada")
            }
        }
    }

    /**
     * Parar lectura y cerrar recursos (no destruyas port/connection aquí si quieres reusar, usa stopAndClose)
     */
    fun stopReading() {
        reading.set(false)
        try {
            readExecutor.shutdownNow()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutdown readExecutor: ${e.message}")
        }
    }

    /**
     * Parar y cerrar puerto/conexión
     */
    fun stopAndClose() {
        stopReading()
        try {
            writeExecutor.shutdownNow()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutdown writeExecutor: ${e.message}")
        }

        try {
            port.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing port: ${e.message}")
        }
        try {
            connection.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing connection: ${e.message}")
        }
        onStatus?.invoke("🔌 Conexión cerrada")
    }

    /**
     * Procesa el buffer en busca de mensajes: 2 bytes de header (big-endian) + payload.
     */
    private fun processBuffer() {
        while (true) {
            val msgBytes: ByteArray? = synchronized(buffer) {
                if (buffer.size < 2) return@synchronized null // no hay header completo
                // big-endian length
                val high = buffer[0].toInt() and 0xFF
                val low = buffer[1].toInt() and 0xFF
                val expectedLength = (high shl 8) or low

                // sanity check: evitar longitudes locas
                if (expectedLength <= 0) {
                    // elimina header corrupto y continúa
                    buffer.removeAt(0)
                    return@synchronized null
                }

                // si no tenemos todo el mensaje, espera
                if (buffer.size < 2 + expectedLength) return@synchronized null

                // extraer payload
                val payload = ByteArray(expectedLength)
                for (i in 0 until expectedLength) {
                    payload[i] = buffer[2 + i]
                }
                // remover bytes procesados
                for (i in 0 until (2 + expectedLength)) buffer.removeAt(0)
                payload
            }

            // si no hay mensaje listo, salir del loop
            if (msgBytes == null) break

            // Log y callback fuera del synchronized
            Log.d(TAG, "📥 Mensaje completo recibido (${msgBytes.size} bytes): ${msgBytes.joinToString(" ") { String.format("%02X", it) }}")
            onStatus?.invoke("📥 Recibidos ${msgBytes.size} bytes")
            try {
                onProtobufReceived?.invoke(msgBytes)
            } catch (e: Exception) {
                Log.e(TAG, "Error en onProtobufReceived: ${e.message}", e)
            }
        }
    }

    /**
     * Escribe un array de bytes al puerto. Añade header de 2 bytes (big-endian) si `addLengthHeader = true`.
     * Reintenta hasta 3 veces en fallos transitorios.
     */
    fun writeToSerial(
        payload: ByteArray,
        addLengthHeader: Boolean = true,
        onStatusChanged: ((String) -> Unit)? = null,
        onErrorCallback: ((String) -> Unit)? = null
    ) {
        writeExecutor.submit {
            val full: ByteArray = if (addLengthHeader) {
                val len = payload.size
                val header = byteArrayOf(((len shr 8) and 0xFF).toByte(), (len and 0xFF).toByte())
                header + payload
            } else {
                payload
            }

            val hexSent = full.joinToString(" ") { String.format("%02X", it) }
            var lastEx: Exception? = null
            repeat(3) { attempt ->
                try {
                    port.write(full, 3000)
                    Log.d(TAG, "📤 Enviado (${full.size} bytes): $hexSent")
                    onStatusChanged?.invoke("📤 Enviado OK (${full.size} bytes)")
                    this.onStatus?.invoke("📤 Enviado OK (${full.size} bytes)")
                    return@submit
                } catch (e: Exception) {
                    lastEx = e
                    Log.w(TAG, "Intento ${attempt + 1} fallido al escribir: ${e.message}")
                    TimeUnit.MILLISECONDS.sleep(100)
                }
            }
            val msg = "❌ Error enviando datos: ${lastEx?.message ?: "unknown"}"
            Log.e(TAG, msg, lastEx)
            onErrorCallback?.invoke(msg)
            onError?.invoke(msg)
        }
    }
}
