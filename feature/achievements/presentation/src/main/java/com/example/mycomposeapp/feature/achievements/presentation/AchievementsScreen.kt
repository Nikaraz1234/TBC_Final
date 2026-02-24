package com.example.mycomposeapp.feature.achievements.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementConditionType
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.feature.achievements.presentation.components.AchievementCard
import com.example.mycomposeapp.feature.achievements.presentation.components.AchievementFilterRow
import com.example.mycomposeapp.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier,
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is AchievementsContract.SideEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message.asString(context))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing16, vertical = spacing.spacing16),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.achievements_title),
                    style = typography.headlineSmall,
                    color = colors.textLight
                )
                if (!state.isLoading && state.achievements.isNotEmpty()) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = "${state.unlockedCount} / ${state.achievements.size}",
                                style = typography.labelMedium,
                                color = colors.goldenYellow
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = colors.glassWhite
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = colors.glassBorder
                        )
                    )
                }
                if (BuildConfig.DEBUG) {
                    IconButton(
                        onClick = { viewModel.onEvent(AchievementsContract.Event.SeedAchievements) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Seed achievements",
                            tint = colors.goldenYellow
                        )
                    }
                }
            }

            // Category filter
            AchievementFilterRow(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { category ->
                    viewModel.onEvent(AchievementsContract.Event.SelectCategory(category))
                }
            )

            Spacer(modifier = Modifier.height(spacing.spacing12))

            // Completion filter
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.spacing16)
            ) {
                AchievementsContract.CompletionFilter.entries.forEachIndexed { index, filter ->
                    SegmentedButton(
                        selected = state.completionFilter == filter,
                        onClick = {
                            viewModel.onEvent(AchievementsContract.Event.SetCompletionFilter(filter))
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = AchievementsContract.CompletionFilter.entries.size
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = colors.goldenYellow,
                            activeContentColor = colors.black,
                            inactiveContainerColor = colors.glassWhite,
                            inactiveContentColor = colors.textMuted
                        )
                    ) {
                        Text(
                            text = filter.label,
                            style = typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.spacing12))

            // Content
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.goldenYellow)
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorMessage
                                ?: stringResource(R.string.achievement_error_loading),
                            color = colors.error,
                            style = typography.bodyMedium
                        )
                    }
                }

                state.filteredAchievements.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.achievement_empty_state),
                            color = colors.textMuted,
                            style = typography.bodyMedium
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = spacing.spacing16,
                            vertical = spacing.spacing8
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.spacing8)
                    ) {
                        items(
                            items = state.filteredAchievements,
                            key = { it.id }
                        ) { achievement ->
                            AchievementCard(
                                achievement = achievement,
                                isUnlocked = achievement.id in state.unlockedIds,
                                currentProgress = resolveProgress(achievement, state.userStats)
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun resolveProgress(achievement: AppAchievement, stats: UserStats?): Int {
    if (stats == null) return 0
    return when (achievement.conditionType) {
        AchievementConditionType.GAMES_PLAYED    -> stats.gamesPlayed
        AchievementConditionType.CORRECT_ANSWERS -> stats.correctAnswers
        AchievementConditionType.BEST_STREAK     -> stats.bestStreak
        AchievementConditionType.TOTAL_XP        -> stats.totalXp
        AchievementConditionType.LEVEL_REACHED   -> stats.level
        AchievementConditionType.POINTS_SCORED   -> stats.points
        AchievementConditionType.HIGH_SCORE      -> {
            val key = achievement.conditionKey ?: return 0
            stats.highScore[key] ?: 0
        }
    }
}
