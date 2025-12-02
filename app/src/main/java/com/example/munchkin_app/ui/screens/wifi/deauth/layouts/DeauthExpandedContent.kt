package com.example.munchkin_app.ui.screens.wifi.deauth.layouts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.WifiStrengthIcon
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.ui.common.components.OptionsButtonSegment
import com.example.munchkin_app.ui.screens.wifi.deauth.DeauthActions
import com.example.munchkin_app.ui.screens.wifi.deauth.DeauthState


@Composable
fun DeauthExpandedContent(
    state: DeauthState,
    action: DeauthActions,
) {

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // --- Columna izquierda: controles ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            ApplicationTitle(
                application = stringResource(R.string.wifi_title),
                stringResource(R.string.wifi_deauth_application_name)
            )

            ProportionalSpacer(0.03f)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "Scan Nearby Networks",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            ProportionalSpacer(0.01f)

            FloatingActionButton(
                onClick = {
                    if (!state.running) {
                        action.viewModel.startDeauthScan()
                        action.screenViewModel.updateScanAttempted(true)
                        action.viewModel.clearDeauthNetworks()
                    } else {
                        print("You cant do anything with this button at the moment")
                    }
        },
                modifier = Modifier,
                containerColor = if (!state.running) MaterialTheme.colorScheme.primary else Color.LightGray
            ) {
                Icon(
                    painter = painterResource(R.drawable._3_refresh),
                    contentDescription = "Sort/Refresh",
                    tint = Color.White
                )
            }

            ProportionalSpacer(0.03f)

            OptionsButtonSegment(
                title = stringResource(R.string.wifi_deauth_attack_type),
                names = state.typeOfAttack,
                selectedIndex = state.attackIndex,
                disabled = state.running || !state.networkIsSelected,
                modifier = Modifier.fillMaxWidth(),
                onSelectionChanged = { index, _ ->
                    Log.d("UI", "UI index = $index")
                    action.viewModel.setAttackType(index)
                    action.screenViewModel.updateAttackIndex(index)
                    Log.d("UI", "attackIndex = $state.attackIndex")
                },
                getTooltipText = { index ->
                    state.attackDescriptions.getOrElse(index) { "" }
                }
            )

            ProportionalSpacer(0.03f)

            if (state.running) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                    ProportionalSpacer(0.01f)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = stringResource(R.string.deauth_inprocess),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ProportionalSpacer(0.03f)
            StartButton(
                text = if (state.running) "Stop" else "Start",
                disabled = state.networkIsSelected,
                command = { action.screenViewModel.updateRunning(!state.running)
                    if (!state.running)
                        action.viewModel.startDeauthAttack()
                    else action.viewModel.deauthStopAttackRequest()},
            )

            ProportionalSpacer(0.03f)
        }

        // --- Columna derecha: panel grande ---
        Box(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            InformationLabel(
                modifier = Modifier.fillMaxSize(),
                loadingText = "Scanning for Nearby Networks...",
                loading = state.scanLoading
            ) {
                if (state.networks.isEmpty()){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "Scan Networks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                state.networks.forEach { network ->
                    val isSelected = state.selectedNetwork == network.ssid

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!state.running) {
                                    action.screenViewModel.updateIsNetworkSelected(true)
                                    action.screenViewModel.updateSelectedNetwork(network.ssid)
                                    action.viewModel.setDeauthTarget(network.bssid)
                                }
                                       },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFB2FFB2) else Color(0xfff1f3f4),
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(0.20f),
                                text = "Ch: ${network.channel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                modifier = Modifier
                                    .weight(0.70f)
                                    .padding(16.dp),
                                text = network.ssid,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            WifiStrengthIcon(network.rssi, Modifier.weight(0.10f))
                        }

                    }

                    ProportionalSpacer(0.01f)
                }
            }
        }
    }
}



