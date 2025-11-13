package com.example.munchkin_app.ui.screens.wifi.analyzer.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import minino.analyzer.Analyzer

@Composable
fun AnalyzerCompactContent(
    innerPaddingValues: PaddingValues,
    storageDestination: List<String>,
    selectedDestinationIndex: Int,
    onDestinationChanged: (Int) -> Unit,
    wifiNetworks: List<Analyzer.WifiNetwork>,
    channels: List<String>,
    selectedChannel: String,
    onChannelChanged: (String) -> Unit,
    viewModel: UsbViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
    totalPackets: Int
){
    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        ApplicationTitle(
            application = "WiFi",
            applicationName = "Analyzer"
        )

        ProportionalSpacer(0.03f)

        // Selector de destino usando lista de strings
        OptionsButtonSegment(
            names = storageDestination,
            selectedIndex = selectedDestinationIndex,
            onSelectionChanged = { index, name ->
                onDestinationChanged(index)
                handleDestinationSelection(index, name)
            },
            title = "Storage Destination",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProportionalSpacer(0.02f)

        ChannelDropMenu(
            channels = channels,
            selectedChannel = selectedChannel,
            handleChannelSelection = { channel ->
                val number = channel.removePrefix("Channel ").toInt()
                viewModel.setChannel(number)
                onChannelChanged(channel)
                handleChannelSelection(channel)
            }
        )

        ProportionalSpacer(0.02f)

        InformationLabel(
            modifier = Modifier,
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
                                    appendLine(selectedChannel)
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

        ProportionalSpacer(0.05f)

        if (running) {
            StartButton(
                text = "Stop",
                command = { viewModel.stopAnalyzer(); onToggleRunning() }
            )
        } else {
            StartButton(
                text = "Start",
                command = { viewModel.startAnalyzer(); onToggleRunning()}
            )
        }
    }
}

private fun handleDestinationSelection(index: Int, name: String) {
    println("Destination selected: $name (Index: $index)")
}

private fun handleChannelSelection(channel: String) {
    println("Channel selected: $channel")
}
