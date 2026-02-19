package com.example.mycomposeapp.feature.game.presentation.components.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.components.display.CoinDisplay
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.core.ui.components.display.HeartIndicator
import com.example.mycomposeapp.core.ui.components.display.StreakDisplay
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme

@Composable
fun CoverGameTopBar(
    livesRemaining: Int,
    coins: Int,
    score: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = spacing.spacing16,
                vertical = spacing.spacing12
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            HeartIndicator(remaining = livesRemaining)

            CoinDisplay(
                coins = coins,
                iconSize = 20.dp,
                fontSize = typography.labelLarge.fontSize,
                formatted = false
            )

            Text(
                text = stringResource(GameR.string.score_pts_format, score),
                style = typography.labelLarge,
                color = colors.textLight
            )

            StreakDisplay(streak = streak)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoverGameTopBarPreview() {
    MyComposeAppTheme {
        Surface {
            CoverGameTopBar(
                livesRemaining = 3,
                coins = 150,
                score = 2450,
                streak = 5
            )
        }
    }
}
