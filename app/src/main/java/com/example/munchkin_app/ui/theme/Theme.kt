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
    secondary = DarkSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground
)

private val LightColorScheme = lightColorScheme(

    primary = LightPrimary,
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
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp

    // Escala basada en el ancho de pantalla
    val fontScale = when {
        screenWidthDp >= 900 -> 3.5f  // tablet grande
        screenWidthDp >= 600 -> 2.5f // tablet pequeña o landscape
        else -> 1f                    // celular
    }

    val scaledTypography = Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 24.sp * fontScale, lineHeight = 30.sp * fontScale),
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