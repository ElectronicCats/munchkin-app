package com.example.munchkin_app.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.screens.home.components.ConnectionOption
import com.example.munchkin_app.ui.screens.home.components.SingleChoiceSegmentedButton
import com.example.munchkin_app.ui.screens.home.components.UsbDeviceCard
import com.example.munchkin_app.viewmodel.UsbViewModel
import minino.rpc.Main

@Composable
fun ConnectionSettingsScreen(
    viewModel: UsbViewModel = hiltViewModel(),
) {
    var selectedOption by remember { mutableStateOf(ConnectionOption.SERIAL) }
    var previousOption by remember { mutableStateOf<ConnectionOption?>(null) }

    val usbDevices by viewModel.usbDevices.collectAsState()
    val status by viewModel.status.collectAsState()
    val version by viewModel.deviceVersion.collectAsState()
    val productName by viewModel.deviceName.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val statusDevice by viewModel.deviceStatus.collectAsState()

    val context = LocalContext.current

    val connectionStatus by viewModel.connectionStatus.collectAsState() // 👈 se observa aquí

    LaunchedEffect(usbDevices) {
        Log.d("Connection", "UI actualizada: usbDevices = $usbDevices, size = ${usbDevices.size}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SingleChoiceSegmentedButton(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = selectedOption,
            onSelectionChanged = { selectedOption = it },
            connectionStatus = connectionStatus
        )

        ProportionalSpacer(0.01f)

        LaunchedEffect(selectedOption) {
            if (previousOption == ConnectionOption.SERIAL && selectedOption != ConnectionOption.SERIAL) {
                viewModel.disconnectDevice()
            }
            previousOption = selectedOption
        }

        when (selectedOption) {
            ConnectionOption.SERIAL -> {
                UsbDeviceCard(
                    viewModel = viewModel,
                    status = status,
                    usbDevices = usbDevices,
                    connectionStatus = connectionStatus,
                    selectedDevice = selectedDevice
                )
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

        Text(
            text = if (connectionStatus.equals("Not Connected", ignoreCase = true))
                ""
            else "Model: $productName\nV$version",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        if (statusDevice == Main.Status.STATUS_OK){
            Toast.makeText(context,
                "Connected Successfully", Toast.LENGTH_SHORT).show()
        } else if (statusDevice == Main.Status.STATUS_ERROR){
            Toast.makeText(context,
                "Connection Error", Toast.LENGTH_SHORT).show()
        }
    }
}
