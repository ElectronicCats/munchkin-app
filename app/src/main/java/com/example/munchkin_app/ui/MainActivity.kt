package com.example.munchkin_app.ui

import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.util.concurrent.Executors
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.screens.HomeScreen
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val ACTION_USB_PERMISSION = "com.example.munchkin_app.USB_PERMISSION"

    private var port: UsbSerialPort? = null
    private var connection: UsbDeviceConnection? = null
    private var ioManager: SerialInputOutputManager? = null


    private lateinit var usbManager: UsbManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        // Registrar receiver una sola vez
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(usbReceiver, filter)

        enableEdgeToEdge()
        setContent {
            MunchkinappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(usbManager, ACTION_USB_PERMISSION, Modifier)
                }
            }
        }
    }
}

@Composable
fun Greeting(manager: UsbManager, actionUsbPermission: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Esperando...") }
    var receivedText by remember { mutableStateOf("") }

    val executor = remember { Executors.newSingleThreadExecutor() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = statusText)
        Text(text = "Recibido $receivedText")

        // Botón para pedir permiso y detectar dispositivos
        Button(onClick = {
            val devices = manager.deviceList
            val device = devices.values.firstOrNull()
            if (device == null) {
                statusText = "No hay dispositivos conectados"
                return@Button
            }

            if (!manager.hasPermission(device)) {
                // pedir permiso solo si no lo tenemos
                val permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    Intent(actionUsbPermission),
                    PendingIntent.FLAG_IMMUTABLE
                )
                manager.requestPermission(device, permissionIntent)
                statusText = "Pidiendo permiso..."
            } else {
                statusText = "Ya tienes permiso para ${device.deviceName}"
            }
        }) {
            Text("Detectar / Pedir permiso")
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Texto a enviar") }
        )

        //Boton para leer texto de dispositivo serial
        Button(onClick = {
            val availableDrivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) {
                statusText = "No hay drivers USB"
                return@Button
            }

            val driver = availableDrivers[0]
            val connection = manager.openDevice(driver.device)
            if (connection == null) {
                statusText = "No se pudo abrir el dispositivo. ¿Permiso concedido?"
                return@Button
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
                port.read(bytes, 0)

                statusText = "Serial recibido:${bytes.toString(Charsets.UTF_8)}"

                port.close()
                connection.close()
            } catch (e: IOException) {
                statusText = "Error: ${e.message}"
            }
        }) {
            Text("Leer el dispositivo")
        }

        // Botón para enviar texto al dispositivo vía serial
        Button(onClick = {
            val availableDrivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(manager)
            if (availableDrivers.isEmpty()) {
                statusText = "No hay drivers USB"
                return@Button
            }

            val driver = availableDrivers[0]
            val connection = manager.openDevice(driver.device)
            if (connection == null) {
                statusText = "No se pudo abrir el dispositivo. ¿Permiso concedido?"
                return@Button
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

                statusText = "Enviado: $inputText"
                inputText = ""

                port.close()
                connection.close()
            } catch (e: IOException) {
                statusText = "Error: ${e.message}"
            }
        }) {
            Text("Enviar al dispositivo")
        }
    }
}
