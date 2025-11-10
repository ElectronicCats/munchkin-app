package com.example.munchkin_app.ui.screens.wifi.captive

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.IndeterminateCircularIndicator
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun CaptiveProcessScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "captive") {innerPadding ->
        CaptiveProcessContents(innerPadding, navController)
    }
}

@Composable
fun CaptiveProcessContents(innerPadding: PaddingValues, navController: NavHostController){
    var loading by remember { mutableStateOf(true) }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(innerPadding)
    ) {
        ApplicationTitle("WiFi", "Captive Portal")

        ProportionalSpacer(0.03f)

        IndeterminateCircularIndicator(loading, 0.7f, 0.07f)

        ProportionalSpacer(0.03f)

        Text(
            text = "Que pedo chaviza aqui va la informacion del analizador",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp),
        )

        ProportionalSpacer(0.03f)

        StartButton("Stop", { loading = false; navController.navigate("captive") })
    }
}

@Preview
@Composable
fun CaptiveProcessScreenPreview() {
    MunchkinappTheme {
        CaptiveProcessScreen(rememberNavController())
    }
}