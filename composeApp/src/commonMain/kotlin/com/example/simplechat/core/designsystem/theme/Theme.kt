package com.example.simplechat.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import org.jetbrains.compose.ui.tooling.preview.Preview

private val LightColors = lightColorScheme(
    primary = SimpleChatColors.DeepLilac,
    onPrimary = SimpleChatColors.SoftTextInverse,
    primaryContainer = SimpleChatColors.Lilac,
    onPrimaryContainer = SimpleChatColors.Slate,
    secondary = SimpleChatColors.AccentTeal,
    onSecondary = SimpleChatColors.SoftTextInverse,
    background = SimpleChatColors.LavenderBlush,
    onBackground = SimpleChatColors.SoftText,
    surface = SimpleChatColors.MintCream,
    onSurface = SimpleChatColors.SoftText,
    surfaceVariant = SimpleChatColors.MistBlue,
    onSurfaceVariant = SimpleChatColors.Slate,
    error = SimpleChatColors.AccentCoral,
    onError = SimpleChatColors.SoftText
)

private val DarkColors = darkColorScheme(
    primary = SimpleChatColors.Lilac,
    onPrimary = SimpleChatColors.MidnightBlue,
    primaryContainer = SimpleChatColors.MidnightBlue,
    onPrimaryContainer = SimpleChatColors.SoftTextInverse,
    secondary = SimpleChatColors.AccentTeal,
    onSecondary = SimpleChatColors.MidnightBlue,
    background = SimpleChatColors.Slate,
    onBackground = SimpleChatColors.SoftTextInverse,
    surface = SimpleChatColors.MidnightBlue,
    onSurface = SimpleChatColors.SoftTextInverse,
    surfaceVariant = SimpleChatColors.DeepLilac,
    onSurfaceVariant = SimpleChatColors.SoftTextInverse,
    error = SimpleChatColors.AccentCoral,
    onError = SimpleChatColors.MidnightBlue
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
