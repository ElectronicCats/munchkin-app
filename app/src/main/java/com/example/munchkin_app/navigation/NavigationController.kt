package com.example.munchkin_app.navigation

import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.screens.UsbControlUI
import com.example.munchkin_app.viewmodel.UsbViewModel
import kotlinx.serialization.Serializable
import kotlin.getValue
import androidx.activity.viewModels
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton
import dagger.hilt.android.AndroidEntryPoint


@Serializable
object Welcome
@Serializable
object TestScreen


@Composable
fun getNavController(viewModel: UsbViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { OnboardingPagerWithButton() }
        composable("test") { UsbControlUI( viewModel ) }
        // Add more destinations similarly.
    }
}

