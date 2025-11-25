package com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.SsidDropdownMenu
import com.example.munchkin_app.ui.common.components.TextFieldWithHint


@Composable
fun SsidSpammerCompactContent() {
    ApplicationTitle(
        stringResource(R.string.wifi_ssidspam_application_title),
        stringResource(R.string.wifi_ssidspam_title)
    )

    ProportionalSpacer(0.03f)

    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = stringResource(R.string.wifi_ssid_spammer_add_ssid),
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Black,
        textAlign = TextAlign.Center
    )

    ProportionalSpacer(0.03f)

    TextFieldWithHint("Name...", "Type the name of the spammer list.")

    ProportionalSpacer(0.01f)

    TextFieldWithHint("SSID's names...", "Type the SSID's for your spammer list separated by a coma.")

    ProportionalSpacer(0.02f)

    StartButton("Save SSID's", command = {})

    ProportionalSpacer(0.06f)

    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = "SSID Lists",
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Black,
        textAlign = TextAlign.Center
    )

    ProportionalSpacer(0.02f)

    SsidDropdownMenu()

    ProportionalSpacer(0.03f)

    StartButton("Start",  command = { })

}
