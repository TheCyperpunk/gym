package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NomadLightColorScheme = lightColorScheme(
    primary = NomadSignal,
    onPrimary = Color.White,
    primaryContainer = NomadSignal.copy(alpha = 0.12f),
    onPrimaryContainer = NomadSignal,
    secondary = NomadSteel,
    onSecondary = Color.White,
    secondaryContainer = NomadConcrete,
    onSecondaryContainer = NomadInk,
    tertiary = NomadMoss,
    onTertiary = Color.White,
    background = NomadConcrete,
    onBackground = NomadInk,
    surface = NomadMist,
    onSurface = NomadInk,
    surfaceVariant = NomadConcrete,
    onSurfaceVariant = NomadSteel,
    outline = NomadLine,
    outlineVariant = NomadFog.copy(alpha = 0.4f),
    error = NomadBrick,
    onError = Color.White
)

private val NomadDarkColorScheme = darkColorScheme(
    primary = NomadSignal,
    onPrimary = Color.White,
    primaryContainer = NomadSignal.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = NomadFog,
    onSecondary = NomadInk,
    background = NomadInk,
    onBackground = NomadMist,
    surface = NomadCardDark,
    onSurface = NomadMist,
    surfaceVariant = NomadCardDarkSurface,
    onSurfaceVariant = NomadFog,
    outline = NomadSteel,
    error = NomadBrick,
    onError = Color.White
)

@Composable
fun NomadFitTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NomadDarkColorScheme else NomadLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NomadTypography,
        content = content
    )
}
