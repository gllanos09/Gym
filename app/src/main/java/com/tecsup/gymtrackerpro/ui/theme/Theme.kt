package com.tecsup.gymtrackerpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GymPrimary,
    onPrimary = GymOnPrimary,
    secondary = GymSecondary,
    onSecondary = GymOnPrimary,
    background = GymBackground,
    onBackground = GymWhite,
    surface = GymSurface,
    onSurface = GymWhite,
    surfaceVariant = GymSurface,
    onSurfaceVariant = GymSecondary
)

@Composable
fun GymTrackerProTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
