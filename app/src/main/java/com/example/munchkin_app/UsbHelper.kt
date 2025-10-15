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
import Munchkin
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

    val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private var requestIdCounter: Int = 0

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

    fun readSerial(onStatusChanged: (String) -> Unit) {
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

            // Asignar a variables de instancia para reutilizar en writeToSerial
            this.port = port  // <-- Añade esta línea
            this.connection = connection  // <-- Añade esta línea

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
                                                // Intentar decodificar como MainResponse
                                                val mainResponse = Munchkin.MainResponse.parseFrom(protobufBytes)

                                                onStatusChanged("✅ Protobuf decodificado:")
                                                onStatusChanged("   🆔 Response ID: ${mainResponse.id}")
                                                onStatusChanged("   📊 Status: ${mainResponse.status}")

                                                // Procesar el contenido según el tipo
                                                when {
                                                    mainResponse.hasLedControlResponse() -> {
                                                        onStatusChanged("   💡 Tipo: LED Control Response")
                                                        onStatusChanged("   ✅ Comando LED ejecutado correctamente")
                                                    }

                                                    mainResponse.hasCounterResponse() -> {
                                                        val counterValue = mainResponse.counterResponse.value
                                                        onStatusChanged("   🔢 Tipo: Counter Response")
                                                        onStatusChanged("   📈 Valor del contador: $counterValue")
                                                    }

                                                    else -> {
                                                        onStatusChanged("   ⚠️ Respuesta sin contenido específico")
                                                    }
                                                }

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

    fun writeToSerial(request: Munchkin.MainRequest, onStatusChanged: (String) -> Unit) {
        executor.submit {
            try {
                // Verificar que el puerto esté abierto
                if (port == null || connection == null) {
                    onStatusChanged("❌ Puerto no inicializado. Llama readSerial() primero.")
                    return@submit
                }

                // Serializar el Protobuf a bytes
                val protobufBytes = request.toByteArray()
                val length = protobufBytes.size

                // Construir el header: "LENGTH:<num>\n"
                val header = "LENGTH:$length\n".toByteArray(Charsets.US_ASCII)

                // Combinar header + datos binarios
                val fullMessage = header + protobufBytes

                // Enviar por el puerto serial
                port?.write(fullMessage, 1000) // Timeout de 1 segundo

                // Debug: mostrar lo que se envió
                val dataHex = protobufBytes.joinToString(" ") { String.format("%02X", it) }

                onStatusChanged("📤 Enviado: LENGTH:$length")
                onStatusChanged("📦 Data hex: $dataHex")
                onStatusChanged("🆔 Request ID: ${request.id}")

            } catch (e: IOException) {
                onStatusChanged("❌ Error escribiendo al puerto: ${e.message}")
            } catch (e: Exception) {
                onStatusChanged("❌ Error inesperado: ${e.message}")
            }
        }
    }

    /**
     * Envía comando para encender el LED
     */
    fun sendLedOn(onStatusChanged: (String) -> Unit) {
        val ledRequest = Munchkin.LedControlRequest.newBuilder()
            .setEnable(true)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setLedControl(ledRequest)
            .build()

        onStatusChanged("💡 Enviando: LED ON")
        writeToSerial(mainRequest, onStatusChanged)
    }

    /**
     * Envía comando para apagar el LED
     */
    fun sendLedOff(onStatusChanged: (String) -> Unit) {
        val ledRequest = Munchkin.LedControlRequest.newBuilder()
            .setEnable(false)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setLedControl(ledRequest)
            .build()

        onStatusChanged("🌑 Enviando: LED OFF")
        writeToSerial(mainRequest, onStatusChanged)
    }

    /**
     * Toggle LED (envía ON si está OFF, o OFF si está ON)
     * Nota: Necesitarías mantener el estado actual para hacer un toggle real
     * Por ahora, simplemente alternamos basándonos en el requestId
     */
    fun sendToggleLed(onStatusChanged: (String) -> Unit) {
        // Alternar basado en el número de request
        val shouldEnable = (requestIdCounter % 2 == 0)

        val ledRequest = Munchkin.LedControlRequest.newBuilder()
            .setEnable(shouldEnable)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setLedControl(ledRequest)
            .build()

        onStatusChanged("🔄 Enviando: LED ${if (shouldEnable) "ON" else "OFF"}")
        writeToSerial(mainRequest, onStatusChanged)
    }

// --- Funciones para Control de Contador ---

    /**
     * Inicia el envío periódico del contador desde el ESP32
     */
    fun startCounter(onStatusChanged: (String) -> Unit) {
        val counterRequest = Munchkin.CounterControlRequest.newBuilder()
            .setAction(Munchkin.CounterControlRequest.Action.ACTION_START)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setCounterControl(counterRequest)
            .build()

        onStatusChanged("▶️ Enviando: START Counter")
        writeToSerial(mainRequest, onStatusChanged)
    }

    /**
     * Detiene el envío periódico del contador
     */
    fun stopCounter(onStatusChanged: (String) -> Unit) {
        val counterRequest = Munchkin.CounterControlRequest.newBuilder()
            .setAction(Munchkin.CounterControlRequest.Action.ACTION_STOP)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setCounterControl(counterRequest)
            .build()

        onStatusChanged("⏹️ Enviando: STOP Counter")
        writeToSerial(mainRequest, onStatusChanged)
    }

    /**
     * Solicita el valor actual del contador (una sola vez)
     */
    fun getCounterValue(onStatusChanged: (String) -> Unit) {
        val counterRequest = Munchkin.CounterControlRequest.newBuilder()
            .setAction(Munchkin.CounterControlRequest.Action.ACTION_GET)
            .build()

        val mainRequest = Munchkin.MainRequest.newBuilder()
            .setId(++requestIdCounter)
            .setCounterControl(counterRequest)
            .build()

        onStatusChanged("📊 Enviando: GET Counter")
        writeToSerial(mainRequest, onStatusChanged)
    }
}
