package com.example.craftscanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF380D00),
    secondary = ForestGreenGI,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    background = CreamBackground,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = Color.Black,
    secondary = Color(0xFF81C784),
    tertiary = GoldAccent,
    background = Color(0xFF141211),
    surface = Color(0xFF1E1B19),
    onBackground = Color(0xFFEDE0D8),
    onSurface = Color(0xFFEDE0D8),
    outline = Color(0xFF4A453F)
)

@Composable
fun CraftScannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
