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
            secondary = appColors.goldenYellowDark,
            background = appColors.backgroundDark,
            surface = appColors.backgroundDarkEnd,
            onBackground = appColors.textLight,
            onSurface = appColors.textLight,
            error = appColors.error,
            onError = appColors.white
        )
    } else {
        lightColorScheme(
            primary = appColors.goldenYellow,
            secondary = appColors.goldenYellowDark,
            background = appColors.backgroundDark,
            surface = appColors.backgroundDarkEnd,
            onBackground = appColors.textLight,
            onSurface = appColors.textLight,
            error = appColors.error,
            onError = appColors.white
        )
    }

    CompositionLocalProvider(
        LocalAppColorScheme provides appColors,
        LocalSpacing provides Spacing(),
        LocalRadius provides Radius()
    ) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            content = content
        )
    }
}

