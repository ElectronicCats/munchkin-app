package com.example.munchkin_app.ui.screens.wifi.deauth.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel

@Composable
fun DeauthCompactContent(
    innerPadding: PaddingValues,
    floatingX: Dp,
    floatingSize: Dp,
    typeOfAttack: List<String>,
    attackIndex: Int,
    screenViewModel: DeauthViewModel,
    running: Boolean,
    onToggleRunning: () -> Unit,
){
    Column (
        modifier = Modifier.padding(innerPadding)
            .verticalScroll(rememberScrollState())
    ){

        ApplicationTitle("WiFi", "Deauth")

        ProportionalSpacer(0.03f)

        OptionsButtonSegment(
            title = "Type of Attack",
            names = typeOfAttack,
            selectedIndex = attackIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onSelectionChanged = {index, name ->
                screenViewModel.updateAttackIndex(index)
            }
        )

        ProportionalSpacer(0.02f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ){
            InformationLabel(
                modifier = Modifier,
                information = "Attack is running",
                loadingText = "Loading..."
            )

            FloatingActionButton(
                onClick = { /* Acción del botón */ },
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

        ProportionalSpacer(0.05f)

        if (running) {
            StartButton(
                text = "Stop",
                command = { onToggleRunning() }
            )
        } else {
            StartButton(
                text = "Start",
                command = { onToggleRunning() }
            )
        }
    }
}