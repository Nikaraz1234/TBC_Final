package com.example.mycomposeapp.feature.game.presentation.components.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.display.CoinDisplay
import com.example.mycomposeapp.core.ui.components.display.StreakDisplay
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun MangaRatingTopBar(
    streak: Int,
    coins: Int,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing16, vertical = spacing.spacing12)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakDisplay(streak = streak)
            CoinDisplay(coins = coins, iconSize = 20.dp, fontSize = 14.sp, formatted = false)
        }
    }
}
