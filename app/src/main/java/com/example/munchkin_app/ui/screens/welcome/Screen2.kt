package com.example.munchkin_app.ui.screens.welcome

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.theme.MunchkinappTheme

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun WelcomeTextTwo(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val minSide = if (screenWidth < screenHeight) screenWidth else screenHeight

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                CompactContent(modifier, minSide)
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                ExpandedContent(modifier, minSide)
            }
            // Medium u otro caso
            else -> {
                CompactContent(modifier, minSide)
            }
        }
    }
}

@Composable
fun CompactContent(modifier: Modifier, minSide: Dp){
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
            //Image
            Image(
                painter = painterResource(R.drawable._1_sam_saludando),
                contentDescription = "Sam Waving Hand",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(minSide * 0.6f)
                    .aspectRatio(1f),
            )

            //Title
            ProportionalSpacer(0.1f)
            Text(
                text = stringResource(R.string.Welcome_two_title),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            //Body Text
            ProportionalSpacer(0.05f)
            Text(
                text = stringResource(R.string.Welcome_two_text),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ExpandedContent(modifier: Modifier, minSide: Dp) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen a la izquierda
            Image(
                painter = painterResource(R.drawable._1_sam_saludando),
                contentDescription = "Sam Waving Hand",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(minSide * 0.5f)
                    .aspectRatio(1f)
            )

            // Texto a la derecha
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.Welcome_two_title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start
                )

                ProportionalSpacer(0.05f)

                Text(
                    text = stringResource(R.string.Welcome_two_text),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

//Preview to see screen in Android Studio
@Preview(
    name = "Phone Preview",
    showBackground = true,
    device = Devices.PHONE
)
@Composable
fun ScreenTwo() {
    MunchkinappTheme {
        WelcomeTextTwo(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview(
    name = "Tablet Preview",
    showBackground = true,
    device = Devices.TABLET
)
@Composable
fun ScreenTwoExpandedPreview() {
    MunchkinappTheme {
        WelcomeTextTwo(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

