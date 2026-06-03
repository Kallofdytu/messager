package com.zchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A237E),
    onPrimary = Color.White,
    secondary = Color(0xFFFFD600),
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5C6BC0),
    onPrimary = Color.White,
    secondary = Color(0xFFFFD600),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
)

@Composable
fun ZChatTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colorScheme, content = content)
}
