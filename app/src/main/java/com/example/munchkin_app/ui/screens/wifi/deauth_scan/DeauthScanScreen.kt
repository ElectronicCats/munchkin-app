package com.example.munchkin_app.ui.screens.wifi.deauth_scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.common.components.DisplayCardContainer
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.analyzer.AnalyzerScreen
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun DeauthScanScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") { innerPadding ->
        DeauthScanContents(innerPadding)
    }
}
@Composable
fun DeauthScanContents(innerPaddingValues: PaddingValues){
    var checked by remember { mutableStateOf(true) }
    var running by remember { mutableStateOf(false) }

    val channels = remember {
        listOf(
            "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6",
            "Channel 7", "Channel 8", "Channel 9", "Channel 10", "Channel 11", "Channel 12",
            "Channel 13"
        )
    }

    val selectedChannel by remember { mutableStateOf(channels.first())}
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(innerPaddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        ApplicationTitle("WiFi", "deauth Scan")

        ProportionalSpacer(0.03f)

        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Channel Settings",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        ProportionalSpacer(0.01f)

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Switch(
                checked = checked,
                onCheckedChange = { checked = it }
            )

            Box(modifier = Modifier.padding(horizontal = 8.dp))

            Text(
                text = "Channel Hopping",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        ProportionalSpacer(0.03f)

        ChannelDropMenu(
            channels = channels,
            selectedChannel = selectedChannel,
            handleChannelSelection = {it}
            )

        ProportionalSpacer(0.03f)

        DisplayCardContainer(
            loading = false,
            loadingText = "Loading deauth Scan Information..."
        )

        ProportionalSpacer(0.03f)

        if (running) {
            StartButton(
                text = "Stop",
                command = { running = false }
            )
        } else {
            StartButton(
                text = "Start",
                command = { running = true }
            )
        }

        ProportionalSpacer(0.03f)
    }
}

@Preview(
    name = "Phone Preview",
    showBackground = true,
    device = Devices.PHONE
)
@Composable
fun AnalyzerPreview(){
    MunchkinappTheme {
        DeauthScanScreen(rememberNavController())
    }
}

@Preview(
    name = "Tablet Preview",
    showBackground = true,
    device = Devices.TABLET
)
@Composable
fun AnalyzerExpandedPreview(){
    MunchkinappTheme {
        DeauthScanScreen(rememberNavController())
    }
}