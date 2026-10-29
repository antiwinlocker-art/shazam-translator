package com.example.shazamtrans.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF1A1A2E),
    primaryContainer = Color(0xFF3A2E5C),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF0F0C29),
    surface = Color(0xFF1A1730),
    onSurface = Color(0xFFEDEAF6),
    onBackground = Color(0xFFEDEAF6),
    surfaceVariant = Color(0xFF252040),
    onSurfaceVariant = Color(0xFFC9C2DB),
    error = Color(0xFFCF6679)
)

@Composable
fun ShazamTransTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}