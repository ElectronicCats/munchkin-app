package com.example.munchkin_app.ui.screens.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ConnectionOption(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    SERIAL("Serial", Icons.Default.Add, "Serial connection"),
    BLUETOOTH("Bluetooth", Icons.Default.AccountBox, "Bluetooth connection"),
    NETWORK("Network", Icons.Default.Build, "Network connection")
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    selectedOption: ConnectionOption,
    onSelectionChanged: (ConnectionOption) -> Unit,
    connectionStatus: String
) {
    Text(
        text = connectionStatus,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(bottom = 8.dp)
    )

    SingleChoiceSegmentedButtonRow(
        space = -4.dp,
        modifier = modifier
    ) {
        ConnectionOption.entries.forEachIndexed { index, option ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Color(0XFF72BA63),
                    activeContentColor = Color.White,
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = ConnectionOption.entries.size
                ),
                onClick = { onSelectionChanged(option) },
                selected = option == selectedOption,
                label = { Text(option.label) },
                icon = {
                    Icon(option.icon, contentDescription = option.contentDescription)
                }
            )
        }
    }
}
