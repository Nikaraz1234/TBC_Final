package com.example.mycomposeapp.feature.game.presentation.components.games.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonSmall
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun AchievementQuestionView(
    content: QuestionContent.Achievements,
    achievementState: GameContract.ModeState.Achievement,
    isRevealed: Boolean,
    onUseHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val visibleCount = if (isRevealed) content.achievements.size
    else achievementState.visibleAchievementCount.coerceAtMost(content.achievements.size)
    val visibleAchievements = content.achievements.take(visibleCount)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cover image - only shown after answer reveal
        if (isRevealed && content.coverImageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(width = 140.dp, height = 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = content.coverImageUrl,
                    contentDescription = stringResource(GameR.string.movie_poster_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Achievement count
        Text(
            text = stringResource(
                GameR.string.achievement_count_format,
                visibleCount,
                content.achievements.size
            ),
            color = colors.textMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Achievements card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 350.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                visibleAchievements.forEachIndexed { index, achievement ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = colors.glassWhite,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    AchievementRow(
                        iconUrl = achievement.iconUrl,
                        name = achievement.name,
                        description = achievement.description,
                        unlockPercentage = achievement.unlockPercentage
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Hint button
        if (!isRevealed && achievementState.visibleAchievementCount < GameConstants.ACHIEVEMENT_MAX_VISIBLE) {
            ButtonSmall(
                text = stringResource(GameR.string.hint_cost_format, achievementState.revealCost),
                onClick = onUseHint,
                style = ButtonStyle.Outlined,
                enabled = achievementState.canRevealMore
            )

            if (achievementState.showInsufficientFundsWarning) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(GameR.string.not_enough_coins),
                    color = AppTheme.colors.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AchievementRow(
    iconUrl: String,
    name: String,
    description: String,
    unlockPercentage: Float,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Achievement icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            if (iconUrl.isNotEmpty()) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            } else {
                Text(
                    text = "\uD83C\uDFC6",
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = colors.textLight,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            if (unlockPercentage > 0f) {
                Text(
                    text = stringResource(
                        GameR.string.achievement_unlock_format,
                        unlockPercentage
                    ),
                    color = colors.textMuted,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}
