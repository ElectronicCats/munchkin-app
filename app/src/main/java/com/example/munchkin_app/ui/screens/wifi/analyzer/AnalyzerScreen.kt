package com.example.munchkin_app.ui.screens.wifi.analyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerCompactContent
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerExpandedContent
import com.example.munchkin_app.viewmodel.screens.wifi.AnalyzerViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import com.google.protobuf.value
import android.content.Context
import minino.analyzer.Analyzer


@Composable
fun AnalyzerScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MunchkinScreens.ApplicationLayout(navRouteBack = "wifi", navController = navController) {innerPadding ->
            AnalyzerContent(innerPadding)
        }
    }
}

@Composable
fun AnalyzerContent(
    innerPaddingValues: PaddingValues,
    viewModel: UsbViewModel = hiltViewModel(),
    screenViewModel: AnalyzerViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
){
    val wifiNetworks by viewModel.wifiNetworks.collectAsState()
    val totalPackets by viewModel.totalPackets.collectAsState()

    val sdCard = stringResource(R.string.wifi_analyzer_sd_card)
    val internal = stringResource(R.string.wifi_analyzer_internal)

    val storageDestination = remember { listOf(sdCard, internal) }

    val selectedDestinationIndex by screenViewModel.selectedDestinationIndex.collectAsState()
    var running by remember { mutableStateOf(false)}

    val channelsArray = stringArrayResource(R.array.wifi_analyzer_channels).toList()
    // Lista de canales para el dropdown
    val channels = remember {
        channelsArray
    }
    val selectedChannel by screenViewModel.selectedChannel.collectAsState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                AnalyzerCompactContent(
                    innerPaddingValues = innerPaddingValues,
                    storageDestination = storageDestination,
                    selectedDestinationIndex = selectedDestinationIndex,
                    onDestinationChanged = { screenViewModel.updateDestinationIndex(it) },
                    wifiNetworks = wifiNetworks,
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelChanged = { screenViewModel.updateChannel(it) },
                    running = running,
                    onStopRunning = {running = false},
                    onToggleRunning = { running = !running },
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    totalPackets = totalPackets
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                AnalyzerExpandedContent(
                    innerPaddingValues = innerPaddingValues,
                    storageDestination = storageDestination,
                    selectedDestinationIndex = selectedDestinationIndex,
                    onDestinationChanged = { screenViewModel.updateDestinationIndex(it)},
                    wifiNetworks = wifiNetworks,
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelChanged = { screenViewModel.updateChannel(it) },
                    running = running,
                    onToggleRunning = { running = !running },
                    onStopRunning = {running = false},
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    totalPackets = totalPackets
                )
            }
            // Medium u otro caso
            else -> {
                AnalyzerCompactContent(
                    innerPaddingValues = innerPaddingValues,
                    storageDestination = storageDestination,
                    selectedDestinationIndex = selectedDestinationIndex,
                    onDestinationChanged = { screenViewModel.updateDestinationIndex(it) },
                    wifiNetworks = wifiNetworks,
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelChanged = { screenViewModel.updateChannel(it) },
                    running = running,
                    onStopRunning = {running = false},
                    onToggleRunning = { running = !running },
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    totalPackets = totalPackets
                )
            }
        }
    }
}

fun formatWifiNetworks(
    context: Context,
    wifiNetworks: List<Analyzer.WifiNetwork>,
    scanChannel: String?
): String {
    // 1. Obtener la plantilla de string del XML
    val template = context.getString(R.string.wifi_network_template)

    // 2. Usar joinToString para procesar la lista
    return wifiNetworks.joinToString("\n\n") { net ->
        // 3. Aplicar la lógica de manejo de nulos/vacíos
        val ssid = net.ssid.ifBlank { "(No SSID)" }
        val channel = scanChannel ?: "(No channel)"
        val bssid = net.bssid.ifBlank { "(No BSSID)" }
        val destination = net.destination.ifBlank { "(No Destination)" }
        val source = net.source.ifBlank { "(No Source)" }

        // 4. Formatear la plantilla con los valores dinámicos
        // Usamos String.format() con la plantilla XML y los 5 argumentos
        String.format(
            template,
            ssid,
            channel,
            bssid,
            destination,
            source
        )
    }
}
/*
@Preview(
    name = "Phone Preview",
    showBackground = true,
    device = Devices.PHONE
)
@Composable
fun AnalyzerPreview(){
    MunchkinappTheme {
        AnalyzerScreen(rememberNavController())
    }
}

@Preview(
    name = "Tablet Preview",
    showBackground = true,
    device = Devices.TABLET
)
@Composable
fun AnalyzerExpandedPreview(){
    MunchkinappTheme {
        AnalyzerScreen(rememberNavController())
    }
}
*/