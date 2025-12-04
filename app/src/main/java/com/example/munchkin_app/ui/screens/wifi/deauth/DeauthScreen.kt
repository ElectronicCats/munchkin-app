package com.example.munchkin_app.ui.screens.wifi.deauth

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthCompactContent
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import minino.deauth.Deauth

@Composable
fun DeauthScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding, _ ->
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
    val attackDescriptions = listOf(
        "Disrupts communication between routers and devices.",
        "Access point installed on a network without authorization.",
        "Merges Broadcast and Rogue AP attacks."
    )
    val attackIndex by screenViewModel.attackIndex.collectAsState()
    val running by screenViewModel.running.collectAsState()
    val networks by viewModel.deauthNetworks.collectAsState()
    val selectedNetwork by screenViewModel.selectedNetwork.collectAsState()
    val networkIsSelected by screenViewModel.isNetworkSelected.collectAsState()

    val scanAttempted by screenViewModel.scanAttempted.collectAsState()
    val scanLoading = scanAttempted && networks.isEmpty()


    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                screenViewModel.updateRunning(false)
                viewModel.deauthStopAttackRequest()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            screenViewModel.updateRunning(false)
            viewModel.deauthStopAttackRequest()
        }
    }

    val state = DeauthState(
        typeOfAttack = typeOfAttack,
        attackIndex = attackIndex,
        running = running,
        networks = networks,
        selectedNetwork = selectedNetwork,
        networkIsSelected = networkIsSelected,
        scanAttempted = scanAttempted,
        scanLoading = scanLoading,
        attackDescriptions = attackDescriptions
    )

    val action = DeauthActions(
        viewModel = viewModel,
        screenViewModel = screenViewModel
    )


    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .fillMaxSize(),
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                DeauthCompactContent(
                    state = state,
                    action = action,
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                DeauthExpandedContent(
                    state = state,
                    action = action,
                )
            }
            // Medium u otro caso
            else -> {
                DeauthCompactContent(
                    state = state,
                    action = action,
                )
            }
        }
    }
}

data class DeauthState(
    val typeOfAttack: List<String>,
    val attackIndex: Int,
    val running: Boolean,
    val networks: List<Deauth.DeauthAP>,
    val selectedNetwork: String,
    val networkIsSelected: Boolean,
    val scanAttempted: Boolean,
    val scanLoading: Boolean,
    val attackDescriptions: List<String>,
)

data class DeauthActions (
    val viewModel: UsbViewModel,
    val screenViewModel: DeauthViewModel,
)



@Preview
@Composable
fun DeauthScreenPreview() {
    MunchkinappTheme {
        DeauthScreen(rememberNavController())
    }

}