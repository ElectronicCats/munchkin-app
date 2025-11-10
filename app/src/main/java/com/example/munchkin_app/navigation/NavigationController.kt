package com.example.munchkin_app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.screens.ble.BleScreen
import com.example.munchkin_app.ui.screens.home.HomeScreen
import com.example.munchkin_app.ui.screens.iot.IotScreen
import com.example.munchkin_app.ui.screens.scripts.ScriptsScreen
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton
import com.example.munchkin_app.ui.screens.wifi.WifiScreen
import com.example.munchkin_app.ui.screens.wifi.analyzer.AnalyzerScreen
import com.example.munchkin_app.ui.screens.wifi.captive.CaptivePortalScreen
import com.example.munchkin_app.ui.screens.wifi.captive.CaptiveProcessScreen
import com.example.munchkin_app.viewmodel.UsbViewModel


@Composable
fun MainNavController(viewModel: UsbViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome",
    ) {
        //Welcome
        composable(
            route = "welcome",
            enterTransition = NavigationTransitions.fadeSlideIn,
            exitTransition = NavigationTransitions.fadeSlideOut
        ) { OnboardingPagerWithButton(navController = navController) }

        //Bottom Navigation Bar
        composable(Destination.HOME.route) {HomeScreen(navController = navController)}
        composable(Destination.WIFI.route) { WifiScreen(navController = navController) }
        composable(Destination.BLE.route) { BleScreen(navController = navController) }
        composable(Destination.IOT.route) { IotScreen(navController = navController) }
        composable(Destination.SCRIPT.route) { ScriptsScreen(navController = navController) }
        //Wifi Applications
        composable("analyzer") { AnalyzerScreen(navController = navController) }
        composable("captive") { CaptivePortalScreen(navController = navController) }
        composable("captiveProcess") { CaptiveProcessScreen(navController = navController) }
    }
}

enum class Destination(
    val route: String,
    val labelResId: Int,
    val icon: Int,
    val contentDescription: Int
) {
    HOME("home", R.string.NavBar_home, R.drawable.icon_home, R.string.NavBar_icon_home),
    WIFI("wifi", R.string.NavBar_wifi, R.drawable.icon_wifi, R.string.NavBar_icon_wifi),
    BLE("ble", R.string.NavBar_ble, R.drawable.icon_bluetooth, R.string.NavBar_icon_ble),
    IOT("iot", R.string.NavBar_iot, R.drawable.icon_iot, R.string.NavBar_icon_iot),
    SCRIPT("script", R.string.NavBar_scripts, R.drawable.icon_terminal, R.string.NavBar_icon_scripts)
}


@Preview
@Composable
fun GetNavControllerPreview() {
    MainNavController(viewModel())
}