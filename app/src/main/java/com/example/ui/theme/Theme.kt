package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PolishSoftBlue,
    secondary = PolishLightPurple,
    tertiary = PolishPurple,
    background = ClinicalDarkBg,
    surface = ClinicalDarkSurface,
    onPrimary = PolishNavy,
    onSecondary = PolishNavy,
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1),
    error = MedicalAlertRed
)

private val LightColorScheme = lightColorScheme(
    primary = PolishNavy,
    secondary = PolishSoftBlue,
    tertiary = PolishPurple,
    background = ClinicalLightBg,
    surface = ClinicalLightSurface,
    onPrimary = Color.White,
    onSecondary = PolishNavy,
    onBackground = PolishTextDark,
    onSurface = PolishTextDark,
    error = MedicalAlertRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
