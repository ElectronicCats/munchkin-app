package com.example.munchkin_app.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun proportionalHeight(screenHeightDp: Int, fraction: Float): Dp {
    return (screenHeightDp * fraction).dp
}

@Composable
fun ApplicationTitle(
    application: String,
    applicationName: String
){
    Text(
        text = application,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
    Text(
        text = applicationName,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun OptionsButtonSegment(
    names: List<String>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onSelectionChanged: (Int, String) -> Unit,
    title: String = "Destination",
    disabled: Boolean = false,
    getTooltipText: (index: Int) -> String = { "" }
) {
    var showTooltipIndex by remember { mutableStateOf<Int?>(null) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showTooltipIndex = null
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ProportionalSpacer(0.03f)
            
            SingleChoiceSegmentedButtonRow(
                space = (-4).dp,
                modifier = modifier
            ) {
                names.forEachIndexed { index, name ->
                    SegmentedButton(
                        modifier = Modifier
                            .weight(1f),
                        enabled = !disabled,
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Color(0XFF72BA63),
                            activeContentColor = Color.White,
                            inactiveContainerColor = Color.White,
                            inactiveContentColor = Color.Black,
                            disabledInactiveContainerColor = Color.White,
                            disabledActiveContainerColor = Color.LightGray,
                            disabledActiveBorderColor = MaterialTheme.colorScheme.primary,
                            disabledInactiveBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = names.size
                        ),
                        onClick = {
                            onSelectionChanged(index, name)
                            showTooltipIndex = null
                        },
                        selected = index == selectedIndex,
                        label = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .fillMaxHeight()
                                    .combinedClickable(
                                        hapticFeedbackEnabled = false,
                                        enabled = !disabled,
                                        onClick = {
                                            onSelectionChanged(index, name)
                                            showTooltipIndex = null
                                        },
                                        onLongClick = {
                                            showTooltipIndex = index
                                        },
                                        interactionSource = interactionSource,
                                        indication = null
                                    )
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                    )
                }
            }
        }

        showTooltipIndex?.let { index ->
            val tooltipText = getTooltipText(index)

            if (tooltipText.isNotEmpty()) {
                ProportionalSpacer(0.01f)
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 40.dp)
                        .align(Alignment.TopCenter)
                        .zIndex(10f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = tooltipText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }

    }

}

@Composable
fun StartButton(
    text: String,
    command: () -> Unit,
    disabled: Boolean = true
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Box(
            modifier = Modifier
                .weight(0.2f)
                .align(Alignment.CenterVertically)
        )
        Button(
            modifier = Modifier
                .weight(0.6f),
            onClick = { command() },
            enabled = disabled,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.LightGray,
            )
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Box(
            modifier = Modifier
                .weight(0.2f)
        )
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun IndeterminateCircularIndicator(loading: Boolean, fraction: Float, stroke: Float) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val baseSize = if (screenWidth < screenHeight) screenWidth else screenHeight
    val circleSize = baseSize * fraction

    if (!loading) return

    CircularProgressIndicator(
        modifier = Modifier
            .padding(16.dp)
            .size(circleSize),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeWidth = circleSize * stroke
    )
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ProportionalSpacer(fraction: Float) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Spacer(modifier = Modifier.height(proportionalHeight(screenHeight, fraction)))
}



