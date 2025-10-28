package com.example.munchkin_app.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.navigation.Destination
import com.example.munchkin_app.ui.common.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.theme.MunchkinappTheme


@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        MunchkinScreens.MunchkinLayout (navController) { innerPadding ->
            HomeContent(innerPadding)
        }
    }
}

@Composable
fun HomeContent(innerPadding: PaddingValues) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.home_screen_title),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium
        )

        ProportionalSpacer(0.02f)

        ConnectionSettingsScreen()

        ProportionalSpacer(0.2f)

        Text(
            text = "File Manager",
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Access to files in your device",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Storage(Modifier.fillMaxWidth())
    }
}

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
    onSelectionChanged: (ConnectionOption) -> Unit
) {

    Text(
        text = "Not Connected",
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
    )

    SingleChoiceSegmentedButtonRow (
        space = -4.dp,
        modifier = modifier
    ){
        ConnectionOption.entries.forEachIndexed { index, option ->
            SegmentedButton(modifier = Modifier.weight(1f),
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
                    Icon(
                        option.icon,
                        contentDescription = option.contentDescription
                    )
                }
            )
        }
    }
}

@Composable
fun ConnectionSettingsScreen() {
    var selectedOption by remember { mutableStateOf(ConnectionOption.SERIAL) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SingleChoiceSegmentedButton(
            modifier = Modifier.fillMaxWidth(),
            selectedOption = selectedOption,
            onSelectionChanged = { selectedOption = it }
        )

        ProportionalSpacer(0.01f)

        when (selectedOption) {
            ConnectionOption.SERIAL -> {
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0X40BA6363)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(8.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                        Text("None Deactivated")
                    }


                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0X4072BA63)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(8.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                        Text("Port: /dev/ttyUSB0")
                    }
                }
            }
            ConnectionOption.BLUETOOTH -> {
                Column {
                    Text("Bluetooth settings")
                    Text("Scanning for devices...")
                }
            }
            ConnectionOption.NETWORK -> {
                Column {
                    Text("Network settings")
                    Text("SSID: Munchkin_Network")
                }
            }
            null -> Unit
        }
    }
}

@Composable
fun Storage(modifier: Modifier = Modifier) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp)
    ) {
        Card(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ){
                Icon(
                        modifier = Modifier
                            .padding(8.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
                )
                Text(
                    text = "Local Storage",
                    textAlign = TextAlign.Center,
                    modifier = modifier
                )
            }

            Text(
                text = "Available Space: 100GB",
                textAlign = TextAlign.Center,
                modifier = modifier
            )
            Text(
                text = "Used Space: 50GB",
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }

        Card(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ){
                Icon(
                    modifier = Modifier
                        .padding(8.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
                Text(
                    text = "Local Storage",
                    textAlign = TextAlign.Center,
                    modifier = modifier
                )
            }

            Text(
                text = "Available Space: 100GB",
                textAlign = TextAlign.Center,
                modifier = modifier
            )
            Text(
                text = "Used Space: 50GB",
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MunchkinappTheme {
        HomeScreen(rememberNavController())
    }
}

