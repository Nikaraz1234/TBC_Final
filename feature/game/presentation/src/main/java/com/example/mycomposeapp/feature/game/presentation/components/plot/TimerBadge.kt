package com.example.mycomposeapp.feature.game.presentation.components.plot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun TimerBadge(
    timeRemaining: Int,
    modifier: Modifier = Modifier
) {
    val typography = AppTheme.typography

    val color = when {
        timeRemaining > 20 -> Color(0xFF4CAF50)
        timeRemaining > 10 -> Color(0xFFFFC107)
        else -> Color(0xFFF44336)
    }

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${timeRemaining}s",
            color = color,
            style = typography.titleSmall
        )
    }
}
