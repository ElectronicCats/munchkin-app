package com.example.munchkin_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.navigation.NavGraphs.bleNavGraph
import com.example.munchkin_app.navigation.NavGraphs.homeNavGraph
import com.example.munchkin_app.navigation.NavGraphs.iotNavGraph
import com.example.munchkin_app.navigation.NavGraphs.scriptNavGraph
import com.example.munchkin_app.navigation.NavGraphs.wifiNavGraph
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton

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


@Composable
fun MainNavController() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome",
    ) {
        //Welcome
        composable(
            route = "welcome",
            enterTransition = NavigationTransitions.fadeSlideIn,
            exitTransition = NavigationTransitions.fadeSlideOut
        ) { OnboardingPagerWithButton(navController = navController) }

        //Home
        homeNavGraph(navController)
        //Wifi
        wifiNavGraph(navController)
        //BLE
        bleNavGraph(navController)
        //IOT
        iotNavGraph(navController)
        //Script
        scriptNavGraph(navController)
    }
}


@Preview
@Composable
fun GetNavControllerPreview() {
    MainNavController()
}