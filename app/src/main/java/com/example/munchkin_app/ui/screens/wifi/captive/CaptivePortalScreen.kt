package com.example.munchkin_app.ui.screens.wifi.captive

import androidx.compose.foundation.background
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
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.screens.wifi.captive.components.PortalAndRedirect
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun CaptivePortalScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MunchkinScreens.ApplicationLayout(navController, "wifi") { innerPadding ->
            CaptivePortalContents(innerPadding, navController)
        }
    }
}

@Composable
fun CaptivePortalContents(innerPadding: PaddingValues, navController: NavHostController){
    val mode = remember {
        listOf("Standalone", "Replicate")
    }
    var selectedModeIndex by remember { mutableStateOf(0) }

    val sdDumping = remember {
        listOf("Dump to SD", "No Dump")
    }
    var selectedSdDestinationIndex by remember { mutableStateOf(0) }

    val channels = remember {
        listOf(
            "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6",
            "Channel 7", "Channel 8", "Channel 9", "Channel 10", "Channel 11", "Channel 12",
            "Channel 13", "Channel 14"
        )
    }
    var selectedChannel by remember { mutableStateOf(channels.first()) }

    Column (
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ) {
        ApplicationTitle("WiFi", "Captive Portal")
        ProportionalSpacer(0.02f)
        Text(
            text = "HTML",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                PortalAndRedirect(
                    modifier = Modifier.weight(1f),
                    title = "Portal",
                    nameImportedContent = "No Portal Imported"
                )
                PortalAndRedirect(
                    modifier = Modifier.weight(1f),
                    title = "Redirect",
                    nameImportedContent = "No Portal Imported"
                )
            }
        }
        ProportionalSpacer(0.02f)
        OptionsButtonSegment(
            names = mode,
            selectedIndex = selectedModeIndex,
            onSelectionChanged = {index, name ->
                selectedModeIndex = index
                handleModeSelection(index, name)
            },
            title = "Mode",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        ProportionalSpacer(0.02f)
        OptionsButtonSegment(
            names = sdDumping,
            selectedIndex = selectedSdDestinationIndex,
            onSelectionChanged = {index, name ->
                selectedSdDestinationIndex = index
                handleSdDestinationSelection(index, name)
            },
            title = "SD Dumping",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        ProportionalSpacer(0.02f)
        ChannelDropMenu(
            channels = channels,
            selectedChannel = selectedChannel,
            handleChannelSelection = {channel ->
                selectedChannel = channel
                handleChannelSelection(channel)
            }
        )
        ProportionalSpacer(0.02f)
        StartButton(
            text = "Run",
            command = {
                navController.navigate("captiveProcess")
            }
        )
        ProportionalSpacer(0.02f)
    }
}

private fun handleModeSelection(index: Int, name: String) {
    println("Destination selected: $name (Index: $index)")
    when (index) {
        0 -> println("Configurando SD Card...")
        1 -> println("Configurando almacenamiento interno...")
        2 -> println("Configurando almacenamiento en la nube...")
        else -> println("Opción desconocida")
    }
}

private fun handleSdDestinationSelection(index: Int, name: String) {
    println("Destination selected: $name (Index: $index)")
    when (index) {
        0 -> println("Configurando SD Card...")
        1 -> println("Configurando almacenamiento interno...")
        2 -> println("Configurando almacenamiento en la nube...")
        else -> println("Opción desconocida")
    }
}

private fun handleChannelSelection(channel: String) {
    println("Channel selected: $channel")
    // Aquí puedes agregar la lógica específica para manejar la selección del canal
}


@Preview
@Composable
fun CaptivePortalScreenPreview() {
    MunchkinappTheme {
        CaptivePortalScreen(rememberNavController())
    }
}
