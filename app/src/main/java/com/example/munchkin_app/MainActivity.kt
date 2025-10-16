package com.example.munchkin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.UsbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var usbHelper: UsbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbHelper = UsbHelper(this)


        usbHelper.registerReceiver()


        setContent {
            UsbControlUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usbHelper.unregisterUsbReceiver()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UsbControlUI() {
        var logs by remember { mutableStateOf(listOf<String>()) }
        val scope = rememberCoroutineScope()

        fun appendLog(text: String) {
            scope.launch(Dispatchers.Main) {
                logs = logs + text
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Munchkin USB Control") })
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { usbHelper.detectAndGetPermission(::appendLog) }) {
                            Text("Detectar USB")
                        }
                        Button(onClick = { usbHelper.readSerial(::appendLog) }) {
                            Text("Iniciar Lectura")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { usbHelper.sendLedCommand(true, ::appendLog) }) {
                            Text("LED ON")
                        }
                        Button(onClick = { usbHelper.sendLedCommand(false, ::appendLog) }) {
                            Text("LED OFF")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { usbHelper.startCounter(::appendLog) }) {
                            Text("Start Counter")
                        }
                        Button(onClick = { usbHelper.stopCounter(::appendLog) }) {
                            Text("Stop Counter")
                        }
                    }

                    Text("Logs:", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(text = log)
                        }
                    }
                }
            }
        )
    }
}
