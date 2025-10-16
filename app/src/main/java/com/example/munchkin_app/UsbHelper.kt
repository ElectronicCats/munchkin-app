package com.example.munchkin_app

import android.app.PendingIntent
import android.content.*
import android.hardware.usb.*
import android.util.Log
import com.hoho.android.usbserial.driver.*
import Munchkin
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UsbHelper(private val context: Context) {

    private enum class State { SEARCHING_HEADER, READING_DATA }

    private val TAG = "UsbHelper"
    val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private var requestIdCounter = 0

    // Puerto y conexión
    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    @Volatile private var isReading = false

    // Executors
    private var readExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var writeExecutor: ExecutorService = Executors.newSingleThreadExecutor()



    // Receiver de permisos
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { Log.d(TAG, "✅ Permiso concedido para $it") }
                    } else {
                        Log.d(TAG, "❌ Permiso denegado para $device")
                    }
                }
            }
        }
    }

    fun registerReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregisterUsbReceiver() {
        context.unregisterReceiver(usbReceiver)
    }

    // --- Abrir puerto solo una vez ---
    fun ensurePortOpen(onStatusChanged: (String) -> Unit): Boolean {
        if (port != null && connection != null) return true

        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager).firstOrNull()
        if (driver == null) {
            onStatusChanged("❌ No hay dispositivos USB")
            return false
        }

        val device = driver.device
        if (!usbManager.hasPermission(device)) {
            onStatusChanged("❌ Sin permiso USB")
            return false
        }

        val conn = usbManager.openDevice(device) ?: run {
            onStatusChanged("❌ No se pudo abrir la conexión")
            return false
        }

        val p = driver.ports[0]
        try {
            p.open(conn)
            p.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            p.dtr = true
            p.rts = true
        } catch (e: IOException) {
            onStatusChanged("❌ Error abriendo puerto: ${e.message}")
            return false
        }

        port = p
        connection = conn
        onStatusChanged("✅ Puerto abierto y listo")
        return true
    }

    fun detectAndGetPermission(onStatusChanged: (String) -> Unit) {
        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager).firstOrNull()
        if (driver == null) {
            onStatusChanged("❌ No se encontraron dispositivos USB seriales.")
            return
        }

        val device = driver.device
        if (!usbManager.hasPermission(device)) {
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            usbManager.requestPermission(device, permissionIntent)
            onStatusChanged("🔑 Solicitando permiso para dispositivo USB...")
        } else {
            onStatusChanged("✅ Permiso ya concedido para ${device.deviceName}")
        }
    }

    // --- Lectura serial ---
    fun readSerial(onStatusChanged: (String) -> Unit) {
        if (isReading) {
            onStatusChanged("⚠️ Ya se está leyendo el puerto.")
            return
        }

        val currentPort = port ?: run {
            if (!ensurePortOpen(onStatusChanged)) return
            port!!
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
                        currentPort.read(buffer, 100)
                    } catch (e: IOException) {
                        onStatusChanged("❌ Error de lectura: ${e.message}")
                        break
                    }

                    if (len <= 0) continue

                    // Agregamos datos recién leídos al buffer en software
                    currentBuffer.addAll(buffer.take(len))

                    // Procesamos tantos paquetes completos como sea posible
                    while (true) {
                        when (state) {
                            State.SEARCHING_HEADER -> {
                                val ascii = String(currentBuffer.toByteArray(), Charsets.US_ASCII)
                                val idx = ascii.indexOf("LENGTH:")
                                if (idx == -1) break // header incompleto

                                if (idx > 0) {
                                    // limpiar datos basura antes del header
                                    currentBuffer = currentBuffer.drop(idx).toMutableList()
                                }

                                val newlineIdx = currentBuffer.indexOf('\n'.code.toByte())
                                if (newlineIdx == -1) break // header incompleto

                                val headerStr = String(currentBuffer.take(newlineIdx + 1).toByteArray()).trim()
                                expectedLength = headerStr.removePrefix("LENGTH:").trim().toIntOrNull() ?: 0
                                currentBuffer = currentBuffer.drop(newlineIdx + 1).toMutableList()
                                state = State.READING_DATA
                            }

                            State.READING_DATA -> {
                                if (currentBuffer.size >= expectedLength && expectedLength > 0) {
                                    // Asegurarnos de no tomar más bytes de los que hay
                                    val safeLength = minOf(expectedLength, currentBuffer.size)
                                    val protobufBytes = currentBuffer.take(safeLength).toByteArray()
                                    currentBuffer = currentBuffer.drop(safeLength).toMutableList()

                                    // Log de los bytes que se van a parsear
                                    val hexString = protobufBytes.joinToString(" ") { "%02X".format(it) }
                                    Log.d("Protobuff", "Bytes a parsear (${protobufBytes.size} bytes): $hexString")

                                    try {
                                        val mainResponse = Munchkin.MainResponse.parseFrom(protobufBytes)
                                        onStatusChanged(
                                            "✅ Respuesta ID=${mainResponse.id}, Status=${mainResponse.status}, Counter=${mainResponse.counterResponse}"
                                        )
                                    } catch (e: Exception) {
                                        Log.d("Protobuff", "${e.message}")
                                        onStatusChanged("❌ Error parseando Protobuf: ${e.message}")
                                    }

                                    // resetear para buscar nuevo header
                                    state = State.SEARCHING_HEADER
                                    expectedLength = 0
                                } else break // esperar más datos

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


    // --- Escritura serial ---
    fun writeToSerial(request: Munchkin.MainRequest, onStatusChanged: (String) -> Unit) {
        writeExecutor.submit {
            try {
                if (port == null || connection == null) {
                    onStatusChanged("❌ Puerto no inicializado")
                    return@submit
                }

                val protobufBytes = request.toByteArray()
                val header = "LENGTH:${protobufBytes.size}\n".toByteArray(Charsets.US_ASCII)
                val fullMessage = header + protobufBytes

                // 🔧 AUMENTAR TIMEOUT Y AGREGAR RETRY
                var attempts = 0
                var success = false

                while (attempts < 3 && !success) {
                    try {
                        Thread.sleep(100) // Pequeña pausa entre intentos
                        port?.write(fullMessage, 3000) // Timeout aumentado
                        success = true

                        val dataHex = protobufBytes.joinToString(" ") { String.format("%02X", it) }
                        Log.d("UsbHelper", "✅ Enviado exitosamente en intento ${attempts + 1}")
                        onStatusChanged("📤 Enviado OK (${protobufBytes.size} bytes)")

                    } catch (e: IOException) {
                        attempts++
                        Log.w("UsbHelper", "⚠️ Intento $attempts falló: ${e.message}")
                        if (attempts >= 3) throw e
                    }
                }

            } catch (e: IOException) {
                onStatusChanged("❌ Error: ${e.message} (Detalles: ${e.stackTraceToString()})")
                Log.e("UsbHelper", "Error después de 3 intentos", e)
            }
        }
    }

    fun stopReading() {
        isReading = false
        try {
            readExecutor.shutdownNow()
            writeExecutor.shutdownNow()
            port?.close()
            connection?.close()
            Log.d(TAG, "🔌 Conexión cerrada.")
        } catch (e: Exception) {
            Log.e(TAG, "Error cerrando conexión: ${e.message}")
        }
        port = null
        connection = null
    }

    // --- Funciones RPC ---
    private fun nextId() = ++requestIdCounter

    fun startCounter(onStatusChanged: (String) -> Unit) {
        val req = Munchkin.MainRequest.newBuilder()
            .setId(nextId())
            .setCounterControl(
                Munchkin.CounterControlRequest.newBuilder()
                    .setAction(Munchkin.CounterControlRequest.Action.ACTION_START)
                    .build()
            ).build()
        onStatusChanged("▶️ Enviando START Counter")
        writeToSerial(req, onStatusChanged)
    }

    fun stopCounter(onStatusChanged: (String) -> Unit) {
        val req = Munchkin.MainRequest.newBuilder()
            .setId(nextId())
            .setCounterControl(
                Munchkin.CounterControlRequest.newBuilder()
                    .setAction(Munchkin.CounterControlRequest.Action.ACTION_STOP)
                    .build()
            ).build()
        onStatusChanged("⏹️ Enviando STOP Counter")
        writeToSerial(req, onStatusChanged)
    }

    fun sendLedCommand(enable: Boolean, onStatusChanged: (String) -> Unit) {
        val req = Munchkin.MainRequest.newBuilder()
            .setId(nextId())
            .setLedControl(
                Munchkin.LedControlRequest.newBuilder().setEnable(enable).build()
            ).build()
        onStatusChanged("💡 Enviando LED ${if (enable) "ON" else "OFF"}")
        writeToSerial(req, onStatusChanged)
    }
}