package com.example.munchkin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import com.example.munchkin_app.navigation.MainNavController
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.UsbViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val usbViewModel: UsbViewModel by viewModels()
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbViewModel.registerReceiver()
        setContent {
            MunchkinappTheme {

                MainNavController()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        usbViewModel.registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        usbViewModel.unregisterReceiver()
    }


    override fun onDestroy() {
        super.onDestroy()
        usbViewModel.unregisterReceiver()
    }


}
