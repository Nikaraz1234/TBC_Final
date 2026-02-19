package com.example.mycomposeapp.feature.game.presentation.components.plot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun GameTopBar(
    questionIndex: Int,
    totalQuestions: Int,
    timeRemaining: Int,
    streak: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(
                    GameR.string.question_progress_format,
                    questionIndex + 1,
                    totalQuestions
                ),
                color = colors.textLight,
                style = typography.bodyMedium
            )

            TimerBadge(timeRemaining = timeRemaining)

            if (streak > 1) {
                Text(
                    text = "\uD83D\uDD25 $streak",
                    color = colors.goldenYellow,
                    style = typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = colors.goldenYellow,
            trackColor = colors.glassWhite
        )
    }
}
