package com.example.mycomposeapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MyComposeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) AppColorScheme.dark() else AppColorScheme.light()

    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = appColors.goldenYellow,
            onPrimary = appColors.onPrimary,
            secondary = appColors.goldenYellowDark,
            onSecondary = appColors.onPrimary,

            tertiary = appColors.gold3,

            background = appColors.background,
            onBackground = appColors.onBackground,

            surface = appColors.surface,
            onSurface = appColors.onSurface,

            surfaceVariant = appColors.surfaceVariant,
            outline = appColors.outline,

            error = appColors.error,
            onError = appColors.white
        )
    } else {
        lightColorScheme(
            primary = appColors.goldenYellow,
            onPrimary = appColors.onPrimary,
            secondary = appColors.goldenYellowDark,
            onSecondary = appColors.onPrimary,

            tertiary = appColors.gold3,

            background = appColors.background,
            onBackground = appColors.onBackground,

            surface = appColors.surface,
            onSurface = appColors.onSurface,

            surfaceVariant = appColors.surfaceVariant,
            outline = appColors.outline,

            error = appColors.error,
            onError = appColors.white
        )
    }

    CompositionLocalProvider(
        LocalAppColorScheme provides appColors,
        LocalAppTypography provides DefaultAppTypography,
        LocalSpacing provides Spacing(),
        LocalRadius provides Radius()
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = DefaultAppTypography.toMaterial3Typography(),
            content = content
        )
    }
}
