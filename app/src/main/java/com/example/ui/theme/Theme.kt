package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyanPrimary,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F59),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = CyberPurpleContainer,
    onSecondaryContainer = Color(0xFFE8DDFF),
    tertiary = CyberGreenSuccess,
    onTertiary = Color(0xFF00391A),
    tertiaryContainer = CyberGreenContainer,
    onTertiaryContainer = Color(0xFF6CF8A1),
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberRedAlert,
    onError = Color.White,
    errorContainer = CyberRedContainer,
    onErrorContainer = Color(0xFFFFDAD6),
    outline = CyberBorder
)

private val LightColorScheme = DarkColorScheme // Default to high-contrast technical theme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force sleek tech dark aesthetic for security tool
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
