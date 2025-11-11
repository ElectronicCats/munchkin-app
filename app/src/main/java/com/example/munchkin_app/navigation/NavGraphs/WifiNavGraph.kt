package com.example.munchkin_app.navigation.NavGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.navigation.NavigationTransitions.fadeIn
import com.example.munchkin_app.navigation.NavigationTransitions.fadeOut
import com.example.munchkin_app.navigation.NavigationTransitions.scaleIn
import com.example.munchkin_app.navigation.NavigationTransitions.scaleOut
import com.example.munchkin_app.ui.screens.wifi.WifiScreen
import com.example.munchkin_app.ui.screens.wifi.analyzer.AnalyzerScreen
import com.example.munchkin_app.ui.screens.wifi.captive.CaptivePortalScreen
import com.example.munchkin_app.ui.screens.wifi.captive.CaptiveProcessScreen

fun NavGraphBuilder.wifiNavGraph(navController: NavHostController) {
    // Pantalla principal WiFi
    composable(
        route = Destination.WIFI.route,
        enterTransition = fadeIn,
        exitTransition = fadeOut
    ) {
        WifiScreen(navController = navController)
    }

    // Subpantallas del módulo WiFi
    //Analyzer
    composable(
        route = "analyzer",
        enterTransition = scaleIn,
        exitTransition = scaleOut
    ) {
        AnalyzerScreen(navController = navController)
    }
    //Captive
    composable(
        route = "captive",
        enterTransition = scaleIn,
        exitTransition = scaleOut
    ) {
        CaptivePortalScreen(navController = navController)
    }
    composable(
        route = "captiveProcess",
    ) { CaptiveProcessScreen(navController = navController) }
}
