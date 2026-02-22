package com.example.mycomposeapp.feature.achievements.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.feature.achievements.presentation.R

@Composable
fun AchievementCard(
    achievement: AppAchievement,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = null
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(spacing.spacing56)
                    .clip(radius.radius12)
                    .background(colors.backgroundDark)
                    .alpha(if (isUnlocked) 1f else 0.4f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.icon.ifEmpty { "\uD83C\uDFC6" },
                    style = typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(spacing.spacing12))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    color = if (isUnlocked) colors.textLight else colors.textMuted,
                    style = typography.titleSmall
                )
                Text(
                    text = achievement.description,
                    color = colors.textMuted,
                    style = typography.bodySmall,
                    modifier = Modifier.padding(top = spacing.spacing2)
                )
                Text(
                    text = stringResource(R.string.achievement_xp_reward_format, achievement.xpReward),
                    color = colors.goldenYellow,
                    style = typography.labelSmall,
                    modifier = Modifier.padding(top = spacing.spacing4)
                )
            }

            Spacer(modifier = Modifier.width(spacing.spacing12))

            // Status icon
            if (isUnlocked) {
                Icon(
                    painter = painterResource(CoreUiR.drawable.ic_check),
                    contentDescription = stringResource(R.string.achievement_completed_desc),
                    tint = colors.successGreen,
                    modifier = Modifier.size(spacing.spacing24)
                )
            } else {
                Icon(
                    painter = painterResource(CoreUiR.drawable.ic_lock),
                    contentDescription = stringResource(R.string.achievement_locked_desc),
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(spacing.spacing20)
                        .alpha(0.5f)
                )
            }
        }
    }
}
