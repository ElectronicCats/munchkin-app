package com.example.munchkin_app.ui.screens.wifi.captive.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.screens.wifi.captive.components.PortalAndRedirect

@Composable
fun CaptiveExpandedContent(
    innerPadding: PaddingValues,
    mode: List<String>,
    selectedModeIndex: Int,
    onSelectionModeChanged: (Int) -> Unit,
    sdDumping: List<String>,
    selectedSdDestinationIndex: Int,
    onSelectedSdIndexChange: (Int) -> Unit,
    channels: List<String>,
    selectedChannel: String,
    onChannelSelection: (String) -> Unit,
    navController: NavHostController
) {
    // Un único ScrollState compartido
    val sharedScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        ProportionalSpacer(0.05f)
        // Row central: izquierda (controles) y derecha (settings)
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Panel izquierdo: Controles (usa el mismo sharedScroll)
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(sharedScroll), // <- aquí
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Header
                ApplicationTitle(
                    application = "WiFi",
                    applicationName = "Captive Portal"
                )

                ProportionalSpacer(0.01f)

                Text(
                    text = "HTML",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.01f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PortalAndRedirect(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = "Portal",
                        nameImportedContent = "No Portal Imported"
                    )
                    PortalAndRedirect(
                        modifier = Modifier
                            .fillMaxWidth(),
                        title = "Redirect",
                        nameImportedContent = "No Portal Imported"
                    )
                }

                ProportionalSpacer(0.03f)
            }

            // Panel derecho: Contenido (usa el mismo sharedScroll)
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(sharedScroll), // <- y aquí
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.03f)

                OptionsButtonSegment(
                    names = mode,
                    selectedIndex = selectedModeIndex,
                    onSelectionChanged = { index, name ->
                        onSelectionModeChanged(index)
                        handleModeSelection(index, name)
                    },
                    title = "Mode",
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.03f)

                OptionsButtonSegment(
                    names = sdDumping,
                    selectedIndex = selectedSdDestinationIndex,
                    onSelectionChanged = { index, name ->
                        onSelectedSdIndexChange(index)
                        handleSdDestinationSelection(index, name)
                    },
                    title = "SD Dumping",
                    modifier = Modifier.fillMaxWidth()
                )

                ProportionalSpacer(0.03f)

                ChannelDropMenu(
                    channels = channels,
                    selectedChannel = selectedChannel,
                    handleChannelSelection = { channel ->
                        onChannelSelection(channel)
                        handleChannelSelection(channel)
                    }
                )

                ProportionalSpacer(0.02f)

                // Más contenido...
                // Botón fijo en la parte inferior
                StartButton(
                    text = "Run",
                    command = {
                        navController.navigate("captiveProcess")
                    }
                )
            }
        }


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
