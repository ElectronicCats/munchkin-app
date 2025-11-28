package com.example.munchkin_app.ui.screens.wifi.analyzer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerCompactContent
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerExpandedContent
import com.example.munchkin_app.viewmodel.screens.wifi.AnalyzerViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
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
        MunchkinScreens.ApplicationLayout(navRouteBack = "wifi", navController = navController) {innerPadding, _ ->
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
    val running by screenViewModel.running.collectAsState()
    val channelsArray = stringArrayResource(R.array.wifi_analyzer_channels).toList()
    val channels = remember {
        channelsArray
    }
    val selectedChannel by screenViewModel.selectedChannel.collectAsState()
    val scanChannel by screenViewModel.scanChannel.collectAsState()
    val showStoppedMessage by screenViewModel.showStoppedMessage.collectAsState()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                screenViewModel.updateRunning(false)
                screenViewModel.updateShowStoppedMessage(true)
                viewModel.stopAnalyzer()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            screenViewModel.updateRunning(false)
            screenViewModel.updateShowStoppedMessage(true)
            viewModel.stopAnalyzer()
        }
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPaddingValues)
            .fillMaxSize(),
    ) {
        val state = AnalyzerState(
            wifiNetworks = wifiNetworks,
            totalPackets = totalPackets,
            selectedChannel = selectedChannel,
            selectedDestinationIndex = selectedDestinationIndex,
            storageDestination = storageDestination,
            channels = channels,
            running = running,
            scanChannel = scanChannel,
            context = context,
            showStoppedMessage = showStoppedMessage
        )

        val action = AnalyzerAction(
            viewModel = viewModel,
            screenViewModel = screenViewModel
        )

        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                AnalyzerCompactContent(
                    state = state,
                    action = action,
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                AnalyzerExpandedContent(
                    state = state,
                    action = action,
                )
            }
            // Medium u otro caso
            else -> {
                AnalyzerCompactContent(
                    state = state,
                    action = action,
                )
            }
        }
    }
}

data class AnalyzerState (
    val wifiNetworks: List<Analyzer.WifiNetwork>,
    val channels: List<String>,
    val selectedChannel: String,
    val running: Boolean,
    val storageDestination: List<String>,
    val selectedDestinationIndex: Int,
    val totalPackets: Int,
    val scanChannel: String?,
    val context: Context,
    val showStoppedMessage: Boolean
)

data class AnalyzerAction(
    val viewModel: UsbViewModel,
    val screenViewModel: AnalyzerViewModel,
)

fun formatWifiNetworks(
    context: Context,
    wifiNetworks: List<Analyzer.WifiNetwork>,
    scanChannel: String?
): String {

    val template = context.getString(R.string.wifi_network_template)


    return wifiNetworks.joinToString("\n\n") { net ->

        val ssid = net.ssid.ifBlank { "(No SSID)" }
        val channel = scanChannel ?: "(No channel)"
        val bssid = net.bssid.ifBlank { "(No BSSID)" }
        val destination = net.destination.ifBlank { "(No Destination)" }
        val source = net.source.ifBlank { "(No Source)" }

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