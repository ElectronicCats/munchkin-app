package com.example.munchkin_app.navigation.NavGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.navigation.NavigationTransitions.fadeIn
import com.example.munchkin_app.navigation.NavigationTransitions.fadeOut
import com.example.munchkin_app.ui.screens.scripts.ScriptsScreen

fun NavGraphBuilder.scriptNavGraph(navController: NavHostController) {
    // Pantalla principal WiFi
    composable(
        route = Destination.SCRIPT.route,
        enterTransition = fadeIn,
        exitTransition = fadeOut
    ) {
        ScriptsScreen(navController = navController)
    }
}