package com.example.munchkin_app.ui.screens.wifi.deauth

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val floatingSize = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 56.dp
        Configuration.ORIENTATION_PORTRAIT -> 45.dp
        else -> 56.dp
    }

    val floatingX = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 40.dp
        Configuration.ORIENTATION_PORTRAIT -> 55.dp
        else -> 56.dp
    }

    val typeOfAttack = remember {
        listOf("Broadcast", "Rogue AP", "Combined")
    }

    val attackIndex by screenViewModel.attackIndex.collectAsState()

    var running by remember { mutableStateOf(false) }

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
                    floatingSize = floatingSize,
                    floatingX = floatingX,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = { running = !running },
                    onStopRunning = {running = false},
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
                    onToggleRunning = { running = !running },
                    onStopRunning = {running = false},
                )
            }
            // Medium u otro caso
            else -> {
                DeauthCompactContent(
                    innerPadding = innerPadding,
                    floatingSize = floatingSize,
                    floatingX = floatingX,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    viewModel = viewModel,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = { running = !running },
                    onStopRunning = {running = false},
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