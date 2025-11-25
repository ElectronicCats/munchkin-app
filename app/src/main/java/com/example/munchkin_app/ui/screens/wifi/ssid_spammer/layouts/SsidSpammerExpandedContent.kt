package com.example.munchkin_app.ui.screens.wifi.ssid_spammer.layouts

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

@Composable
fun SsidSpammerExpandedContent() {
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

            TextFieldWithHint("Name...", "Type the name of the spammer list.")

            ProportionalSpacer(0.015f)

            TextFieldWithHint(
                "SSID's names...",
                "Type the SSID's for your spammer list."
            )

            ProportionalSpacer(0.025f)

            StartButton("Save SSID's", command = { } )
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

            SsidDropdownMenu()

            ProportionalSpacer(0.06f)

            StartButton("Start", command = { })
        }
    }
}
