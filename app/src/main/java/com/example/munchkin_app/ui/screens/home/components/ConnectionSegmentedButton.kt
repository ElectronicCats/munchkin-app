package com.example.munchkin_app.ui.screens.home.components

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R

enum class ConnectionOption(
    val label: String,
    val icon: Int,
    val contentDescription: String
) {
    SERIAL("Serial", R.drawable.icon_usb, "Serial connection"),
    BLUETOOTH("Bluetooth", R.drawable.icon_bluetooth, "Bluetooth connection"),
    NETWORK("Network", R.drawable.icon_wifi, "Network connection")
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
                    inactiveContainerColor = Color.White,
                    inactiveContentColor = Color.Black
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = ConnectionOption.entries.size
                ),
                onClick = { onSelectionChanged(option) },
                selected = option == selectedOption,
                label = { Text(option.label) },
                icon = {
                    Icon(painterResource(option.icon), contentDescription = option.contentDescription)
                }
            )
        }
    }
}
