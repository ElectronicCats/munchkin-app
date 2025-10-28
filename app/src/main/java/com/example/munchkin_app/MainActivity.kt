package com.example.munchkin_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.munchkin_app.navigation.MainNavController
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.UsbViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val usbViewModel: UsbViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbViewModel.registerReceiver()

        setContent {
            MunchkinappTheme {
                MainNavController(usbViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usbViewModel.unregisterReceiver()
    }


}
