package com.example.munchkin_app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import simple.Simple
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.Executors

class UsbHelper(private val context: Context) {

    private enum class State {
        SEARCHING_HEADER, // Buscando el inicio del patrón "LENGTH:"
        READING_LENGTH,   // Leyendo los dígitos de la longitud
        READING_DATA      // Leyendo los bytes binarios del Protobuf
    }

    companion object {
        const val HEADER_MAX_SIZE = 64
    }

    val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? =
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let {
                            Log.d("UsbHelper", "Permiso concedido para $it")
                        }
                    } else {
                        Log.d("UsbHelper", "Permiso denegado para $device")
                    }
                }
            }
        }
    }

    fun readSerialDebug(onStatusChanged: (String) -> Unit) {
        val manager = usbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            onStatusChanged("No hay drivers USB")
            return
        }

        val driver = availableDrivers[0]
        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            onStatusChanged("No se pudo abrir el dispositivo. ¿Permiso concedido?")
            return
        }

        val port = driver.ports[0]
        try {
            port.open(connection)
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

            val executor = Executors.newSingleThreadExecutor()
            executor.submit {
                val readBuffer = ByteArray(256)

                // --- Variables de Framing y Estado ---
                var state = State.SEARCHING_HEADER
                var currentBuffer = mutableListOf<Byte>() // Buffer acumulativo de bytes recibidos
                var expectedLength: Int = 0
                val HEADER_START = "LENGTH:".toByteArray(Charsets.US_ASCII)
                val HEADER_SEPARATOR = '\n'.toByte()

                while (true) {
                    try {
                        // 1. Lectura del puerto USB
                        val len = port.read(readBuffer, 100) // Timeout 100ms
                        if (len > 0) {
                            // 🔹 DEBUG: Mostrar los bytes leídos en hexadecimal
                            val hex = readBuffer.take(len).joinToString(" ") { String.format("%02X", it) }
                            onStatusChanged("🔹 Bytes leídos ($len): $hex")

                            // Acumular bytes en el buffer actual
                            currentBuffer.addAll(readBuffer.take(len))

                            // 2. Procesamiento del buffer acumulado
                            while (currentBuffer.isNotEmpty()) {
                                when (state) {
                                    State.SEARCHING_HEADER -> {
                                        // Buscar el inicio de "LENGTH:"
                                        val ascii = String(currentBuffer.toByteArray(), Charsets.US_ASCII)
                                        val headerIndex = ascii.indexOf("LENGTH:")
                                        if (headerIndex == -1) {
                                            // No se encuentra el patrón
                                            if (currentBuffer.size > 64) {
                                                onStatusChanged("Descartando ${currentBuffer.size} bytes (sin header visible).")
                                                currentBuffer.clear()
                                            }
                                            break
                                        }

                                        // Cortar todo lo anterior al header
                                        if (headerIndex > 0) {
                                            currentBuffer = currentBuffer.drop(headerIndex).toMutableList()
                                        }

                                        // Verificar si ya tenemos el header completo con '\n'
                                        val newlineIndex = ascii.indexOf('\n', headerIndex)
                                        if (newlineIndex != -1) {
                                            val headerPart = ascii.substring(headerIndex, newlineIndex).trim()
                                            val lengthText = headerPart.removePrefix("LENGTH:").trim()
                                            expectedLength = lengthText.toIntOrNull() ?: 0

                                            if (expectedLength > 0) {
                                                onStatusChanged("📏 Longitud extraída: $expectedLength bytes.")
                                                // Saltar los bytes del header + '\n'
                                                val bytesToDrop = newlineIndex - headerIndex + 1
                                                currentBuffer = currentBuffer.drop(bytesToDrop).toMutableList()
                                                state = State.READING_DATA
                                                currentBuffer.clear() // 🔧 limpiar cualquier residuo textual
                                            } else {
                                                onStatusChanged("❌ Longitud inválida en header: '$lengthText'")
                                                currentBuffer.clear()
                                            }
                                        } else {
                                            // Falta el salto de línea, esperar más bytes
                                            break
                                        }
                                    }

                                    State.READING_DATA -> {
                                        if (currentBuffer.size >= expectedLength) {
                                            val protobufBytes = currentBuffer.take(expectedLength).toByteArray()
                                            currentBuffer = currentBuffer.drop(expectedLength).toMutableList()

                                            val dataHex = protobufBytes.joinToString(" ") { String.format("%02X", it) }
                                            onStatusChanged("📦 Recibido $expectedLength bytes: $dataHex")

                                            try {
                                                val simpleMessage = Simple.SimpleMessage.parseFrom(protobufBytes)
                                                onStatusChanged("✅ Protobuf decodificado: lucky_number = ${simpleMessage.luckyNumber}")
                                            } catch (e: Exception) {
                                                onStatusChanged("❌ Error decodificando Protobuf: ${e.message}")
                                            }

                                            // Reiniciar estado para el siguiente mensaje
                                            state = State.SEARCHING_HEADER
                                            expectedLength = 0
                                            currentBuffer.clear()
                                        } else {
                                            val faltan = expectedLength - currentBuffer.size
                                            onStatusChanged("⏳ Esperando $faltan bytes más...")
                                            break
                                        }
                                    }

                                    State.READING_LENGTH -> TODO()
                                }
                            }
                        }
                    } catch (e: IOException) {
                        onStatusChanged("Error de lectura: ${e.message}")
                        break
                    } catch (e: Exception) {
                        onStatusChanged("Error inesperado en loop de lectura: ${e.message}")
                    }
                }

            }
        } catch (e: Exception) {
            onStatusChanged("Error abriendo puerto: ${e.message}")
        }
    }


    fun registerReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter)
    }

    fun unregisterUsbReceiver() {
        context.unregisterReceiver(usbReceiver)
    }

    fun detectAndGetPermission(onStatusChanged: (String) -> Unit) {
        val devices = usbManager.deviceList
        val device = devices.values.firstOrNull()
        if (device == null) {
            onStatusChanged("No hay dispositivos conectados")
            return
        }

        if (!usbManager.hasPermission(device)) {
            val permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            usbManager.requestPermission(device, permissionIntent)
            onStatusChanged("Pidiendo permiso...")
        } else {
            onStatusChanged("Ya tienes permiso para ${device.deviceName}")
        }
    }

    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private var executor = Executors.newSingleThreadExecutor()
    @Volatile private var isReading = false

    fun stopReading() {
        isReading = false
        executor.shutdownNow()
        try {
            port?.close()
            connection?.close()
        } catch (e: Exception) {
            Log.e("UsbHelper", "Error cerrando puerto: ${e.message}")
        }
    }

    fun readSerial(onStatusChanged: (String) -> Unit) {
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            onStatusChanged("No hay drivers USB")
            return
        }

        val driver = availableDrivers[0]
        connection = usbManager.openDevice(driver.device)
        if (connection == null) {
            onStatusChanged("No se pudo abrir el dispositivo. ¿Permiso concedido?")
            return
        }

        port = driver.ports[0]
        try {
            port?.open(connection)
            port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        } catch (e: Exception) {
            onStatusChanged("Error abriendo puerto: ${e.message}")
            return
        }

        isReading = true
        val buffer = ByteArrayOutputStream()
        val readBuffer = ByteArray(256)
        val timeout = 1000

        executor.submit {
            while (isReading) {
                try {
                    val len = port?.read(readBuffer, timeout) ?: 0
                    if (len <= 0) {
                        Thread.sleep(10)
                        continue
                    }

                    buffer.write(readBuffer, 0, len)
                    val dataBytes = buffer.toByteArray()
                    val dataString = String(dataBytes, Charsets.US_ASCII)

                    val regex = Regex("LENGTH:(\\d+)\\n")
                    val match = regex.find(dataString)

                    if (match != null) {
                        val msgLength = match.groupValues[1].toIntOrNull() ?: 0
                        if (msgLength <= 0) {
                            onStatusChanged("Longitud inválida en header: $msgLength")
                            buffer.reset()
                            continue
                        }

                        val headerEndIndex = match.range.last + 1
                        val remainingData = dataBytes.copyOfRange(headerEndIndex, dataBytes.size)

                        if (remainingData.size >= msgLength) {
                            val messageBytes = remainingData.copyOfRange(0, msgLength)
                            try {
                                val simpleMessage = Simple.SimpleMessage.parseFrom(messageBytes)
                                onStatusChanged("PROTOBUF DECODIFICADO: lucky_number = ${simpleMessage.luckyNumber}")
                            } catch (e: Exception) {
                                onStatusChanged("Error parsing protobuf: ${e.message}")
                            }

                            val extra = remainingData.copyOfRange(msgLength, remainingData.size)
                            buffer.reset()
                            buffer.write(extra)
                        }
                    }

                    if (buffer.size() > 2048) {
                        onStatusChanged("Buffer demasiado grande, reiniciando...")
                        buffer.reset()
                    }

                } catch (e: IOException) {
                    onStatusChanged("Error de lectura: ${e.message}")
                    break
                } catch (e: Exception) {
                    onStatusChanged("Error inesperado: ${e.message}")
                }
            }
        }
    }
}
