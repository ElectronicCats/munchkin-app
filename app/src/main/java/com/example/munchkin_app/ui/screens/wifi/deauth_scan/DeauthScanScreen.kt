package com.example.munchkin_app.ui.screens.wifi.deauth_scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
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
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun DeauthScanScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") { innerPadding ->
        DeauthScanContents(innerPadding)
    }
}
@Composable
fun DeauthScanContents(
    innerPaddingValues: PaddingValues,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
){
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
            .fillMaxSize()
    ) {

        when {
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                DeauthScanCompactContent(
                    selectedChannel = selectedChannel,
                    channels = channels,
                    checked = checked,
                    onCheckedChange = { checked = it },
                    running = running,
                    onToggleRunning = { running = !running },
                    onChannelChanged = {}
                )
            }
            
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                DeauthScanExpandedContent(
                    selectedChannel = selectedChannel,
                    channels = channels,
                    checked = checked,
                    onCheckedChange = { checked = it },
                    running = running,
                    onToggleRunning = { running = !running },
                    onChannelChanged = {}
                )
            }
            
            else -> {
                DeauthScanCompactContent(
                    selectedChannel = selectedChannel,
                    channels = channels,
                    checked = checked,
                    onCheckedChange = { checked = it },
                    running = running,
                    onToggleRunning = { running = !running },
                    onChannelChanged = {}
                )
            }
        }


    }
}

@Composable
fun DeauthScanCompactContent(
    channels: List<String>,
    selectedChannel: String,
    onChannelChanged: (String) -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    running: Boolean,
    onToggleRunning: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ApplicationTitle("WiFi", "Deauth Scan")

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
                onCheckedChange = { onCheckedChange(it) }
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
            handleChannelSelection = { channel ->
                onChannelChanged(channel)
            },
            disabled = checked
        )

        ProportionalSpacer(0.03f)

        InformationLabel(
            loading = false,
            loadingText = "Loading Deauth Scan Information..."
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Deauth Scan Information",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        ProportionalSpacer(0.03f)

        StartButton(
            text = if (running) "Stop" else "Start",
            command = { onToggleRunning() }
        )

        ProportionalSpacer(0.03f)
    }
}

@Composable
fun DeauthScanExpandedContent(
    channels: List<String>,
    selectedChannel: String,
    onChannelChanged: (String) -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    running: Boolean,
    onToggleRunning: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ------- LEFT COLUMN -------
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            ApplicationTitle("WiFi", "Deauth Scan")

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
                    onCheckedChange = { onCheckedChange(it) }
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
                handleChannelSelection = { channel ->
                    onChannelChanged(channel)
                },
                disabled = checked
            )

            ProportionalSpacer(0.03f)

            StartButton(
                text = if (running) "Stop" else "Start",
                command = { onToggleRunning() }
            )
        }

        // ------- RIGHT COLUMN -------
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            InformationLabel(
                loading = false,
                loadingText = "Loading Deauth Scan Information..."
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Deauth Scan Information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            ProportionalSpacer(0.03f)
        }
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