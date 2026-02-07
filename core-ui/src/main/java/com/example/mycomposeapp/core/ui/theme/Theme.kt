package com.example.mycomposeapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme()

@Composable
fun MyComposeAppTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppColorScheme provides AppColorScheme(),
        LocalSpacing provides Spacing(),
        LocalRadius provides Radius()
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
