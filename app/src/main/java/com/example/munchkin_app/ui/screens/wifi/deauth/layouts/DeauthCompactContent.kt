package com.example.munchkin_app.ui.screens.wifi.deauth.layouts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.InformationLabel
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel

@Composable
fun DeauthCompactContent(
    innerPadding: PaddingValues,
    typeOfAttack: List<String>,
    attackIndex: Int,
    viewModel: UsbViewModel,
    screenViewModel: DeauthViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
    onStopRunning: () -> Unit,
){
    val networks by viewModel.deauthNetworks.collectAsState()
    val selectedNetwork by screenViewModel.selectedNetwork.collectAsState()
    val networkIsSelected by screenViewModel.isNetworkSelected.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle


    var scanAttempted by remember { mutableStateOf(false) } // 🚨 Estado local
    val scanLoading = scanAttempted && networks.isEmpty()

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onStopRunning()
                viewModel.deauthStopAttackRequest()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            onStopRunning()
            viewModel.deauthStopAttackRequest()
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ){

        ApplicationTitle(
            application = stringResource(R.string.wifi_title),
            stringResource(R.string.wifi_deauth_application_name)
        )

        ProportionalSpacer(0.01f)

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
            onClick = { if (!running)
                viewModel.startDeauthScan(); scanAttempted = true; viewModel.clearDeauthNetworks() },
            modifier = Modifier,
            containerColor = if (!running) MaterialTheme.colorScheme.primary else Color.LightGray
        ) {
            Icon(
                painter = painterResource(R.drawable._3_refresh),
                contentDescription = "Sort/Refresh",
                tint = Color.White
            )
        }

        ProportionalSpacer(0.03f)

        val attackDescriptions = listOf(
            "Disrupts communication between routers and devices.",
            "Access point installed on a network without authorization.",
            "Merges Broadcast and Rogue AP attacks."
        )

        OptionsButtonSegment(
            title = stringResource(R.string.wifi_deauth_attack_type),
            names = typeOfAttack,
            disabled = running || !networkIsSelected,
            selectedIndex = attackIndex,
            modifier = Modifier.fillMaxWidth(),
            onSelectionChanged = { index, _ ->
                Log.d("UI", "UI index = $index")
                viewModel.setAttackType(index)
                screenViewModel.updateAttackIndex(index)
                Log.d("UI", "attackIndex = $attackIndex")
            },
            getTooltipText = { index ->
                attackDescriptions.getOrElse(index) { "" }
            }
        )

        ProportionalSpacer(0.01f)

        // --- Columna derecha: panel grande ---
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            InformationLabel(
                modifier = Modifier.fillMaxSize(),
                loadingText = "Scanning for Nearby Networks...",
                loading = scanLoading
            ) {
                if (networks.isEmpty()){
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

                networks.forEach { network ->
                    val isSelected = selectedNetwork == network.ssid

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!running) {
                                    screenViewModel.updateIsNetworkSelected(true)
                                    screenViewModel.updateSelectedNetwork(network.ssid)
                                    viewModel.setDeauthTarget(network.bssid)
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

        ProportionalSpacer(0.02f)

        StartButton(
            text = if (running) "Stop" else "Start",
            command = { onToggleRunning()
                if (!running)
                    viewModel.startDeauthAttack()
                else viewModel.deauthStopAttackRequest()}
        )


    }
}