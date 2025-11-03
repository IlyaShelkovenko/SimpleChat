package com.example.simplechat.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

private val LightColors = lightColorScheme(
    primary = SimpleChatColors.AuroraBlue,
    onPrimary = SimpleChatColors.SoftWhite,
    primaryContainer = SimpleChatColors.SkyMist,
    onPrimaryContainer = SimpleChatColors.Twilight,
    secondary = SimpleChatColors.CoralGlow,
    onSecondary = SimpleChatColors.SoftWhite,
    secondaryContainer = SimpleChatColors.PeachGlow,
    onSecondaryContainer = SimpleChatColors.Twilight,
    background = SimpleChatColors.SoftWhite,
    onBackground = SimpleChatColors.Twilight,
    surface = SimpleChatColors.WhisperGray,
    onSurface = SimpleChatColors.Twilight,
    surfaceVariant = SimpleChatColors.SkyMist,
    onSurfaceVariant = SimpleChatColors.Twilight,
    error = SimpleChatColors.CoralGlow,
    onError = SimpleChatColors.SoftWhite
)

private val DarkColors = darkColorScheme(
    primary = SimpleChatColors.MintGlow,
    onPrimary = SimpleChatColors.DeepSpace,
    primaryContainer = SimpleChatColors.Twilight,
    onPrimaryContainer = SimpleChatColors.SoftWhite,
    secondary = SimpleChatColors.CoralGlow,
    onSecondary = SimpleChatColors.DeepSpace,
    secondaryContainer = SimpleChatColors.Twilight,
    onSecondaryContainer = SimpleChatColors.SoftWhite,
    background = SimpleChatColors.DeepSpace,
    onBackground = SimpleChatColors.SoftWhite,
    surface = SimpleChatColors.Midnight,
    onSurface = SimpleChatColors.SoftWhite,
    surfaceVariant = SimpleChatColors.Twilight,
    onSurfaceVariant = SimpleChatColors.SlateGray,
    error = SimpleChatColors.CoralGlow,
    onError = SimpleChatColors.DeepSpace
)

@Composable
fun SimpleChatTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = SimpleChatTypography,
        content = content
    )
}

@Preview
@Composable
private fun SimpleChatThemePreview() {
    SimpleChatTheme {
        androidx.compose.material3.Surface(
            color = MaterialTheme.colorScheme.surface,
        ) {}
    }
}
