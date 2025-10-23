package com.example.munchkin_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.viewmodel.UsbViewModel


//This screen was made to test protobuf and UsbHelper
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbControlUI(viewModel: UsbViewModel) {
    val logs =viewModel.logs
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Munchkin USB Control") },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.detectAndGetPermission() }) {
                        Text("Detectar USB")
                    }
                    Button(onClick = { viewModel.readSerial() }) {
                        Text("Iniciar Lectura")
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.startCounter() }) {
                        Text("Start Counter")
                    }
                    Button(onClick = { viewModel.stopCounter() }) {
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