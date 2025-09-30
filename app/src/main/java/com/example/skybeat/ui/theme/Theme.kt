package com.example.skybeat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    secondary = Color(0xFF1ED760),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF282828),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF535353),
    onSurfaceVariant = Color(0xFFB3B3B3)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    background = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    surface = Color(0xFFF0F0F0),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFCCCCCC),
    onSurfaceVariant = Color(0xFF666666)
)

@Composable
fun SkybeatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}