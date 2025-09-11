package com.example.munchkin_app.ui.theme

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.registerReceiver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

class UsbHelper (private val context: Context) {
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
                            Log.d(TAG, "Permiso concedido para $it")
                            // Aquí podrías abrir el dispositivo directamente o notificar a la UI
                        }
                    } else {
                        Log.d(TAG, "Permiso denegado para $device")
                    }
                }
            }
        }
    }

    fun registerReceiver() {
        // Registrar receiver una sola vez
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter)
    }

    fun unregisterUsbReceiver() {
        context.unregisterReceiver(usbReceiver)
    }

    fun getManager(): UsbManager = usbManager

    fun detectAndGetPermision(onStatusChanged: (String) -> Unit) {
        val manager = getManager()
        val devices = manager.deviceList
        val device = devices.values.firstOrNull()
        if (device == null) {
            onStatusChanged("No hay dispositivos conectados")
            return
        }

        if (!manager.hasPermission(device)) {
            // pedir permiso solo si no lo tenemos
            val permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            manager.requestPermission(device, permissionIntent)
            onStatusChanged("Pidiendo permiso...")
        } else {
            onStatusChanged("Ya tienes permiso para ${device.deviceName}")
        }
    }

    fun readSerial(onStatusChanged: (String) -> Unit) {
        val manager = getManager()
        val availableDrivers =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)
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
            port.setParameters(
                115200,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )
            val bytes = ByteArray(100)
            val len = port.read(bytes, 2000)

            if (len > 0) {
                val text = String(bytes, 0, len, Charsets.UTF_8)
                onStatusChanged("Serial recibido: $text")
            } else {
                onStatusChanged("No llegaron datos del dispositivo")
            }

        } catch (e: IOException) {
            onStatusChanged("Error: ${e.message}")
        } finally {
            try { port.close() } catch (_: Exception) {}
            try { connection.close() } catch (_: Exception) {}
        }
    }

    fun writeSerial(inputText: String, onStatusChanged: (String) -> Unit) {
        val manager = getManager()
        val availableDrivers =
            UsbSerialProber.getDefaultProber().findAllDrivers(manager)
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
            port.setParameters(
                115200,
                8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )
            port.write(inputText.toByteArray(), 2000)

            onStatusChanged("Enviado: $inputText")

        } catch (e: IOException) {
            onStatusChanged("Error: ${e.message}")
        } finally {
            port.close()
            connection.close()
        }
    }
}
