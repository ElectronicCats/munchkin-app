package com.example.munchkin_app.ui.screens.wifi.ssid_spammer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.ChannelDropMenu
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.common.components.SsidDropdownMenu
import com.example.munchkin_app.ui.screens.wifi.deauth.DeauthScreen
import com.example.munchkin_app.ui.theme.MunchkinappTheme


@Composable
fun SsidSpammerScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding ->
        SsidSpammerContent(innerPadding)
    }
}

@Composable
fun SsidSpammerContent(
    innerPadding: PaddingValues,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
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

        TextFieldWithHint("SSID's names...", "Type the SSID's for your spammer list.")

        ProportionalSpacer(0.02f)

        StartButton("Hola") { }

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

        StartButton("Hola") { }

    }
}

@Composable
fun TextFieldWithHint(name: String, hint: String) {
    val fieldShape = RoundedCornerShape(8.dp)

    TextField(
        modifier = Modifier
            .padding(horizontal = 60.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = fieldShape
            )
            .fillMaxWidth(),
        state = rememberTextFieldState(),
        shape = fieldShape,
        label = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3)
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        text = hint,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Black,
        textAlign = TextAlign.Center,
    )
}

@Preview
@Composable
fun SsidSpammerScreenPreview() {
    MunchkinappTheme {
        SsidSpammerScreen(rememberNavController())
    }

}