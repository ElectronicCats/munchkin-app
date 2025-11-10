package com.example.munchkin_app.ui.screens.wifi.analyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@Composable
fun AnalyzerScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MunchkinScreens.ApplicationLayout(navRouteBack = "wifi", navController = navController) {innerPadding ->
            AnalyzerContent(innerPadding)
        }
    }
}

@Composable
fun AnalyzerContent(innerPaddingValues: PaddingValues){
    // Lista de opciones de destino
    val storageDestination = remember {
        listOf("SD Card", "Internal")
    }
    var selectedDestinationIndex by remember { mutableStateOf(0) }

    // Lista de canales para el dropdown
    val channels = remember {
        listOf(
            "Channel 1", "Channel 2", "Channel 3", "Channel 4", "Channel 5", "Channel 6",
        "Channel 7", "Channel 8", "Channel 9", "Channel 10", "Channel 11", "Channel 12",
            "Channel 13", "Channel 14"
            )
    }
    var selectedChannel by remember { mutableStateOf(channels.first()) }

    Column(
        modifier = Modifier
            .padding(innerPaddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        ApplicationTitle(
            application = "WiFi",
            applicationName = "Analyzer"
        )

        ProportionalSpacer(0.03f)

        // Selector de destino usando lista de strings
        OptionsButtonSegment(
            names = storageDestination,
            selectedIndex = selectedDestinationIndex,
            onSelectionChanged = { index, name ->
                selectedDestinationIndex = index
                handleDestinationSelection(index, name)
            },
            title = "Storage Destination",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProportionalSpacer(0.02f)

        ChannelDropMenu(
            channels = channels,
            selectedChannel = selectedChannel,
            handleChannelSelection = { channel ->
                selectedChannel = channel
                handleChannelSelection(channel)
            }
        )

        ProportionalSpacer(0.02f)
        
        InformationLabel(modifier = Modifier.weight(1f))

        ProportionalSpacer(0.02f)

        StartButton(
            text = "Start",
            command = {}
        )

        ProportionalSpacer(0.05f)
    }
}

private fun handleDestinationSelection(index: Int, name: String) {
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
fun AnalyzerPreview(){
    MunchkinappTheme {
        AnalyzerScreen(rememberNavController())
    }
}