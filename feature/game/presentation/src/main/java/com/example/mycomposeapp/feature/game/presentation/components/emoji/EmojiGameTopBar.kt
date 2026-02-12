package com.example.mycomposeapp.feature.game.presentation.components.emoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.display.CoinDisplay
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun EmojiGameTopBar(
    guessesRemaining: Int,
    coins: Int,
    isFromArchive: Boolean,
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
            Text(
                text = if (isFromArchive) stringResource(GameR.string.mode_archive) else stringResource(GameR.string.mode_daily_emoji),
                color = if (isFromArchive) colors.textMuted else colors.goldenYellow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            CoinDisplay(coins = coins, iconSize = 20.dp, fontSize = 14.sp, formatted = false)
        }
    }
}
