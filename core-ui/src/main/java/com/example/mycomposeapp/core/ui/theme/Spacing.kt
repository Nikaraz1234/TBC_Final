package com.example.mycomposeapp.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Spacing(
    val spacing1: Dp = 1.dp,
    val spacing2: Dp = 2.dp,
    val spacing3: Dp = 3.dp,
    val spacing4: Dp = 4.dp,
    val spacing5: Dp = 5.dp,
    val spacing6: Dp = 6.dp,
    val spacing8: Dp = 8.dp,
    val spacing12: Dp = 12.dp,
    val spacing16: Dp = 16.dp,
    val spacing18: Dp = 18.dp,
    val spacing20: Dp = 20.dp,
    val spacing24: Dp = 24.dp,
    val spacing28: Dp = 28.dp,
    val spacing32: Dp = 32.dp,
    val spacing48: Dp = 48.dp,
    val spacing56: Dp = 56.dp,
    val spacing80: Dp = 80.dp,
    val spacing40: Dp = 40.dp,
    val spacing10: Dp = 10.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
