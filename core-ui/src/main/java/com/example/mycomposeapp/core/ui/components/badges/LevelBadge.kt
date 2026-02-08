package com.example.mycomposeapp.core.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun LevelBadge(
    level: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Text(
        text = "Lv. $level",
        color = colors.backgroundDark,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                color = colors.goldenYellow,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
