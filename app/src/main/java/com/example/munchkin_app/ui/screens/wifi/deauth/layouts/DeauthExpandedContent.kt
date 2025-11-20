package com.example.munchkin_app.ui.screens.wifi.deauth.layouts

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.DisplayCardContainer
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel
import com.example.munchkin_app.viewmodel.usb.UsbViewModel


@Composable
fun DeauthExpandedContent(
    innerPadding: PaddingValues,
    typeOfAttack: List<String>,
    attackIndex: Int,
    screenViewModel: DeauthViewModel,
    viewModel: UsbViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
    floatingX: Dp,
    floatingSize: Dp,
) {

    // 💡 CORRECCIÓN 1: Se usa apList (lista de APs de Deauth) en lugar de wifiNetworks (lista de Analyzer)
    val aps by viewModel.deauthNetworks.collectAsState()

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
                onSelectionChanged = { index, name ->
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
                command = { onToggleRunning() }
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
            DisplayCardContainer(
                modifier = Modifier.fillMaxSize(),
                loadingText = "Loading..."
            ) {
                // 💡 CORRECCIÓN 1: Se itera sobre 'aps'
                aps.forEach { n ->
                    Text(n.ssid + n.bssid,)
                }
            }

            FloatingActionButton(
                // 💡 CORRECCIÓN 2: Se llama a la función correcta de escaneo de Deauth
                onClick = { viewModel.requestDeauthScan()},
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