package com.example.munchkin_app.ui.screens.scripts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.munchkin_app.ui.common.MunchkinScreens
import com.example.munchkin_app.ui.screens.UsbControlUI
import com.example.munchkin_app.ui.screens.ble.BleSample
import com.example.munchkin_app.viewmodel.UsbViewModel

@Composable
fun ScriptsScreen(navController: NavHostController) {
    val viewModel: UsbViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ){
        MunchkinScreens.MunchkinLayout (navController) { innerPadding ->
            UsbControlUI(viewModel)
        //BleSample("Scripts Sample", innerPadding)
        }
    }
}