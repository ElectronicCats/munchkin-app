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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R

enum class ConnectionOption(
    val label: Int,
    val icon: Int,
    val contentDescription: Int
) {
    SERIAL(R.string.home_serial,
        R.drawable.icon_usb,
        R.string.home_serial_description),
    BLUETOOTH(R.string.home_bluetooth,
        R.drawable.icon_bluetooth,
        R.string.home_bluetooth_description),
    NETWORK(R.string.home_network,
        R.drawable.icon_wifi,
        R.string.home_network_description)
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
        space = (-4).dp,
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
                label = { Text(stringResource(option.label)) },
                icon = {
                    Icon(
                        painter = painterResource(option.icon),
                        contentDescription = stringResource(option.contentDescription))
                }
            )
        }
    }
}
