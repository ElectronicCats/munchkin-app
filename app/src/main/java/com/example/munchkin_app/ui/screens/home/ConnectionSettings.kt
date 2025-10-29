package com.example.munchkin_app.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.munchkin_app.data.usb.UsbHelper
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.viewmodel.UsbViewModel

enum class ConnectionOption(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    SERIAL("Serial", Icons.Default.Add, "Serial connection"),
    BLUETOOTH("Bluetooth", Icons.Default.AccountBox, "Bluetooth connection"),
    NETWORK("Network", Icons.Default.Build, "Network connection")
}


@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    selectedOption: ConnectionOption,
    onSelectionChanged: (ConnectionOption) -> Unit,
    isConnected: String
) {

    Text(
        text = isConnected,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )

    SingleChoiceSegmentedButtonRow (
        space = -4.dp,
        modifier = modifier
    ){
        ConnectionOption.entries.forEachIndexed { index, option ->
            SegmentedButton(modifier = Modifier.weight(1f),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0XFF72BA63),
                    activeContentColor = Color.White,
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = ConnectionOption.entries.size
                ),
                onClick = { onSelectionChanged(option) },
                selected = option == selectedOption,
                label = { Text(option.label) },
                icon = {
                    Icon(
                        option.icon,
                        contentDescription = option.contentDescription
                    )
                }
            )
        }
    }
}

@Composable
fun ConnectionSettingsScreen(
    viewModel: UsbViewModel = hiltViewModel(),
) {
    var selectedOption by remember { mutableStateOf(ConnectionOption.SERIAL) }
    var previousOption by remember { mutableStateOf<ConnectionOption?>(null) }

    val usbDevices by viewModel.usbDevices.collectAsState()
    val status by viewModel.status.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()

    var isConnected = selectedDevice?.let { "Connected to $it" } ?: "Not Connected"

    // Agrega logs para confirmar que la UI observa cambios
    LaunchedEffect(usbDevices) {
        Log.d("Connection", "UI actualizada: usbDevices = $usbDevices, size = ${usbDevices.size}")
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SingleChoiceSegmentedButton(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = selectedOption,
            onSelectionChanged = { selectedOption = it },
            isConnected = isConnected
        )

        ProportionalSpacer(0.01f)

        LaunchedEffect(selectedOption) {
            if (previousOption == ConnectionOption.SERIAL && selectedOption != ConnectionOption.SERIAL) {
                isConnected = "Not Connected"
                viewModel.disconnectDevice()
            }
            previousOption = selectedOption
        }

        when (selectedOption) {
            ConnectionOption.SERIAL -> {
                LaunchedEffect(Unit) {
                    viewModel.detectDevices()
                }
                Text(status)

                if (usbDevices.isEmpty()) {
                    Log.d("Connection", "Mostrando 'No USB devices detected' - usbDevices: $usbDevices")
                    Text("No USB devices detected")
                } else {
                    Log.d("Connection", "Mostrando lista con ${usbDevices.size} dispositivos - usbDevices: $usbDevices")
                    Column (){
                        usbDevices.forEach { device ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        isConnected = viewModel.connectDevice(device)
                                               },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (device == selectedDevice)
                                        Color(0x4072BA63)
                                    else Color(0x40BA6363)
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (device == selectedDevice)
                                            Icons.Default.Add else Icons.Default.Close,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = device,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            ConnectionOption.BLUETOOTH -> {
                Column {
                    Text("Bluetooth settings")
                    Text("Scanning for devices...")
                }
            }
            ConnectionOption.NETWORK -> {
                Column {
                    Text("Network settings")
                    Text("SSID: Munchkin_Network")
                }
            }
        }
    }
}