package com.example.munchkin_app.ui.screens.home.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.munchkin_app.R
import com.example.munchkin_app.viewmodel.usb.UsbViewModel

@Composable
fun UsbDeviceCard(
    viewModel: UsbViewModel = hiltViewModel(),
    status: String,
    usbDevices: List<String>,
    connectionStatus: String,
    selectedDevice: String?
) {
    LaunchedEffect(Unit) {
        viewModel.detectDevices()
    }

    Text(
        text = status,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge
    )

    if (usbDevices.isEmpty()) {
        Log.d("Connection", "No USB devices detected - usbDevices: $usbDevices")
    } else {
        Column {
            usbDevices.forEach { device ->
                Card(
                    border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            if (connectionStatus.contains("Not connected", ignoreCase = true))
                                viewModel.connectDevice(device)
                            else
                                viewModel.disconnectDevice()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (connectionStatus.contains(device) && connectionStatus.startsWith("Connected"))
                            Color(0x4072BA63)
                        else
                            Color(0x40BA6363)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            painter = if (device == selectedDevice)
                                painterResource(R.drawable.icon_close) else painterResource(R.drawable.icon_add),
                            contentDescription = null
                        )
                        Text(
                            text = device,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}