package com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.SsidDropdownMenu
import com.example.munchkin_app.ui.common.components.TextFieldWithHint
import com.example.munchkin_app.viewmodel.screens.wifi.SsidSpamViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import kotlin.math.log

@Composable
fun SsidSpammerExpandedContent(
    viewModel: UsbViewModel,
    screenViewModel: SsidSpamViewModel
) {
    var ssidListTitle by remember { mutableStateOf("") }
    var ssidText by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }
    val allConfigs by screenViewModel.allConfigs.collectAsState()
    val configNames = allConfigs.configs.keys.toList()

    var selectedListName by remember {
        mutableStateOf(configNames.firstOrNull() ?: "")
    }

    val ssidsForSelectedList: List<String> = allConfigs.configs[selectedListName] ?: emptyList()


    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {

        // ---- LEFT PANEL: Creation Form ---- //
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ApplicationTitle(
                stringResource(R.string.wifi_ssidspam_application_title),
                stringResource(R.string.wifi_ssidspam_title)
            )

            ProportionalSpacer(0.03f)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.wifi_ssid_spammer_add_ssid),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            ProportionalSpacer(0.03f)

            TextFieldWithHint(
                name = "Name...",
                hint = "Type the name of the spammer list.",
                value = ssidListTitle,
                onValueChange = { newValue ->
                    ssidListTitle = newValue
                }
            )

            ProportionalSpacer(0.015f)

            TextFieldWithHint(
                name = "SSID's names...",
                hint = "Type the SSID's for your spammer list.",
                value = ssidText,
                onValueChange = { newValue ->
                    ssidText = newValue
                }
            )

            ProportionalSpacer(0.025f)

            StartButton(
                text = "Save SSID's",
                command = {
                    if (ssidListTitle.isBlank() || ssidText.isBlank()) {
                        Log.w("SSID_SPAMMER", "El nombre de la lista o los SSIDs no pueden estar vacíos.")
                    } else {
                        screenViewModel.saveNewConfig(
                            name = ssidListTitle,
                            rawText = ssidText
                        )
                        Log.d("SSID_SPAMMER", "Configuración guardada: Nombre='$ssidListTitle'")

                    }
                }
            )
        }

        // ---- RIGHT PANEL: Lists + Start ---- //
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "SSID Lists",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            ProportionalSpacer(0.02f)

            SsidDropdownMenu(
                ssids = configNames,
                selectedItem = selectedListName
            ) { newName ->
                selectedListName = newName

                val listToSpam = allConfigs.configs[newName] ?: emptyList()

                if (listToSpam.isNotEmpty()) {
                    viewModel.ssidSpammerSetSsids(3, listToSpam)
                    Log.d("SsidSpammer", "Lista de SSIDs enviada: $newName (${listToSpam.size} SSIDs)")
                } else {
                    Log.w("SsidSpammer", "Lista de SSIDs vacía para el nombre: $newName")
                }
            }

            ProportionalSpacer(0.06f)

            StartButton(
                text = if (!running) "Start" else "Stop",
                command = {
                    if (!running)
                        viewModel.ssidSpammerStartRequest()
                    else viewModel.ssidSpammerStopRequest()
                    running = !running
                }
            )
        }
    }
}
