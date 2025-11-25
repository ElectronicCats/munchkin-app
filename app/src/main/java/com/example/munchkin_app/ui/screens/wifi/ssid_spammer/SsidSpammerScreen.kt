package com.example.munchkin_app.ui.screens.wifi.ssid_spammer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding ->
        SsidSpammerContent(innerPadding)
    }
}

@Composable
fun SsidSpammerContent(
    innerPadding: PaddingValues,
    viewModel: UsbViewModel = hiltViewModel(),
    screenViewModel: SsidSpamViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
){
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
                SsidSpammerCompactContent()
            }

            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)  -> {
                SsidSpammerExpandedContent(
                    viewModel = viewModel,
                    screenViewModel = screenViewModel
                )
            }

            else -> {
                SsidSpammerCompactContent()
            }
        }
    }


}

@Preview
@Composable
fun SsidSpammerScreenPreview() {
    MunchkinappTheme {
        SsidSpammerScreen(rememberNavController())
    }

}