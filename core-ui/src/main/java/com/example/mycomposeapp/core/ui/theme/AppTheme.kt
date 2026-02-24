package com.example.mycomposeapp.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object AppTheme {
    val colors: AppColorScheme
        @Composable @ReadOnlyComposable get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current

    val spacing: Spacing
        @Composable @ReadOnlyComposable get() = LocalSpacing.current

    val radius: Radius
        @Composable @ReadOnlyComposable get() = LocalRadius.current
}