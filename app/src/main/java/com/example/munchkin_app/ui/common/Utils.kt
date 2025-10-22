package com.example.munchkin_app.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


object LayoutConfig {
    fun proportionalHeight(screenHeightDp: Int, fraction: Float): Dp {
        return (screenHeightDp * fraction).dp
    }
}

@Composable
fun ProportionalSpacer(fraction: Float) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Spacer(modifier = Modifier.height(LayoutConfig.proportionalHeight(screenHeight, fraction)))
}