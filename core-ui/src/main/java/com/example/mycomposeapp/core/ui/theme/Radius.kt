package com.example.mycomposeapp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

@Immutable
data class Radius(
    val radius8: RoundedCornerShape = RoundedCornerShape(8.dp),
    val radius12: RoundedCornerShape = RoundedCornerShape(12.dp),
    val radius16: RoundedCornerShape = RoundedCornerShape(16.dp),
    val radius20: RoundedCornerShape = RoundedCornerShape(20.dp),
    val radius24: RoundedCornerShape = RoundedCornerShape(24.dp),
    val radius28: RoundedCornerShape = RoundedCornerShape(28.dp),
    val radiusFull: RoundedCornerShape = RoundedCornerShape(percent = 50)
)

val LocalRadius = staticCompositionLocalOf { Radius() }
