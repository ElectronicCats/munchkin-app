package com.example.munchkin_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var statusText by remember { mutableStateOf("Esperando...") }
    var receivedText by remember { mutableStateOf("") }
    var isReading by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

    val mainHandler = Handler(Looper.getMainLooper())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Título
        Text(
            text = "🎮 Control USB Munchkin",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // --- SECCIÓN DE CONEXIÓN ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isConnected) "✅ Conectado" else "⚠️ Desconectado",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = {
                        usbHelper.detectAndGetPermission { newStatus ->
                            mainHandler.post { statusText = newStatus }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🔌 Detectar / Pedir permiso")
                }

                Button(
                    onClick = {
                        if (!isReading) {
                            isReading = true
                            isConnected = true
                            usbHelper.readSerial { msg ->
                                mainHandler.post {
                                    receivedText = msg
                                    statusText = msg
                                }
                            }
                            statusText = "Leyendo..."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isReading
                ) {
                    Text("📖 Iniciar Lectura")
                }

                Button(
                    onClick = {
                        if (isReading) {
                            usbHelper.stopReading()
                            isReading = false
                            isConnected = false
                            statusText = "Lectura detenida"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isReading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("⏹️ Detener Lectura")
                }
            }
        }

        Divider()

        // --- SECCIÓN DE COMANDOS LED ---
        Text(
            text = "💡 Control de LED",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    usbHelper.sendLedOn { msg ->
                        mainHandler.post { statusText = msg }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isConnected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("💡 ON")
            }

            Button(
                onClick = {
                    usbHelper.sendLedOff { msg ->
                        mainHandler.post { statusText = msg }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isConnected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("🌑 OFF")
            }
        }

        Button(
            onClick = {
                usbHelper.sendToggleLed { msg ->
                    mainHandler.post { statusText = msg }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isConnected,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("🔄 Toggle LED")
        }

        Divider()

        // --- SECCIÓN DE CONTROL DE CONTADOR ---
        Text(
            text = "🔢 Control de Contador",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    usbHelper.startCounter { msg ->
                        mainHandler.post { statusText = msg }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isConnected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("▶️ START")
            }

            Button(
                onClick = {
                    usbHelper.stopCounter { msg ->
                        mainHandler.post { statusText = msg }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = isConnected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("⏹️ STOP")
            }
        }

        Button(
            onClick = {
                usbHelper.getCounterValue { msg ->
                    mainHandler.post { statusText = msg }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isConnected
        ) {
            Text("📊 Obtener Valor")
        }

        Divider()

        // --- SECCIÓN DE DEBUG ---
        Text(
            text = "🧪 Debug",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- PANEL DE LOG ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "📋 Estado:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall
                )

                if (receivedText.isNotEmpty() && receivedText != statusText) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📥 Último recibido:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = receivedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}