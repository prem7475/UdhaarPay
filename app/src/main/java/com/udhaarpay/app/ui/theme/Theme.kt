package com.udhaarpay.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val DarkLuxuryColorScheme = darkColorScheme(
    primary = KotakRed500,
    onPrimary = OnDarkPrimary,
    primaryContainer = KotakRed700,
    onPrimaryContainer = OnDarkPrimary,
    secondary = WhiteMuted,
    onSecondary = Coal900,
    tertiary = WhiteSoft,
    onTertiary = Coal950,
    background = Coal950,
    onBackground = OnDarkPrimary,
    surface = Coal900,
    onSurface = OnDarkPrimary,
    surfaceVariant = Coal850,
    onSurfaceVariant = OnDarkSecondary,
    outline = OutlineDark,
    error = Error500,
    onError = OnDarkPrimary
)

private val LightLuxuryColorScheme = lightColorScheme(
    primary = KotakRed600,
    onPrimary = White100,
    primaryContainer = Color(0xFFFFE3E5),
    onPrimaryContainer = Color(0xFF4A0003),
    secondary = KotakRed500,
    onSecondary = White100,
    tertiary = Coal900,
    onTertiary = White100,
    background = White100,
    onBackground = Coal900,
    surface = WhiteSoft,
    onSurface = Coal900,
    surfaceVariant = Color(0xFFF3F3F8),
    onSurfaceVariant = Color(0xFF4C4C55),
    outline = Color(0xFFD2D2DA),
    error = Error500,
    onError = White100
)

object UdhaarPayBrushes {
    val AppBackground = Brush.verticalGradient(
        colors = listOf(Coal950, Coal900, Color(0xFF22080E))
    )

    val PremiumCard = Brush.linearGradient(
        colors = listOf(Coal850, Color(0xFF2F0A11), Coal900)
    )

    val AccentGlow = Brush.linearGradient(
        colors = listOf(KotakRed300, KotakRed500, KotakRed700)
    )

    val PrimaryButton = Brush.horizontalGradient(
        colors = listOf(KotakRed700, KotakRed500)
    )
}

@Composable
fun UdhaarPayTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkLuxuryColorScheme else LightLuxuryColorScheme,
        typography = UdhaarPayTypography,
        shapes = UdhaarPayShapes,
        content = content
    )
}
