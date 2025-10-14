package com.example.munchkin_app

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.munchkin_app.ui.theme.MunchkinappTheme

class MainActivity : ComponentActivity() {

    private lateinit var usbHelper: UsbHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbHelper = UsbHelper(this)
        usbHelper.registerReceiver() // Escucha permisos USB

        enableEdgeToEdge()
        setContent {
            MunchkinappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsbScreen(usbHelper)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usbHelper.stopReading()
        usbHelper.unregisterUsbReceiver()
    }
}

@Composable
fun UsbScreen(usbHelper: UsbHelper) {
    var inputText by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Esperando...") }
    var receivedText by remember { mutableStateOf("") }
    var isReading by remember { mutableStateOf(false) }

    val mainHandler = Handler(Looper.getMainLooper())

    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = statusText)
        Text(text = "Recibido: $receivedText")

        Button(onClick = {
            usbHelper.detectAndGetPermission { newStatus ->
                statusText = newStatus
            }
        }) {
            Text("Detectar / Pedir permiso")
        }

        Button( onClick = {
            if (!isReading) {
                isReading = true
                usbHelper.readSerial { msg ->
                    mainHandler.post { receivedText = msg } // usando Handler
                }
                statusText = "Leyendo..."
            }
        }) {
            Text("Leer dispositivo")
        }

        Button(onClick = {
            if (isReading) {
                usbHelper.stopReading()
                isReading = false
                statusText = "Lectura detenida"
            }
        }) {
            Text("Detener lectura")
        }


        Button(onClick = {
            if (isReading) {
                usbHelper.sendPing{}
            }
        }) {
            Text("Hacer ping")
        }

        Button(onClick = {
            if (isReading) {
                usbHelper.sendLedOn {}
            }
        }) {
            Text("Encender LED")
        }

        Button(onClick = {
            if (isReading) {
                usbHelper.sendLedOff{}
            }
        }) {
            Text("Apagar LED")
        }
    }
}


