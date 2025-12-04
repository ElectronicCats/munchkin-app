package com.example.munchkin_app.navigation.NavGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.navigation.NavigationTransitions.fadeIn
import com.example.munchkin_app.navigation.NavigationTransitions.fadeOut
import com.example.munchkin_app.ui.screens.home.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    // Pantalla principal home
    composable(
        route = Destination.HOME.route,
        enterTransition = fadeIn,
        exitTransition = fadeOut
    ) {
        HomeScreen(navController = navController)
    }
}