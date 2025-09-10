package com.example.munchkin_app.ui.screens


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.data.network.sendPerson
import java.io.FileOutputStream



@Composable
fun HomeScreen(navController: NavController) {
    // Aquí va tu UI
    Text(text = "¡Hola, Home Screen!")
    Button(
        onClick = { }
    ) { }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MunchkinappTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
    }
}

