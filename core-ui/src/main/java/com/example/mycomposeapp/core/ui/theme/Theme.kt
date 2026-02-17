package com.example.mycomposeapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun MyComposeAppTheme(
    content: @Composable () -> Unit
) {
    val appColors = AppColorScheme()

    val materialColorScheme = darkColorScheme(
        primary = appColors.goldenYellow,
        onPrimary = appColors.backgroundDark,
        secondary = appColors.goldenYellowDark,
        onSecondary = appColors.backgroundDark,
        background = appColors.backgroundDark,
        onBackground = appColors.textLight,
        surface = appColors.backgroundDarkEnd,
        onSurface = appColors.textLight,
        error = appColors.error,
        onError = appColors.white
    )

    CompositionLocalProvider(
        LocalAppColorScheme provides appColors,
        LocalSpacing provides Spacing(),
        LocalRadius provides Radius()
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
