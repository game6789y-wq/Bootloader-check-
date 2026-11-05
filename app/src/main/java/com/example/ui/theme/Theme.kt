package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BootCheckColorScheme = darkColorScheme(
    primary = CyberCyanPrimary,
    onPrimary = Color(0xFF062522),
    primaryContainer = Color(0xFF174A46),
    onPrimaryContainer = Color(0xFFB5FFF2),
    secondary = CyberPurple,
    onSecondary = Color(0xFF211A3A),
    secondaryContainer = CyberPurpleContainer,
    onSecondaryContainer = Color(0xFFE9E0FF),
    tertiary = CyberGreenSuccess,
    onTertiary = Color(0xFF08291A),
    tertiaryContainer = CyberGreenContainer,
    onTertiaryContainer = Color(0xFFB9F7D2),
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberRedAlert,
    onError = Color(0xFF3A0710),
    errorContainer = CyberRedContainer,
    onErrorContainer = Color(0xFFFFDCE0),
    outline = CyberBorder,
    outlineVariant = CyberBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BootCheckColorScheme,
        typography = Typography,
        content = content
    )
}
