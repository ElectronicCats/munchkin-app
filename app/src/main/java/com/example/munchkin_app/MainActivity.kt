package com.example.munchkin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.munchkin_app.UsbHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val usbViewModel: UsbViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbViewModel.registerReceiver()

        setContent {
            UsbControlUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usbViewModel.unregisterReceiver()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UsbControlUI(viewModel: UsbViewModel = hiltViewModel()) {
        val logs =viewModel.logs
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
                        Button(onClick = { usbViewModel.detectAndGetPermission() }) {
                            Text("Detectar USB")
                        }
                        Button(onClick = { usbViewModel.readSerial() }) {
                            Text("Iniciar Lectura")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { usbViewModel.startCounter() }) {
                            Text("Start Counter")
                        }
                        Button(onClick = { usbViewModel.stopCounter() }) {
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
