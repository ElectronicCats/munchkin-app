package com.example.munchkin_app.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.munchkin_app.ui.theme.DarkOnBackground

private val DarkColorScheme = darkColorScheme(

    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground
)

private val LightColorScheme = lightColorScheme(

    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    onBackground = LightOnBackground

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MunchkinappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    //Gathers the screen size
    val configuration = LocalConfiguration.current
    val smallestWidthDp = configuration.smallestScreenWidthDp
    //Scale based on screen size
    val screenScale = when {
        smallestWidthDp >= 900 -> 2.0f  // Large table
        smallestWidthDp >= 600 -> 1.5f // Small tablet or Landscape
        else -> 1f
    }
    //Gathers the font scale in case the cellphone has one
    val fontScale = configuration.fontScale
    //To obtain the desired size
    val combinedScale = screenScale / fontScale

    //Typography's modified to be responsive
    val scaledTypography = Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 24.sp * combinedScale, lineHeight = 30.sp * combinedScale),
        titleMedium = Typography.titleMedium.copy(fontSize = 24.sp * combinedScale, lineHeight = 30.sp * combinedScale),
        bodyMedium = Typography.bodyMedium.copy(fontSize = 16.sp * combinedScale, lineHeight = 25.sp * combinedScale),
        bodySmall = Typography.bodySmall.copy(fontSize = 12.sp * combinedScale, lineHeight = 25.sp * combinedScale),
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}