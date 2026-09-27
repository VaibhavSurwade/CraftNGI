package com.example.craftadmin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = NavyDark,
    secondary = GoldAccent,
    onSecondary = Color.Black,
    tertiary = ForestGreen,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderColor
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF63B3ED),
    onPrimary = Color.Black,
    secondary = GoldAccent,
    background = Color(0xFF1A202C),
    surface = Color(0xFF2D3748),
    onBackground = Color(0xFFF7FAFC),
    onSurface = Color(0xFFF7FAFC)
)

@Composable
fun CraftAdminTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
