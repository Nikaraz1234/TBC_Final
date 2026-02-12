package com.example.mycomposeapp.core.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun LevelBadge(
    level: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Text(
        text = "Lv. $level",
        color = colors.backgroundDark,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                color = colors.goldenYellow,
                shape = AppTheme.radius.radius8
            )
            .padding(horizontal = spacing.spacing6, vertical = spacing.spacing2)
    )
}
