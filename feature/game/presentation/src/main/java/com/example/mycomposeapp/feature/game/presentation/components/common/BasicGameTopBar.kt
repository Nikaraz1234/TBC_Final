package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.display.CoinDisplay
import com.example.mycomposeapp.core.ui.components.display.HeartIndicator
import com.example.mycomposeapp.core.ui.components.display.StreakDisplay
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun BasicGameTopBar(
    livesRemaining: Int,
    coins: Int,
    score: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = spacing.spacing16, vertical = spacing.spacing12)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeartIndicator(remaining = livesRemaining)

            CoinDisplay(coins = coins, iconSize = 20.dp, fontSize = 14.sp, formatted = false)

            Text(
                text = stringResource(GameR.string.score_pts_format, score),
                color = colors.textLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            StreakDisplay(streak = streak)
        }
    }
}