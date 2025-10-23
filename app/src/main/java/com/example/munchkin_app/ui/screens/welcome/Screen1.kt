package com.example.munchkin_app.ui.screens.welcome


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.theme.MunchkinappTheme


@Composable
fun WelcomeTextOne(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .verticalScroll(rememberScrollState())
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {

            //Image()

            //Title
            ProportionalSpacer(0.05f)
            Text(
                text = stringResource(R.string.Welcome_one_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            //Body Text
            ProportionalSpacer(0.05f)
            Text(
                text = stringResource(R.string.Welcome_one_text),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ScreenOne() {
    MunchkinappTheme {
        WelcomeTextOne(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}