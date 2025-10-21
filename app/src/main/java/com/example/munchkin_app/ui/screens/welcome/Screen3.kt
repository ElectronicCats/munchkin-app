package com.example.munchkin_app.ui.screens.welcome

import androidx.compose.foundation.background
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.theme.MunchkinappTheme


@Composable
fun WelcomeTextThree(
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            //Title
            Text(
                text = stringResource(R.string.Welcome_three_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Body
            Text(
                text = stringResource(R.string.Welcome_three_text),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            //Terms and conditions
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box (
                    contentAlignment = Alignment.Center,
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = {checked = it},
                        modifier = Modifier
                    )
                }
                Text(
                    text = stringResource(R.string.Welcome_three_terms),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 0.dp, bottom = 16.dp, top = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ScreenThree() {
    MunchkinappTheme {
        WelcomeTextThree(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}