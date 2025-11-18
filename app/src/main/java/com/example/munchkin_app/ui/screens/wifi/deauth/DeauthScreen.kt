package com.example.munchkin_app.ui.screens.wifi.deauth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.example.munchkin_app.R
import com.example.munchkin_app.ui.common.ApplicationTitle
import com.example.munchkin_app.ui.common.InformationLabel
import com.example.munchkin_app.ui.common.OptionsButtonSegment
import com.example.munchkin_app.ui.common.ProportionalSpacer
import com.example.munchkin_app.ui.common.StartButton
import com.example.munchkin_app.ui.common.components.MunchkinScreens
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerCompactContent
import com.example.munchkin_app.ui.screens.wifi.analyzer.layouts.AnalyzerExpandedContent
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthCompactContent
import com.example.munchkin_app.ui.screens.wifi.deauth.layouts.DeauthExpandedContent
import com.example.munchkin_app.ui.theme.MunchkinappTheme
import com.example.munchkin_app.viewmodel.screens.wifi.DeauthViewModel

@Composable
fun DeauthScreen(navController: NavHostController) {
    MunchkinScreens.ApplicationLayout(navController, "wifi") {innerPadding ->
        DeauthContents(innerPadding)
    }
}

@Composable
fun DeauthContents(
    innerPadding: PaddingValues,
    screenViewModel: DeauthViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val floatingSize = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 56.dp
        Configuration.ORIENTATION_PORTRAIT -> 45.dp
        else -> 56.dp
    }

    val floatingX = when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 40.dp
        Configuration.ORIENTATION_PORTRAIT -> 55.dp
        else -> 56.dp
    }

    val typeOfAttack = remember {
        listOf("Broadcast", "Rogue AP", "Combined")
    }

    val attackIndex by screenViewModel.attackIndex.collectAsState()

    var running by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
    ) {
        when {
            // Compact
            !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
                DeauthCompactContent(
                    innerPadding = innerPadding,
                    floatingSize = floatingSize,
                    floatingX = floatingX,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = { running = !running }
                )
            }
            // Expanded
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
                DeauthExpandedContent(
                    innerPadding = innerPadding,
                    floatingSize = floatingSize,
                    floatingX = floatingX,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = { running = !running }
                )
            }
            // Medium u otro caso
            else -> {
                DeauthCompactContent(
                    innerPadding = innerPadding,
                    floatingSize = floatingSize,
                    floatingX = floatingX,
                    typeOfAttack = typeOfAttack,
                    attackIndex = attackIndex,
                    screenViewModel = screenViewModel,
                    running = running,
                    onToggleRunning = { running = !running }
                )
            }
        }
    }
}





@Preview
@Composable
fun DeauthScreenPreview() {
    MunchkinappTheme {
        DeauthScreen(rememberNavController())
    }

}