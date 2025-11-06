package com.example.munchkin_app.data.usb

import android.hardware.usb.UsbDeviceConnection
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class UsbSerialManager(
    private val port: UsbSerialPort,
    private val connection: UsbDeviceConnection
) {
    private val TAG = "UsbSerialManager"

    private val readExecutor = Executors.newSingleThreadExecutor()
    private val writeExecutor = Executors.newSingleThreadExecutor()

    private val buffer = mutableListOf<Byte>()
    private val reading = AtomicBoolean(false)

    var onProtobufReceived: ((ByteArray) -> Unit)? = null
    var onStatus: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

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
                        port.read(temp, 500)
                    } catch (e: IOException) {
                        val msg = "❌ Error en read(): ${e.message}"
                        Log.e(TAG, msg, e)
                        onError?.invoke(msg)
                        break
                    }

                    if (len <= 0) continue

                    synchronized(buffer) {
                        for (i in 0 until len) buffer.add(temp[i])
                    }

                    val hex = temp.copyOf(len).joinToString(" ") { String.format("%02X", it) }
                    Log.d(TAG, "Bytes leídos ($len): $hex")

                    processBuffer()
                }
            } finally {
                reading.set(false)
                onStatus?.invoke("ℹ️ Lectura finalizada")
            }
        }
    }

    fun stopReading() {
        reading.set(false)
        try {
            readExecutor.shutdownNow()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutdown readExecutor: ${e.message}")
        }
    }

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
     * Procesa el buffer en busca de mensajes completos.
     * - Soporta encabezado de longitud o delimitador 0D 0A
     * - Ignora mensajes de control pequeños (13, 0D, 0A)
     */
    private fun processBuffer() {
        while (true) {
            val msgBytes: ByteArray? = synchronized(buffer) {
                if (buffer.isEmpty()) return@synchronized null

                // --- Protocolo basado en longitud ---
                if (buffer.size >= 2) {
                    val high = buffer[0].toInt() and 0xFF
                    val low = buffer[1].toInt() and 0xFF
                    val expectedLength = (high shl 8) or low
                    if (expectedLength in 1..4096 && buffer.size >= 2 + expectedLength) {
                        val payload = ByteArray(expectedLength)
                        for (i in 0 until expectedLength) {
                            payload[i] = buffer[2 + i]
                        }
                        repeat(2 + expectedLength) { buffer.removeAt(0) }
                        return@synchronized payload
                    }
                }

                // --- Protocolo con terminador 0D 0A ---
                val endIndex = buffer.windowed(2).indexOfFirst {
                    it[0] == 0x0D.toByte() && it[1] == 0x0A.toByte()
                }
                if (endIndex != -1) {
                    val payload = buffer.subList(0, endIndex).toByteArray()
                    repeat(endIndex + 2) { buffer.removeAt(0) }
                    return@synchronized payload
                }

                null
            }

            if (msgBytes == null) break

            // --- 🧹 Filtro de paquetes de control pequeños ---
            if (msgBytes.size <= 3 && msgBytes.all { it == 0x13.toByte() || it == 0x0D.toByte() || it == 0x0A.toByte() }) {
                Log.d(TAG, "🧹 Ignorado paquete de control: ${msgBytes.joinToString(" ") { "%02X".format(it) }}")
                continue
            }

            Log.d(TAG, "📥 Mensaje completo (${msgBytes.size} bytes): ${msgBytes.joinToString(" ") { "%02X".format(it) }}")
            onStatus?.invoke("Connexion OK, received [${msgBytes.size} bytes]")
            try {
                onProtobufReceived?.invoke(msgBytes)
            } catch (e: Exception) {
                Log.e(TAG, "Error en onProtobufReceived: ${e.message}", e)
            }
        }
    }

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
            } else payload

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
