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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerCompactContent
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.AnalyzerViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel


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

    val storageDestination = remember {
        listOf("SD Card", "Internal")
    }
    val selectedDestinationIndex by screenViewModel.selectedDestinationIndex.collectAsState()
    var running by remember { mutableStateOf(false)}

    // Lista de canales para el dropdown
    val channels = remember {
        listOf(
            "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6",
            "Channel 7", "Channel 8", "Channel 9", "Channel 10", "Channel 11", "Channel 12",
            "Channel 13", "Channel 14"
        )
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
                    onToggleRunning = { running = !running },
                    viewModel = viewModel,
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
                    viewModel = viewModel,
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
                    onToggleRunning = { running = !running },
                    viewModel = viewModel,
                    totalPackets = totalPackets
                )
            }
        }
    }
}

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