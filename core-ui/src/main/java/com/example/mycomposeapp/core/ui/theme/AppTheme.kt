package com.example.mycomposeapp.core.ui.theme

import androidx.compose.runtime.Composable

object AppTheme {
    val colors: AppColorScheme
        @Composable
        get() = LocalAppColorScheme.current

    val spacing: Spacing
        @Composable
        get() = LocalSpacing.current

    val radius: Radius
        @Composable
        get() = LocalRadius.current
}
