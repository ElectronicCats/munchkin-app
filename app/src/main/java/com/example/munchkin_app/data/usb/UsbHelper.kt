package com.example.munchkin_app.data.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.munchkin_app.data.usb.UsbSerialManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

class UsbHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private var serialManager: UsbSerialManager? = null
    private val TAG = "UsbHelper"
    val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"
    private val ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
    private val ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }
    private var requestIdCounter = 0
    // Puerto y conexión
    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private var usbDevice: UsbDevice? = null
    var onDevicesChanged: (() -> Unit)? = null


    // Receiver de permisos
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Receiver triggered: ${intent.action}")
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    val status = if (granted) "✅ Permiso concedido" else "❌ Permiso denegado"
                    Log.d(TAG, "$status para $device")
                }
                ACTION_USB_ATTACHED -> {
                    Log.d(TAG, "Receiver triggered: ${intent.action} - Dispositivo: ${intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)?.deviceName}")
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    Log.d(TAG, "Dispositivo USB conectado: ${device?.deviceName}")
                    onDevicesChanged?.invoke()  // Notifica cambio
                }
                ACTION_USB_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    Log.d(TAG, "Dispositivo USB desconectado: ${device?.deviceName}")
                    onDevicesChanged?.invoke()  // Notifica cambio
                }
            }
        }
    }
    data class DeviceListEntry(
        val device: UsbDevice,
        val name: String = device.deviceName,
    )

    fun detectDevices(): List<DeviceListEntry> {
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        return drivers.map { driver ->
            DeviceListEntry(
                name = driver.device.deviceName,
                device = driver.device
            )
        }
    }


    fun registerReceiver() {
        Log.d(TAG, "Registrando receiver")
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(ACTION_USB_ATTACHED)
            addAction(ACTION_USB_DETACHED)
        }
        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun unregisterUsbReceiver() {
        Log.d(TAG, "Desregistrando receiver")
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
        serialManager = UsbSerialManager(port!!, connection!!)
        onStatusChanged("✅ Puerto abierto y listo")
        return true
    }

    fun detectAndGetPermission(onStatusChanged: (String) -> Unit) {
        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager).firstOrNull()
        val foundDevice = usbManager.deviceList.values.firstOrNull()
        if (driver == null) {
            onStatusChanged("❌ No se encontraron dispositivos USB seriales.")
            return
        } else {
            usbDevice = foundDevice
            onStatusChanged("✅ Dispositivo detectado: ${usbDevice?.deviceName}")
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
        if (usbDevice == null) {
            onStatusChanged("⚠️ No se ha detectado ningún dispositivo. Usa 'Detectar USB' primero.")
            return
        }

        if (serialManager == null) {
            val opened = ensurePortOpen(onStatusChanged)
            if (!opened) return
        }

        serialManager?.stop()

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        val driver = availableDrivers.firstOrNull { it.device.deviceId == usbDevice?.deviceId }
        if (driver == null) {
            onStatusChanged("❌ No se encontró driver para el dispositivo.")
            return
        }

        connection = usbManager.openDevice(driver.device)
        if (connection == null) {
            onStatusChanged("❌ No se pudo abrir la conexión. Asegúrate de tener permiso.")
            return
        }

        port = driver.ports.firstOrNull()
        if (port == null) {
            onStatusChanged("❌ No se encontró puerto serial disponible.")
            return
        }

        port!!.open(connection)
        port!!.setParameters(
            115200,
            8,
            UsbSerialPort.STOPBITS_1,
            UsbSerialPort.PARITY_NONE
        )

        serialManager = UsbSerialManager(port!!, connection!!)
        serialManager?.readSerial(
            onStatusChanged = onStatusChanged,
            onError = { errorMsg ->
                Log.e(TAG, "Error asíncrono en readSerial: $errorMsg")
                onStatusChanged("❌ Error en lectura: $errorMsg. Reiniciando...")
                disconnect(onStatusChanged)
                onDevicesChanged?.invoke()
            }
        )

        onStatusChanged("✅ Lectura inicializada correctamente.")


    }

    // --- Escritura serial ---
    fun writeToSerial(request: Munchkin.MainRequest, onStatusChanged: (String) -> Unit) {
        try {
            val protobufBytes = request.toByteArray()
            serialManager?.writeToSerial(
                requestBytes = protobufBytes,
                onStatusChanged = onStatusChanged,
                onError = { errorMsg ->
                    Log.e(TAG, "Error asíncrono en writeToSerial: $errorMsg")
                    onStatusChanged("❌ Error al enviar: $errorMsg. Reiniciando...")
                    disconnect(onStatusChanged)
                    onDevicesChanged?.invoke()
                }
            )
        } catch (e: Exception) {
            onStatusChanged("❌ Error al enviar datos: ${e.message}. Reiniciando...")
            Log.e(TAG, "Error en writeToSerial", e)
            disconnect(onStatusChanged)  // Cierra puerto y resetea
            onDevicesChanged?.invoke()  // Fuerza refresco de lista
        }
    }

    fun stopReading() {
        serialManager?.stop()
        port = null
        connection = null
    }

    fun disconnect(onStatusChanged: (String) -> Unit) {
        Log.d(TAG, "Ejecutando disconnect")
        stopReading()
        usbDevice = null
        serialManager = null
        onStatusChanged("🔌 Desconectado")
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
}