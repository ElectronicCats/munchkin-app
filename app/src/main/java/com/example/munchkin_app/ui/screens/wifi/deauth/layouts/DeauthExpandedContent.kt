package com.example.munchkin_app.ui.screens.wifi.deauth.layouts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel


@Composable
fun DeauthExpandedContent(
    innerPadding: PaddingValues,
    typeOfAttack: List<String>,
    attackIndex: Int,
    viewModel: UsbViewModel,
    screenViewModel: DeauthViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
    floatingX: Dp,
    floatingSize: Dp,
) {
    val networks by viewModel.deauthNetworks.collectAsState()
    var selectedNetwork by remember { mutableStateOf<String?>(null) }
    var bssidNetwork by remember { mutableStateOf<String?>(null) }
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        // --- Columna izquierda: controles ---
        Column(
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

            ProportionalSpacer(0.1f)

            OptionsButtonSegment(
                title = stringResource(R.string.wifi_deauth_attack_type),
                names = typeOfAttack,
                selectedIndex = attackIndex,
                modifier = Modifier.fillMaxWidth(),
                onSelectionChanged = { index, _ ->
                    viewModel.setAttackType(index)
                    screenViewModel.updateAttackIndex(index)
                }
            )

            ProportionalSpacer(0.1f)

            if (running) {
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

            ProportionalSpacer(0.1f)
            StartButton(
                text = if (running) "Stop" else "Start",
                command = { onToggleRunning()
                    if (!running)
                        viewModel.startDeauthAttack()
                    else viewModel.deauthStopAttackRequest()}
            )

            ProportionalSpacer(0.1f)
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
                loadingText = "Loading..."
            ) {

                networks.forEach { network ->
                    val isSelected = selectedNetwork == network.ssid

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedNetwork = network.ssid
                                bssidNetwork = network.bssid
                                Log.d("Deauth","$selectedNetwork\n$bssidNetwork")
                                viewModel.setDeauthTarget(network.bssid)
                                       },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFB2FFB2) else Color(0xfff1f3f4),
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            text = network.ssid.drop(2),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }

                    ProportionalSpacer(0.01f)
                }
            }

            FloatingActionButton(
                onClick = { viewModel.startDeauthScan() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-10).dp, y = floatingX)
                    .size(floatingSize),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(R.drawable._3_refresh),
                    contentDescription = "Sort/Refresh",
                    tint = Color.White
                )
            }
        }
    }
}