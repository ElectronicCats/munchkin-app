package com.example.munchkin_app.ui.screens.home.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.viewmodel.screens.home.HomeViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import minino.rpc.Main

@Composable
fun ConnectionSettingsScreen(
    viewModel: UsbViewModel = hiltViewModel(),
    screenViewModel: HomeViewModel = hiltViewModel()
) {
    val selectedOption by screenViewModel.selectedOption.collectAsState()
    val previousOption by screenViewModel.previousOption.collectAsState()

    val usbDevices by viewModel.usbDevices.collectAsState()
    val status by viewModel.status.collectAsState()
    val deviceVersion by viewModel.deviceVersion.collectAsState()
    val deviceName by viewModel.deviceName.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val deviceStatus by viewModel.deviceStatus.collectAsState()
    val deviceCounterId by viewModel.deviceCounterId.collectAsState()

    val context = LocalContext.current

    val connectionStatus by viewModel.connectionStatus.collectAsState() // 👈 se observa aquí

    val safeDeviceName = deviceName ?: stringResource(R.string.home_loading_name)
    val safeDeviceVersion = deviceVersion ?: stringResource(R.string.home_null_version)
    val safeDeviceCounterId = deviceCounterId ?: stringResource(R.string.home_toast_not_connected_default)
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
            onSelectionChanged = { newOption -> screenViewModel.updateSelectedOption(newOption)},
            connectionStatus = connectionStatus
        )

        ProportionalSpacer(0.01f)

        LaunchedEffect(selectedOption) {
            if (previousOption == ConnectionOption.SERIAL && selectedOption != ConnectionOption.SERIAL) {
                viewModel.disconnectDevice()
            }
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
                stringResource(R.string.home_model_version_empty)
            else stringResource(R.string.home_model_version_data, safeDeviceName, safeDeviceVersion),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        if (deviceStatus == Main.Status.STATUS_OK){
            Toast.makeText(context,
                stringResource(
                    R.string.home_connected_successfully_id,safeDeviceCounterId),
                Toast.LENGTH_SHORT).show()
        } else if (deviceStatus == Main.Status.STATUS_ERROR){
            Toast.makeText(context,
                stringResource(R.string.home_toast_connection_error), Toast.LENGTH_SHORT).show()
        }
    }
}
