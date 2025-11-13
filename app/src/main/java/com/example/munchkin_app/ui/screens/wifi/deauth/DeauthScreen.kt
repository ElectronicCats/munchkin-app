package com.example.munchkin_app.ui.screens.wifi.deauth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun DeauthScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding ->
    DeauthContents(innerPadding)
    }
}

@Composable
fun DeauthContents(innerPadding: PaddingValues) {
    Column (
        modifier = Modifier.padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ){
        val configuration = LocalConfiguration.current
        val orientation = configuration.orientation

        val floatingSize = when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 56.dp
            Configuration.ORIENTATION_PORTRAIT -> 40.dp
            else -> 56.dp
            }

        val floatingX = when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 40.dp
            Configuration.ORIENTATION_PORTRAIT -> 55.dp
            else -> 56.dp
        }

        val typeOfAttack = remember {
            listOf("Broadcast", "Rogue AP", "Combined")
        }

        var attackIndex by remember { mutableIntStateOf(0) }

        var running by remember { mutableStateOf(false) }

        ApplicationTitle("WiFi", "Deauth")

        ProportionalSpacer(0.03f)

        OptionsButtonSegment(
            title = "Type of Attack",
            names = typeOfAttack,
            selectedIndex = attackIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onSelectionChanged = {index, name ->
                attackIndex = index
            }
        )

        ProportionalSpacer(0.02f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ){
            InformationLabel(
                modifier = Modifier,
                information = "Attack is running",
                loadingText = "Loading..."
            )

            FloatingActionButton(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = floatingX)
                    .size(floatingSize),
                containerColor = Color(0XFFF48FB1)
            ) {
                // Icono de flechas de dos vías (simulando refrescar/ordenar)
                Icon(
                    painter = painterResource(R.drawable._3_refresh),
                    contentDescription = "Sort/Refresh",
                    tint = Color.White
                )
            }
        }

        ProportionalSpacer(0.05f)

        if (running) {
            StartButton(
                text = "Stop",
                command = { }
            )
        } else {
            StartButton(
                text = "Start",
                command = { }
            )
        }
    }
}

@Preview
@Composable
fun DeauthScreenPreview() {
    MunchkinappTheme {
        DeauthScreen(rememberNavController())
    }

}