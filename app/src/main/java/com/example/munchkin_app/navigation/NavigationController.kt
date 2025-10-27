package com.example.munchkin_app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.common.MunchkinScreens
import com.example.munchkin_app.ui.screens.UsbControlUI
import com.example.munchkin_app.ui.screens.home.HomeScreen
import com.example.munchkin_app.ui.screens.home.ScrollContent
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton
import com.example.munchkin_app.viewmodel.UsbViewModel


@Composable
fun GetNavController(viewModel: UsbViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome",
    ) {
        composable("test") { UsbControlUI( viewModel ) }
        composable("welcome") { OnboardingPagerWithButton(navController = navController) }
        composable("home") {
            AppsNavHost(navController, Destination.HOME, Modifier)
        }
    }
}

enum class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("home", "Home", Icons.Default.Home, "Home"),
    WIFI("wifi", "Wifi", Icons.Default.PlayArrow, "Wifi"),
    BLE("ble", "BLE", Icons.Default.PlayArrow, "BLE"),
    IOT("iot", "IoT", Icons.Default.PlayArrow, "IoT"),
    SCRIPT("script", "Scripts", Icons.Default.PlayArrow, "Scripts")
}

@Composable
fun AppsNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
    ) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.HOME -> HomeScreen(navController)
                    Destination.WIFI -> UsbControlUI(viewModel())
                    Destination.BLE -> UsbControlUI(viewModel())
                    Destination.IOT -> UsbControlUI(viewModel())
                    Destination.SCRIPT -> UsbControlUI(viewModel())
                }
            }
        }
    }
}


@Preview
@Composable
fun GetNavControllerPreview() {
    GetNavController(viewModel())
}