package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SpotifyColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = PitchBlack,
    primaryContainer = SpotifyGreenHover,
    onPrimaryContainer = PitchBlack,
    secondary = MutedGrey,
    onSecondary = CrispWhite,
    secondaryContainer = ElevatedGrey,
    onSecondaryContainer = CrispWhite,
    background = PitchBlack,
    onBackground = CrispWhite,
    surface = SurfaceCharcoal,
    onSurface = CrispWhite,
    surfaceVariant = ElevatedGrey,
    onSurfaceVariant = MutedGrey,
    outline = DividerGrey,
    outlineVariant = HighlightGrey
)

@Composable
fun SpotiflyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SpotifyColorScheme,
        typography = SpotifyTypography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SpotiflyTheme(content = content)
}

