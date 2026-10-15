package com.example.ui.theme

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PennyGreenPrimary,
    onPrimary = SlateSurface,
    primaryContainer = PennyGreenContainer,
    onPrimaryContainer = PennyGreenText,
    secondary = PennyGreenDark,
    onSecondary = SlateSurface,
    secondaryContainer = SlateSurfaceSubtle,
    onSecondaryContainer = CharcoalTextPrimary,
    tertiary = PennyCoralExpense,
    onTertiary = SlateSurface,
    tertiaryContainer = PennyCoralContainer,
    onTertiaryContainer = PennyCoralText,
    error = PennyCoralExpense,
    onError = SlateSurface,
    errorContainer = PennyCoralContainer,
    onErrorContainer = PennyCoralText,
    background = SlateBackground,
    onBackground = CharcoalTextPrimary,
    surface = SlateSurface,
    onSurface = CharcoalTextPrimary,
    surfaceVariant = SlateSurfaceSubtle,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PennyGreenLight,
    onPrimary = CharcoalTextPrimary,
    primaryContainer = PennyGreenDark,
    onPrimaryContainer = PennyGreenContainer,
    secondary = PennyGreenPrimary,
    onSecondary = SlateSurface,
    secondaryContainer = CharcoalTextPrimary,
    onSecondaryContainer = SlateSurfaceSubtle,
    tertiary = PennyCoralLight,
    onTertiary = CharcoalTextPrimary,
    tertiaryContainer = PennyCoralDark,
    onTertiaryContainer = PennyCoralContainer,
    error = PennyCoralLight,
    onError = CharcoalTextPrimary,
    errorContainer = PennyCoralDark,
    onErrorContainer = PennyCoralContainer,
    background = CharcoalTextPrimary,
    onBackground = SlateBackground,
    surface = Color(0xFF0F172A),
    onSurface = SlateBackground,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = SlateTextMuted,
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
