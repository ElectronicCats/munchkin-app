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
import com.example.munchkin_app.ui.theme.UsbHelper
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

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
