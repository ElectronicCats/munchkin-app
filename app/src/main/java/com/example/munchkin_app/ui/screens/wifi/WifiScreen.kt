package com.example.munchkin_app.ui.screens.wifi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.Cards
import com.example.munchkin_app.ui.common.MunchkinScreens
import com.example.munchkin_app.ui.screens.ble.BleSample
import com.example.munchkin_app.ui.theme.MunchkinappTheme


@Composable
fun WifiScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        MunchkinScreens.MunchkinLayout (navController) { innerPadding ->
            WifiHome(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun WifiHome(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "WiFi",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        AppCardRow(
            navController = navController,
            titleOne = "Analyzer",
            textOne = "Scan All Networks Around You",
            titleTwo = "Captive",
            textTwo = "Set Up Your Own Captive Portal!",
            navRouteOne = "analyzer",
            navRouteTwo = "test",
            iconOne = Icons.Default.Favorite,
            iconTwo = Icons.Default.Favorite,
            enableOne = true,
            enableTwo = true
        )
        //Deauth and Deauth Scan
        AppCardRow(
            navController = navController,
            titleOne = "Deauth",
            textOne = "Deny Access to Users in a Network",
            titleTwo = "Deauth Scan",
            textTwo = "Detect Deauth Packets in the Air",
            navRouteOne = "welcome",
            navRouteTwo = "test",
            iconOne = Icons.Default.Favorite,
            iconTwo = Icons.Default.Favorite,
            enableOne = true,
            enableTwo = true
        )
        //DOS and SSID Spammer
        AppCardRow(
            navController = navController,
            titleOne = "DOS",
            textOne = "Denial of Service in your Network",
            titleTwo = "SSID Spammer",
            textTwo = "Spam a great amount of SSIDs",
            navRouteOne = "welcome",
            navRouteTwo = "test",
            iconOne = Icons.Default.Favorite,
            iconTwo = Icons.Default.Favorite,
            enableOne = true,
            enableTwo = true
        )
        //Modbus TCP and additional app (if needed)
        AppCardRow(
            navController = navController,
            titleOne = "Modbus TCP",
            textOne = "Audit Any Network Nearby",
            titleTwo = "",
            textTwo = "",
            navRouteOne = "welcome",
            navRouteTwo = "",
            iconOne = Icons.Default.Favorite,
            iconTwo = Icons.Default.Favorite,
            enableOne = true,
            enableTwo = false
        )


    }
}

@Composable
fun AppCardRow(
    navController: NavHostController,
    titleOne: String,
    textOne: String,
    titleTwo: String,
    textTwo: String,
    navRouteOne: String,
    navRouteTwo: String,
    iconOne: ImageVector,
    iconTwo: ImageVector,
    enableOne: Boolean = true,
    enableTwo: Boolean = true
) {
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (enableOne) {
            Cards.CustomCard(
                modifier = Modifier.weight(if (enableTwo) 1f else 0.8f),
                title = titleOne,
                text = textOne,
                iconDescription = "First Card Icon",
                navController = navController,
                navRoute = navRouteOne,
                icon = iconOne
            )
        }

        if (enableTwo) {
            Cards.CustomCard(
                modifier = Modifier.weight(1f),
                title = titleTwo,
                text = textTwo,
                iconDescription = "Second Card Icon",
                navController = navController,
                navRoute = navRouteTwo,
                icon = iconTwo
            )
        }
    }
}

@Preview
@Composable
fun WifiScreenPreview() {
    MunchkinappTheme {
        WifiScreen(rememberNavController())
    }
}