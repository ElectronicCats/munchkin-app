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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.ui.common.components.InputType
import com.example.munchkin_app.ui.common.components.SsidDropdownMenu
import com.example.munchkin_app.ui.common.components.TextFieldWithHint
import com.example.munchkin_app.viewmodel.screens.wifi.SsidSpamViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel
import kotlinx.coroutines.launch

@Composable
fun SsidSpammerExpandedContent(
    viewModel: UsbViewModel,
    screenViewModel: SsidSpamViewModel,
    snackbarHostState: SnackbarHostState
) {
    val ssidListTitle by screenViewModel.ssidListTitle.collectAsState()
    val ssidText by screenViewModel.ssidText.collectAsState()
    val running by screenViewModel.running.collectAsState()
    val ssidIsSelected by screenViewModel.ssidIsSelected.collectAsState()
    val allConfigs by screenViewModel.allConfigs.collectAsState()
    val configNames = allConfigs.configs.keys.toList()
    val listToSpam: List<String> by screenViewModel.listToSpam.collectAsState()

    val scope = rememberCoroutineScope()

    var selectedListName by remember {
        mutableStateOf(configNames.firstOrNull() ?: "Pick a List")
    }

    if (allConfigs.configs.isEmpty()) screenViewModel.updateSsidIsSelected(false)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {

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
                    screenViewModel.updateSsidListTitle(newValue)
                }
            )

            ProportionalSpacer(0.015f)

            TextFieldWithHint(
                name = "SSID's names...",
                hint = "Type the SSID's for your spammer list.\nThe SSID's must be separated by a comma",
                value = ssidText,
                type = InputType.SSID_LIST,
                onValueChange = { newValue ->
                    screenViewModel.updateSsidText(newValue)
                }
            )

            ProportionalSpacer(0.025f)

            StartButton(
                text = "Save SSID's",
                command = {
                    scope.launch {
                        Log.d("SSID_SPAMMER", "Snackbar lanzado")
                        snackbarHostState.showSnackbar(
                            message = "You must add a name to your list and SSID's to save the configuration.",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        )
                    }
                    if (ssidListTitle.isBlank() || ssidText.isBlank()) {
                        Log.w("SSID_SPAMMER", "El nombre de la lista o los SSIDs no pueden estar vacíos.")
                    } else {
                        scope.launch {
                            Log.d("SSID_SPAMMER", "Snackbar lanzado")
                            snackbarHostState.showSnackbar(
                                message = "Configuration Saved Successfully.",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                        screenViewModel.saveNewConfig(
                            name = ssidListTitle,
                            rawText = ssidText
                        )
                        Log.d("SSID_SPAMMER", "Configuración guardada: Nombre='$ssidListTitle'")
                        screenViewModel.updateSsidText("")
                        screenViewModel.updateSsidListTitle("")
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
                selectedItem = selectedListName,
                disabled = running,
                onDelete = { listName ->
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Are you sure you want to delete $listName?",
                            duration = SnackbarDuration.Indefinite,
                            actionLabel = "Yes",
                            withDismissAction = true
                        )

                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                screenViewModel.deleteConfig(listName)

                                if (listName == selectedListName) {
                                    val remainingConfigs = allConfigs.configs.keys.toList().filter { it != listName }
                                    selectedListName = remainingConfigs.firstOrNull() ?: "Pick a List"
                                    screenViewModel.updateListToSpam(allConfigs.configs[selectedListName] ?: emptyList())
                                }

                                snackbarHostState.showSnackbar(
                                    message = "'$listName' deleted successfully.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            SnackbarResult.Dismissed -> {
                                Log.d("SSID_SPAMMER", "Borrado de '$listName' cancelado o ignorado.")
                            }
                        }
                    }


                },
                content = { newName ->
                    screenViewModel.updateSsidIsSelected(true)
                    selectedListName = newName
                    screenViewModel.updateListToSpam(allConfigs.configs[newName] ?: emptyList())
                }
            )

            ProportionalSpacer(0.03f)

            InformationLabel(
                loading = running
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = listToSpam.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            ProportionalSpacer(0.03f)

            StartButton(
                text = if (!running) "Start" else "Stop",
                disabled = ssidIsSelected,
                command = {
                    if (!running) {
                        viewModel.ssidSpammerSetSsids(3, listToSpam)
                        viewModel.ssidSpammerStartRequest()
                    } else viewModel.ssidSpammerStopRequest()
                    screenViewModel.updateRunning(!running)
                }
            )
        }
    }
}
