package com.example.munchkin_app.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.screens.home.components.ConnectionSettingsScreen
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
fun HomeContent(
    innerPadding: PaddingValues,
) {
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
            style = MaterialTheme.typography.titleLarge
        )

        ProportionalSpacer(0.02f)

        ConnectionSettingsScreen()

        ProportionalSpacer(0.02f)

        Text(
            text = "File Manager",
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
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
                painter = painterResource(R.drawable.icon_folder),
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
                    painter = painterResource(R.drawable.icon_cloud),
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

