package com.example.munchkin_app.navigation.NavGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.navigation.NavigationTransitions.fadeIn
import com.example.munchkin_app.navigation.NavigationTransitions.fadeOut
import com.example.munchkin_app.ui.screens.iot.IotScreen

fun NavGraphBuilder.iotNavGraph(navController: NavHostController) {
    // Pantalla principal iot
    composable(
        route = Destination.IOT.route,
        enterTransition = fadeIn,
        exitTransition = fadeOut
    ) {
        IotScreen(navController = navController)
    }
}