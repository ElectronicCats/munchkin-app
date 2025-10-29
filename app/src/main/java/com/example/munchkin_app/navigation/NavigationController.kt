package com.example.munchkin_app.navigation

import android.provider.Settings.Global.getString
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.screens.UsbControlUI
import com.example.munchkin_app.ui.screens.ble.BleScreen
import com.example.munchkin_app.ui.screens.home.HomeScreen
import com.example.munchkin_app.ui.screens.iot.IotScreen
import com.example.munchkin_app.ui.screens.scripts.ScriptsScreen
import com.example.munchkin_app.ui.screens.welcome.OnboardingPagerWithButton
import com.example.munchkin_app.ui.screens.wifi.WifiScreen
import com.example.munchkin_app.viewmodel.UsbViewModel


@Composable
fun MainNavController(viewModel: UsbViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome",
    ) {
        composable("test") { UsbControlUI( viewModel ) }
        composable("welcome") { OnboardingPagerWithButton(navController = navController) }
        composable(Destination.HOME.route) {HomeScreen(navController = navController)}
        composable(Destination.WIFI.route) { WifiScreen(navController = navController) }
        composable(Destination.BLE.route) { BleScreen(navController = navController) }
        composable(Destination.IOT.route) { IotScreen(navController = navController) }
        composable(Destination.SCRIPT.route) { ScriptsScreen(navController = navController) }
    }
}

enum class Destination(
    val route: String,
    val labelResId: Int,
    val icon: ImageVector,
    val contentDescription: Int
) {
    HOME("home", R.string.NavBar_home, Icons.Default.Home, R.string.NavBar_icon_home),
    WIFI("wifi", R.string.NavBar_wifi, Icons.Default.PlayArrow, R.string.NavBar_icon_wifi),
    BLE("ble", R.string.NavBar_ble, Icons.Default.PlayArrow, R.string.NavBar_icon_ble),
    IOT("iot", R.string.NavBar_iot, Icons.Default.PlayArrow, R.string.NavBar_icon_iot),
    SCRIPT("script", R.string.NavBar_scripts, Icons.Default.PlayArrow, R.string.NavBar_icon_scripts)
}


@Preview
@Composable
fun GetNavControllerPreview() {
    MainNavController(viewModel())
}