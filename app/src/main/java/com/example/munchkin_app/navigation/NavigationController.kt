package com.example.munchkin_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.screens.UsbControlUI
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton
import com.example.munchkin_app.viewmodel.UsbViewModel

@Composable
fun GetNavController(viewModel: UsbViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome",
    ) {
        composable("welcome") { OnboardingPagerWithButton(navController = navController) }
        composable("test") { UsbControlUI( viewModel ) }
        // Add more destinations similarly.
    }
}

@Preview
@Composable
fun GetNavControllerPreview() {
    GetNavController(viewModel())
}