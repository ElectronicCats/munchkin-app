package com.example.munchkin_app.ui.screens.wifi.deauth

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthCompactContent
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel

@Composable
fun DeauthScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding ->
        DeauthContents(innerPadding)
    }
}

@Composable
fun DeauthContents(
    innerPadding: PaddingValues,
    viewModel: UsbViewModel = hiltViewModel(),
    screenViewModel: DeauthViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val typeOfAttack = remember {
        listOf("Broadcast", "Rogue AP", "Combined")
    }

    val attackIndex by screenViewModel.attackIndex.collectAsState()

    val running by screenViewModel.running.collectAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                DeauthCompactContent(
                    innerPadding = innerPadding,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = {screenViewModel.updateRunning(!running)},
                    onStopRunning = {screenViewModel.updateRunning(false)},
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                DeauthExpandedContent(
                    innerPadding = innerPadding,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = {screenViewModel.updateRunning(!running)},
                    onStopRunning = {screenViewModel.updateRunning(false)},
                )
            }
            // Medium u otro caso
            else -> {
                DeauthCompactContent(
                    innerPadding = innerPadding,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = {screenViewModel.updateRunning(!running)},
                    onStopRunning = {screenViewModel.updateRunning(false)},
                )
            }
        }
    }
}





@Preview
@Composable
fun DeauthScreenPreview() {
    MunchkinappTheme {
        DeauthScreen(rememberNavController())
    }

}