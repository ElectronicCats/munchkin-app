package com.example.munchkin_app.ui.screens.wifi.captive

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.captive.layouts.CaptiveCompactContent
import com.example.munchkin_app.ui.screens.wifi.captive.layouts.CaptiveExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.CaptiveViewModel

@Composable
fun CaptivePortalScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MunchkinScreens.ApplicationLayout(navController, "wifi") { innerPadding ->
            CaptivePortalContents(innerPadding, navController)
        }
    }
}

@Composable
fun CaptivePortalContents(
    innerPadding: PaddingValues,
    navController: NavHostController,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    viewModel: CaptiveViewModel = hiltViewModel()
){
    val mode = remember {
        listOf("Standalone", "Replicate")
    }
    val sdDumping = remember {
        listOf("Dump to SD", "No Dump")
    }
    val channels = remember {
        listOf(
            "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6",
            "Channel 7", "Channel 8", "Channel 9", "Channel 10", "Channel 11", "Channel 12",
            "Channel 13", "Channel 14"
        )
    }

    val selectedModeIndex by viewModel.selectedModeIndex.collectAsState()
    val selectedSdDestinationIndex by viewModel.selectedSdDestinationIndex.collectAsState()
    val selectedChannel by viewModel.selectedChannel.collectAsState()

    // Quita contentAlignment para que no centre y permita scroll
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                CaptiveCompactContent(
                    innerPadding = innerPadding,
                    mode = mode,
                    selectedModeIndex = selectedModeIndex,
                    onSelectionModeChanged = { viewModel.updateModeIndex(it) },
                    sdDumping = sdDumping,
                    selectedSdDestinationIndex = selectedSdDestinationIndex,
                    onSelectedSdIndexChange = { viewModel.updateSdDestinationIndex(it) },
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelSelection = { viewModel.updateChannel(it) },
                    navController = navController
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                CaptiveExpandedContent(
                    innerPadding = innerPadding,
                    mode = mode,
                    selectedModeIndex = selectedModeIndex,
                    onSelectionModeChanged = {viewModel.updateModeIndex(it) },
                    sdDumping = sdDumping,
                    selectedSdDestinationIndex = selectedSdDestinationIndex,
                    onSelectedSdIndexChange = { viewModel.updateSdDestinationIndex(it) },
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelSelection = { viewModel.updateChannel(it) },
                    navController = navController
                )
            }
            // Medium u otro caso (incluye landscape en teléfono)
            else -> {
                CaptiveCompactContent(
                    innerPadding = innerPadding,
                    mode = mode,
                    selectedModeIndex = selectedModeIndex,
                    onSelectionModeChanged = { viewModel.updateModeIndex(it) },
                    sdDumping = sdDumping,
                    selectedSdDestinationIndex = selectedSdDestinationIndex,
                    onSelectedSdIndexChange = { viewModel.updateSdDestinationIndex(it) },
                    channels = channels,
                    selectedChannel = selectedChannel,
                    onChannelSelection = { viewModel.updateChannel(it) },
                    navController = navController
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
fun CaptivePortalScreenPreview() {
    MunchkinappTheme {
        CaptivePortalScreen(rememberNavController())
    }
}

@Preview(
    name = "Phone Landscape Preview",
    showBackground = true,
    widthDp = 840,
    heightDp = 400
)
@Composable
fun CaptivePortalScreenLandscapePreview() {
    MunchkinappTheme {
        CaptivePortalScreen(rememberNavController())
    }
}

@Preview(
    name = "Tablet Preview",
    showBackground = true,
    device = Devices.TABLET
)
@Composable
fun CaptivePortalExpandedScreenPreview() {
    MunchkinappTheme {
        CaptivePortalScreen(rememberNavController())
    }
}