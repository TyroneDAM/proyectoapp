package com.example.bookcloudapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF4973B),
    onPrimary = Color.White,
    background = Color(0xFFF2F2F2),
    onBackground = Color(0xFF222222),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD36E),
    onPrimary = Color(0xFF222222),
    background = Color(0xFF222222),
    onBackground = Color.White,
)

@Composable
fun BookcloudAppTheme(
    darkTheme: Boolean = false, // ← forzar claro
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZorroTypography,
        content = content
    )
}
