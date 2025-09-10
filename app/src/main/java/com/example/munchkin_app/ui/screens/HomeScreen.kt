package com.example.munchkin_app.ui.screens


import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.data.network.sendPerson
import java.io.FileOutputStream

@Composable
fun HomeScreen(navController: NavController,
               usbDevice: UsbDevice?,
               modifier: Modifier
) {
    Box (
        modifier = modifier
            .fillMaxSize()
    ) {
        usbDevice?.let {
            Text(text = "Dispositivo USB conectado: ${it.deviceName}")
        } ?: Text(text = "No hay dispositivo conectado")
    }


}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MunchkinappTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController,
            usbDevice = null,
            modifier = Modifier
        )
    }
}

