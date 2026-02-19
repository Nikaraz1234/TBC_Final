package com.example.mycomposeapp.core.ui.components.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun StreakDisplay(
    streak: Int,
    modifier: Modifier = Modifier
) {
    if (streak > 1) {
        Text(
            text = "\uD83D\uDD25 $streak",
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.goldenYellow,
            modifier = modifier
        )
    }
}