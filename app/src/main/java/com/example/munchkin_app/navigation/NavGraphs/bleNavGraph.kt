package com.example.munchkin_app.navigation.NavGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.navigation.NavigationTransitions.fadeIn
import com.example.munchkin_app.navigation.NavigationTransitions.fadeOut
import com.example.munchkin_app.ui.screens.ble.BleScreen

fun NavGraphBuilder.bleNavGraph(navController: NavHostController) {
    // Pantalla principal ble
    composable(
        route = Destination.BLE.route,
        enterTransition = fadeIn,
        exitTransition = fadeOut
    ) {
        BleScreen(navController = navController)
    }
}