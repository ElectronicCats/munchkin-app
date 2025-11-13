package com.example.munchkin_app.ui.common

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
    title: String = "Destination"
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )

    SingleChoiceSegmentedButtonRow(
        space = (-4).dp,
        modifier = modifier
    ) {
        names.forEachIndexed { index, name ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0XFF72BA63),
                    activeContentColor = Color.White,
                    inactiveContainerColor = Color.White,
                    inactiveContentColor = Color.Black
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = names.size
                ),
                onClick = {
                    onSelectionChanged(index, name)
                },
                selected = index == selectedIndex,
                label = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                },
            )
        }
    }
}

@Composable
fun InformationLabel(
    modifier: Modifier = Modifier,
    information: String,
    loading: Boolean = false,
    loadingText: String = "Scanning..."
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Information",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        ProportionalSpacer(0.05f)

        Card(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            // scrollable text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (loading) {
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
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            text = loadingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        text = information.ifEmpty { "No networks found" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StartButton(
    text: String,
    command: () -> Unit
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
            onClick = { command() }
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

    // 🔧 Usa el lado más corto
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

