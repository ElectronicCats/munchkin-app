package com.example.munchkin_app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = Audimat,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.01.em
    ),
    titleMedium = TextStyle(
        fontFamily = Audimat,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.01.em
    ),
    titleSmall = TextStyle(
        fontFamily = Audimat,
        fontWeight = FontWeight.Light,
        letterSpacing = 0.01.em
    ),
    bodyLarge = TextStyle(
        fontFamily = Metropolis,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.01.em
    ),
    bodyMedium = TextStyle(
        fontFamily = Metropolis,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.01.em
    ),
    bodySmall = TextStyle(
        fontFamily = Metropolis,
        fontWeight = FontWeight.Light,
        letterSpacing = 0.01.em
    )


    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)