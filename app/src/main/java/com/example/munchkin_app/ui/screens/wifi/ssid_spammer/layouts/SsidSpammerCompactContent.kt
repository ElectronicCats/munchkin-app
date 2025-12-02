package com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.ui.common.components.InputType
import com.example.munchkin_app.ui.common.components.SsidDropdownMenu
import com.example.munchkin_app.ui.common.components.TextFieldWithHint
import com.example.munchkin_app.ui.screens.wifi.ssid_spammer.SsidSpammerActions
import com.example.munchkin_app.ui.screens.wifi.ssid_spammer.SsidSpammerState


@Composable
fun SsidSpammerCompactContent(
    state: SsidSpammerState,
    action: SsidSpammerActions,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
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
            value = state.ssidListTitle,
            onValueChange = { newValue ->
                action.screenViewModel.updateSsidListTitle(newValue)
            }
        )

        ProportionalSpacer(0.015f)

        TextFieldWithHint(
            name = "SSID's names...",
            hint = "Type the SSID's for your spammer list.\nThe SSID's must be separated by a comma",
            value = state.ssidText,
            type = InputType.SSID_LIST,
            onValueChange = { newValue ->
                action.screenViewModel.updateSsidText(newValue)
            }
        )

        ProportionalSpacer(0.025f)

        StartButton(
            text = "Save SSID's",
            command = action.onSave
        )

        ProportionalSpacer(0.03f)

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "SSID Lists",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        ProportionalSpacer(0.02f)

        SsidDropdownMenu(
            ssids = state.configNames,
            selectedItem = state.selectedListName,
            disabled = state.running,
            onDelete = { listName ->
                action.onDelete(listName)
            },
            content = { newName ->
                action.screenViewModel.updateSsidIsSelected(true)
                action.updateSelectedListName(newName)
                action.screenViewModel.updateListToSpam(action.screenViewModel.allConfigs.value.configs[newName] ?: emptyList())
            }
        )

        ProportionalSpacer(0.03f)

        InformationLabel(
            loading = state.running,
            loadingText = "Spamming..."
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.listToSpam.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }

        ProportionalSpacer(0.03f)

        StartButton(
            text = if (!state.running) "Start" else "Stop",
            disabled = state.ssidIsSelected,
            command = action.onStartStop
        )

        ProportionalSpacer(0.03f)
    }
}
