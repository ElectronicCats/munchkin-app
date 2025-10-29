package com.example.munchkin_app.data.usb

import android.hardware.usb.UsbDeviceConnection
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UsbSerialManager(
    private val port: UsbSerialPort,
    private val connection: UsbDeviceConnection
) {
    private enum class State { SEARCHING_HEADER, READING_DATA }

    @Volatile private var isReading = false
    private val readExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val writeExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val TAG = "UsbSerialManager"

    fun readSerial(onStatusChanged: (String) -> Unit, onError: (String) -> Unit) {
        if (isReading) {
            onStatusChanged("⚠️ Ya se está leyendo el puerto.")
            return
        }

        isReading = true
        onStatusChanged("✅ Lectura iniciada...")

        readExecutor.submit {
            val buffer = ByteArray(256)
            var state = State.SEARCHING_HEADER
            var currentBuffer = mutableListOf<Byte>()
            var expectedLength = 0

            try {
                while (isReading) {
                    val len = try {
                        port.read(buffer, 100)
                    } catch (e: IOException) {
                        onStatusChanged("❌ Error de lectura: ${e.message}")
                        onError("❌ Error de lectura: ${e.message}")
                        break
                    }

                    if (len <= 0) continue

                    currentBuffer.addAll(buffer.take(len))

                    while (true) {
                        when (state) {
                            State.SEARCHING_HEADER -> {
                                val ascii = String(currentBuffer.toByteArray(), Charsets.US_ASCII)
                                val idx = ascii.indexOf("LENGTH:")
                                if (idx == -1) break

                                if (idx > 0) currentBuffer = currentBuffer.drop(idx).toMutableList()

                                val newlineIdx = currentBuffer.indexOf('\n'.code.toByte())
                                if (newlineIdx == -1) break

                                val headerStr = String(currentBuffer.take(newlineIdx + 1).toByteArray()).trim()
                                expectedLength = headerStr.removePrefix("LENGTH:").trim().toIntOrNull() ?: 0
                                currentBuffer = currentBuffer.drop(newlineIdx + 1).toMutableList()
                                state = State.READING_DATA
                            }

                            State.READING_DATA -> {
                                if (currentBuffer.size >= expectedLength && expectedLength > 0) {
                                    val safeLength = minOf(expectedLength, currentBuffer.size)
                                    val protobufBytes = currentBuffer.take(safeLength).toByteArray()
                                    currentBuffer = currentBuffer.drop(safeLength).toMutableList()

                                    val hexString = protobufBytes.joinToString(" ") { "%02X".format(it) }
                                    Log.d(TAG, "Bytes a parsear (${protobufBytes.size} bytes): $hexString")

                                    try {
                                        val mainResponse = Munchkin.MainResponse.parseFrom(protobufBytes)
                                        onStatusChanged(
                                            "✅ Respuesta ID=${mainResponse.id}, Status=${mainResponse.status}, Counter=${mainResponse.counterResponse}"
                                        )
                                    } catch (e: Exception) {
                                        Log.d(TAG, "${e.message}")
                                        onStatusChanged("❌ Error parseando Protobuf: ${e.message}")
                                    }

                                    state = State.SEARCHING_HEADER
                                    expectedLength = 0
                                } else break
                            }
                        }
                    }
                }
            } finally {
                isReading = false
                onStatusChanged("ℹ️ Lectura finalizada.")
            }
        }
    }

    fun writeToSerial(requestBytes: ByteArray, onStatusChanged: (String) -> Unit, onError: (String) -> Unit) {
        writeExecutor.submit {
            try {
                if (port == null || connection == null) {
                    onStatusChanged("❌ Puerto no inicializado")
                    return@submit
                }

                val header = "LENGTH:${requestBytes.size}\n".toByteArray(Charsets.US_ASCII)
                val fullMessage = header + requestBytes

                var attempts = 0
                var success = false
                while (attempts < 3 && !success) {
                    try {
                        Thread.sleep(100)
                        port.write(fullMessage, 3000)
                        success = true
                        onStatusChanged("📤 Enviado OK (${requestBytes.size} bytes)")
                    } catch (e: IOException) {
                        attempts++
                        Log.w(TAG, "⚠️ Intento $attempts falló: ${e.message}")
                        if (attempts >= 3) {
                            val errorMsg = "Error después de 3 intentos: ${e.message}"
                            onStatusChanged("❌ $errorMsg")
                            onError(errorMsg)  // Llama al callback de error
                        }
                    }
                }

            } catch (e: IOException) {
                onStatusChanged("❌ Error: ${e.message}")
                Log.e(TAG, "Error después de 3 intentos", e)
            }
        }
    }

    fun stop() {
        isReading = false
        try {
            readExecutor.shutdownNow()
            writeExecutor.shutdownNow()
            port.close()
            connection.close()
            Log.d(TAG, "🔌 Conexión cerrada.")
        } catch (e: Exception) {
            Log.e(TAG, "Error cerrando conexión: ${e.message}")
        }
    }
}