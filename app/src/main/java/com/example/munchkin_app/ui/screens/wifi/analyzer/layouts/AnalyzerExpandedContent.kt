package com.example.munchkin_app.ui.screens.wifi.analyzer.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.viewmodel.screens.wifi.AnalyzerViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import minino.analyzer.Analyzer

@Composable
fun AnalyzerExpandedContent(
    innerPaddingValues: PaddingValues,
    storageDestination: List<String>,
    selectedDestinationIndex: Int,
    onDestinationChanged: (Int) -> Unit,
    wifiNetworks: List<Analyzer.WifiNetwork>,
    channels: List<String>,
    selectedChannel: String,
    onChannelChanged: (String) -> Unit,
    viewModel: UsbViewModel,
    screenViewModel: AnalyzerViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
    onStopRunning: () -> Unit,
    totalPackets: Int
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val scanChannel by screenViewModel.scanChannel.collectAsState()

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onStopRunning()
                viewModel.stopAnalyzer()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            onStopRunning()
            viewModel.stopAnalyzer()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Panel izquierdo
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(end = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ApplicationTitle(
                    application = "WiFi",
                    applicationName = "Analyzer"
                )

                ProportionalSpacer(0.03f)

                OptionsButtonSegment(
                    names = storageDestination,
                    selectedIndex = selectedDestinationIndex,
                    onSelectionChanged = { index, name ->
                        onDestinationChanged(index)
                        handleDestinationSelection(index, name)
                    },
                    title = "Storage Destination",
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.03f)

                ChannelDropMenu(
                    channels = channels,
                    selectedChannel = selectedChannel,
                    handleChannelSelection = { channel ->
                        onChannelChanged(channel)
                        handleChannelSelection(channel)
                    }
                )

                ProportionalSpacer(0.05f)

                StartButton(
                    text = if (running) "Stop" else "Start",
                    command = {
                        if (running) {
                            viewModel.stopAnalyzer()
                        } else {
                            screenViewModel.clearScanChannel()
                            screenViewModel.setScanChannel(selectedChannel)
                            viewModel.startAnalyzer()
                        }
                        onToggleRunning()
                    }
                )
            }

            // Panel derecho
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),  // ✅ Scroll aquí
                horizontalAlignment = Alignment.Start
            ) {
                InformationLabel(
                    modifier = Modifier.fillMaxSize(),
                    loading = running,
                    information = buildString {
                        when {
                            running -> {
                                appendLine("Scanning WiFi networks...")
                            }
                            wifiNetworks.isEmpty() -> {
                                appendLine("No WiFi networks found.")
                            }
                            else -> {
                                appendLine("Total packets: $totalPackets")
                                appendLine()

                                append(
                                    wifiNetworks.joinToString("\n\n") { net ->
                                        buildString {
                                            appendLine("SSID: ${net.ssid.ifBlank { "(No SSID)" }}")
                                            appendLine(scanChannel ?: "(No channel)")
                                            appendLine("BSSID: ${net.bssid.ifBlank { "(No BSSID)" }}")
                                            appendLine("Destination: ${net.destination.ifBlank { "(No Destination)" }}")
                                            appendLine("Source: ${net.source.ifBlank { "(No Source)" }}")
                                        }
                                    }
                                )
                            }
                        }
                    }
                )

            }
        }
    }
}

private fun handleDestinationSelection(index: Int, name: String) {
    println("Destination selected: $name (Index: $index)")
}

private fun handleChannelSelection(channel: String) {
    println("Channel selected: $channel")
}
