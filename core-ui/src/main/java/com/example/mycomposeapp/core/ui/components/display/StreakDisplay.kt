package com.example.mycomposeapp.core.ui.components.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun StreakDisplay(
    streak: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp
) {
    if (streak > 1) {
        Text(
            text = "\uD83D\uDD25 $streak",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.goldenYellow,
            modifier = modifier
        )
    }
}
