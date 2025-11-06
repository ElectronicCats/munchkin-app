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
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.qualifiers.ApplicationContext
import minino.rpc.Main
import java.io.IOException
import javax.inject.Inject

class UsbHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val TAG = "UsbHelper"
    val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"
    private val ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
    private val ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED"

    private val usbManager: UsbManager by lazy {
        context.getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private var usbDevice: UsbDevice? = null

    var serialManager: UsbSerialManager? = null
    var onDevicesChanged: (() -> Unit)? = null

    var onUsbPermissionGranted: (() -> Unit)? = null

    // --- Receiver para permisos y eventos USB ---
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Receiver triggered: ${intent.action}")
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    val status = if (granted) "Permission granted" else "Permission denied"
                    Log.d(TAG, "$status para $device")

                    if (granted) {
                        onUsbPermissionGranted?.invoke()
                    }
                }
                ACTION_USB_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    Log.d(TAG, "USB conectado: ${device?.deviceName}")
                    onDevicesChanged?.invoke()
                }
                ACTION_USB_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    Log.d(TAG, "USB desconectado: ${device?.deviceName}")
                    onDevicesChanged?.invoke()
                }
            }
        }
    }

    data class DeviceListEntry(val device: UsbDevice, val name: String = device.deviceName)

    fun detectDevices(): List<DeviceListEntry> {
        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        return drivers.map { DeviceListEntry(it.device, it.device.deviceName) }
    }

    fun setProtobufCallback(callback: (ByteArray) -> Unit) {
        serialManager?.onProtobufReceived = callback
    }

    fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(ACTION_USB_ATTACHED)
            addAction(ACTION_USB_DETACHED)
        }
        ContextCompat.registerReceiver(context, usbReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    fun unregisterUsbReceiver() {
        try {
            context.unregisterReceiver(usbReceiver)
        } catch (e: Exception) {
            Log.w(TAG, "Receiver ya desregistrado: ${e.message}")
        }
    }

    // --- Abrir puerto y arrancar lectura continua ---
    fun ensurePortOpen(onStatusChanged: (String) -> Unit): Boolean {
        if (port != null && connection != null && serialManager != null) {
            return true // ya está abierto
        }

        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager).firstOrNull()
        if (driver == null) {
            onStatusChanged("No USB devices available")
            return false
        }

        val device = driver.device
        if (!usbManager.hasPermission(device)) {
            onStatusChanged("USB permission denied for ${device.deviceName}")
            return false
        }

        val conn = usbManager.openDevice(device) ?: run {
            onStatusChanged("Error while opening connection USB")
            return false
        }

        val p = driver.ports[0]
        try {
            p.open(conn)
            p.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            p.dtr = true
            p.rts = true
        } catch (e: IOException) {
            onStatusChanged("Error opening USB port: ${e.message}")
            return false
        }

        // Crear y arrancar el nuevo serialManager
        serialManager = UsbSerialManager(p, conn).apply {
            onStatus = { msg -> onStatusChanged(msg) }
            onError = { err -> onStatusChanged("USB Error: $err") }
            startReading()
        }

        port = p
        connection = conn
        usbDevice = device
        onStatusChanged("Port opened and ready to read")
        return true
    }

    // --- Solicitar permisos ---
    fun detectAndGetPermission(onStatusChanged: (String) -> Unit) {
        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager).firstOrNull()
        val foundDevice = usbManager.deviceList.values.firstOrNull()
        if (driver == null) {
            onStatusChanged("No serial devices found.")
            return
        } else {
            usbDevice = foundDevice
            onStatusChanged("Detected USB device: ${usbDevice?.deviceName}")
        }

        val device = driver.device
        if (!usbManager.hasPermission(device)) {
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            usbManager.requestPermission(device, permissionIntent)
            onStatusChanged("Granting USB permission...")
        } else {
            onStatusChanged("Device connected.")
        }
    }

    // --- Escritura serial (protobuf) ---
    fun writeToSerial(request: Main.MainRequest, onStatusChanged: (String) -> Unit) {
        if (serialManager == null) {
            val opened = ensurePortOpen(onStatusChanged)
            if (!opened) return
        }

        try {
            val protobufBytes = request.toByteArray()
            serialManager?.writeToSerial(
                payload = protobufBytes,
                addLengthHeader = true,
                onStatusChanged = onStatusChanged,
                onErrorCallback = { err ->
                    Log.e(TAG, "Error en writeToSerial: $err")
                    onStatusChanged("Error while trying to write: $err")
                    disconnect(onStatusChanged)
                    onDevicesChanged?.invoke()
                }
            )
        } catch (e: Exception) {
            onStatusChanged("Error while trying to write data: ${e.message}")
            Log.e(TAG, "Excepción en writeToSerial", e)
            disconnect(onStatusChanged)
            onDevicesChanged?.invoke()
        }
    }

    // --- Desconexión y limpieza ---
    fun disconnect(onStatusChanged: (String) -> Unit) {
        Log.d(TAG, "Cerrando conexión USB")
        try {
            serialManager?.stopAndClose()
        } catch (e: Exception) {
            Log.e(TAG, "Error cerrando puerto: ${e.message}")
        }
        port = null
        connection = null
        usbDevice = null
        serialManager = null
        onStatusChanged("Disconnected")
    }
}
