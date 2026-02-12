package com.example.mycomposeapp.core.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = AppTheme.radius.radius16,
    contentPadding: PaddingValues = PaddingValues(AppTheme.spacing.spacing16),
    borderWidth: Dp = AppTheme.spacing.spacing1,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = AppTheme.colors

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = colors.glassGradient, shape = shape)
            .border(borderWidth, colors.glassBorder, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(contentPadding),
        content = content
    )
}
