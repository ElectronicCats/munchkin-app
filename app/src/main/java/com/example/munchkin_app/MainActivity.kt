package com.example.munchkin_app

import android.hardware.usb.UsbManager
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {

    val usbHelper = UsbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usbManager = usbHelper.getManager()

        usbHelper.registerReceiver()

        enableEdgeToEdge()
        setContent {
            MunchkinappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(usbManager, usbHelper, Modifier)
                }
            }
        }
    }
}

@Composable
fun Greeting(manager: UsbManager, usbHelper: UsbHelper, modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf("Esperando...") }
    var receivedText by remember { mutableStateOf("") }
    var isButtonPressed by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = statusText)
        Text(text = "Recibido $receivedText")

        // Botón para pedir permiso y detectar dispositivos
        Button(onClick = {
           usbHelper.detectAndGetPermision {newStatus ->
               statusText = newStatus
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
            isButtonPressed = true
        }) {
            Text("Leer el dispositivo")
        }

        // Botón para enviar texto al dispositivo vía serial
        Button(onClick = {
            usbHelper.writeSerial(inputText) {newStatus ->
                statusText = newStatus
                inputText = ""
            }
        }) {
            Text("Enviar al dispositivo")
        }

        if (isButtonPressed) {
            // Lectura continua del serial
            LaunchedEffect(Unit) {
                usbHelper.readSerial { data ->
                    val text = data
                    receivedText = text  // esto actualizará automáticamente el Text de Compose
                }
            }
        }
    }
}
