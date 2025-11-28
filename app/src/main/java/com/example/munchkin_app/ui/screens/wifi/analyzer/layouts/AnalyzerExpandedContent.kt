package com.example.munchkin_app.ui.screens.wifi.analyzer.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.ui.common.components.OptionsButtonSegment
import com.example.munchkin_app.ui.screens.wifi.analyzer.AnalyzerAction
import com.example.munchkin_app.ui.screens.wifi.analyzer.AnalyzerState
import com.example.munchkin_app.ui.screens.wifi.analyzer.formatWifiNetworks

@Composable
fun AnalyzerExpandedContent(
    state: AnalyzerState,
    action: AnalyzerAction
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    application = stringResource(R.string.wifi_title),
                    applicationName = stringResource(R.string.wifi_analyzer_title_application)
                )

                ProportionalSpacer(0.03f)

                OptionsButtonSegment(
                    names = state.storageDestination,
                    selectedIndex = state.selectedDestinationIndex,
                    disabled = state.running,
                    onSelectionChanged = { index, _ ->
                        action.screenViewModel.updateDestinationIndex(index)
                    },
                    title = stringResource(R.string.wifi_analyzer_storage_destination),
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.03f)

                ChannelDropMenu(
                    channels = state.channels,
                    selectedChannel = state.selectedChannel,
                    disabled = state.running,
                    handleChannelSelection = { channel ->
                        val number = channel.removePrefix("Channel ").toInt()
                        action.viewModel.setChannel(number)
                        action.screenViewModel.updateChannel(channel)
                    },
                )

                ProportionalSpacer(0.05f)

                StartButton(
                    text = if (state.running) stringResource(R.string.analyzer_stop) else stringResource(R.string.analyzer_start),
                    command = {
                        if (state.running) {
                            action.viewModel.stopAnalyzer()
                            action.screenViewModel.updateShowStoppedMessage(true)
                        } else {
                            action.screenViewModel.clearScanChannel()
                            action.screenViewModel.setScanChannel(state.selectedChannel)
                            action.viewModel.startAnalyzer()
                            action.screenViewModel.updateShowStoppedMessage(false)
                        }
                        action.screenViewModel.updateRunning(!state.running)
                    }
                )

                if (state.showStoppedMessage) {
                    ProportionalSpacer(0.02f)

                    Text(
                        text = "Analyzer stopped.\nanalizer.pcap\nStored in SD Card: /apps/analizer/pcaps ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            // Panel derecho
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight(),  // ✅ Scroll aquí
                horizontalAlignment = Alignment.Start
            ) {
                InformationLabel(
                    modifier = Modifier,
                    loading = state.running,
                    loadingText = "Scanning...\n${state.totalPackets} Packets"
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = buildString {
                            when {
                                state.running -> {
                                    appendLine(stringResource(R.string.wifi_analyzer_scanning_networks))
                                }
                                state.wifiNetworks.isEmpty() -> {
                                    appendLine(stringResource(R.string.wifi_analyzer_no_networks))
                                }
                                else -> {
                                    appendLine(stringResource(R.string.analyzer_total_packets, state.totalPackets))
                                    appendLine()

                                    append(formatWifiNetworks(state.context, state.wifiNetworks, state.scanChannel))
                                }
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}
