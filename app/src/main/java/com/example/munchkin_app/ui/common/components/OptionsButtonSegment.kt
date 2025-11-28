package com.example.munchkin_app.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.munchkin_app.ui.common.ProportionalSpacer


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