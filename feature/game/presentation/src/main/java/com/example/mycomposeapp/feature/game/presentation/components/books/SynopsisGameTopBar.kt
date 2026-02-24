package com.example.mycomposeapp.feature.game.presentation.components.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun SynopsisGameTopBar(
    coins: Int,
    score: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val typography = AppTheme.typography
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Coins: $coins",
            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onBackground
        )
        Text(
            text = "Score: $score",
            style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.goldenYellow
        )
        Text(
            text = "Streak: $streak",
            style = typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onBackground
        )
    }
}
