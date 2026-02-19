package com.example.mycomposeapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

object AppTheme {
    val colors: AppColorScheme
        @Composable
        get() = LocalAppColorScheme.current

    val typography: Typography
        @Composable get() = MaterialTheme.typography

    val spacing: Spacing
        @Composable
        get() = LocalSpacing.current

    val radius: Radius
        @Composable
        get() = LocalRadius.current
}
