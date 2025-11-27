package com.example.munchkin_app.ui.screens.wifi.ssid_spammer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts.SsidSpammerCompactContent
import com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts.SsidSpammerExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.SsidSpamViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel


@Composable
fun SsidSpammerScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(
        navController = navController,
        navRouteBack = "wifi",
    ) {innerPadding, hostState ->
        SsidSpammerContent(innerPadding, hostState)
    }
}

@Composable
fun SsidSpammerContent(
    innerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState,
    viewModel: UsbViewModel = hiltViewModel(),
    screenViewModel: SsidSpamViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
){
    val ssidListTitle by screenViewModel.ssidListTitle.collectAsState()
    val ssidText by screenViewModel.ssidText.collectAsState()
    val running by screenViewModel.running.collectAsState()
    val ssidIsSelected by screenViewModel.ssidIsSelected.collectAsState()
    val allConfigs by screenViewModel.allConfigs.collectAsState()
    val configNames = allConfigs.configs.keys.toList()
    val listToSpam: List<String> by screenViewModel.listToSpam.collectAsState()

    val scope = rememberCoroutineScope()

    var selectedListName by remember {
        mutableStateOf(configNames.firstOrNull() ?: "Pick a List")
    }

    if (allConfigs.configs.isEmpty()) screenViewModel.updateSsidIsSelected(false)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {

        when {
            //Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                SsidSpammerCompactContent(
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)  -> {
                SsidSpammerExpandedContent(
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    snackbarHostState = snackbarHostState
                )
            }

            else -> {
                SsidSpammerCompactContent(
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

data class SsidSpammerState(
    val ssidListTitle: String,
    val ssidText: String,
    val running: Boolean,
    val ssidIsSelected: Boolean,
    val configNames: List<String>,
    val listToSpam: List<String>,
    val selectedListName: String
)

data class SsidSpammerActions(
    val viewModel: UsbViewModel,
    val screenViewModel: SsidSpamViewModel,
    val snackbarHostState: SnackbarHostState,
    val onSave: () -> Unit,
    val onDelete: (String) -> Unit,
    val onStartStop: () -> Unit,
    val updateSelectedListName: (String) -> Unit
)

@Preview
@Composable
fun SsidSpammerScreenPreview() {
    MunchkinappTheme {
        SsidSpammerScreen(rememberNavController())
    }

}