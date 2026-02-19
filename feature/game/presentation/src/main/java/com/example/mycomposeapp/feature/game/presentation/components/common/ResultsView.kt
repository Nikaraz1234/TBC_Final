package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ResultsView(
    modifier: Modifier = Modifier,
    gameResult: GameResult?,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
    isCoverMode: Boolean = false,
    isAchievementMode: Boolean = false,
    isMangaRatingMode: Boolean = false
) {
    val result = gameResult ?: return

    if (isCoverMode || isAchievementMode || isMangaRatingMode) {
        CoverResultsContent(
            result = result,
            onPlayAgain = onPlayAgain,
            onExit = onExit,
            guessedLabel = when {
                isAchievementMode -> stringResource(GameR.string.games_guessed)
                isMangaRatingMode -> "Correct Guesses"
                else -> stringResource(GameR.string.movies_guessed)
            },
            modifier = modifier
        )
    } else {
        RegularResultsContent(
            result = result,
            onPlayAgain = onPlayAgain,
            onExit = onExit,
            modifier = modifier
        )
    }
}

@Composable
private fun ResultsHeader(
    emoji: String,
    title: String,
    score: Int,
    isNewHighScore: Boolean = false
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    Spacer(modifier = Modifier.height(40.dp))

    Text(
        text = emoji,
        style = typography.displayLarge
    )

    Spacer(modifier = Modifier.height(spacing.spacing16))

    Text(
        text = title,
        color = colors.goldenYellow,
        style = typography.headlineLarge,
        textAlign = TextAlign.Center
    )

    if (isNewHighScore) {
        Spacer(modifier = Modifier.height(spacing.spacing8))
        Text(
            text = stringResource(GameR.string.new_high_score),
            color = Color(0xFFFFD700),
            style = typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(spacing.spacing8))

    Text(
        text = stringResource(GameR.string.points_format, score),
        color = colors.textLight,
        style = typography.displaySmall.copy(fontWeight = FontWeight.Bold)
    )

    Spacer(modifier = Modifier.height(spacing.spacing24))
}

@Composable
private fun ResultsStats(
    stats: List<Pair<String, String>>
) {
    val spacing = AppTheme.spacing

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.spacing12)
        ) {
            stats.forEach { (label, value) ->
                StatRow(label = label, value = value)
            }
        }
    }
}

@Composable
private fun ResultsActions(
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val spacing = AppTheme.spacing

    ButtonLarge(
        text = stringResource(GameR.string.btn_play_again),
        onClick = onPlayAgain,
        style = ButtonStyle.Filled
    )

    Spacer(modifier = Modifier.height(spacing.spacing12))

    ButtonLarge(
        text = stringResource(GameR.string.btn_back_to_menu),
        onClick = onExit,
        style = ButtonStyle.Outlined
    )

    Spacer(modifier = Modifier.height(spacing.spacing24))
}

@Composable
private fun ResultsLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun CoverResultsContent(
    modifier: Modifier = Modifier,
    result: GameResult,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
    guessedLabel: String = ""
) {
    val spacing = AppTheme.spacing
    val label = guessedLabel.ifEmpty { stringResource(GameR.string.movies_guessed) }

    ResultsLayout(modifier = modifier) {
        ResultsHeader(
            emoji = "\uD83C\uDFAC",
            title = stringResource(GameR.string.game_over),
            score = result.totalScore,
            isNewHighScore = result.isNewHighScore
        )

        ResultsStats(
            stats = listOf(
                label to "${result.correctAnswers}",
                stringResource(GameR.string.best_streak) to "${result.bestStreak}",
                stringResource(GameR.string.final_score) to "${result.totalScore}",
                stringResource(GameR.string.coins_label) to "${result.finalCoinBalance}"
            )
        )

        Spacer(modifier = Modifier.height(spacing.spacing24))

        ResultsActions(onPlayAgain = onPlayAgain, onExit = onExit)
    }
}

@Composable
private fun RegularResultsContent(
    result: GameResult,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    val percentage = if (result.totalQuestions > 0) {
        (result.correctAnswers * 100) / result.totalQuestions
    } else 0

    val gradeEmoji = when {
        percentage >= 90 -> "\uD83C\uDFC6"
        percentage >= 70 -> "\u2B50"
        percentage >= 50 -> "\uD83D\uDC4D"
        else -> "\uD83D\uDCAA"
    }

    ResultsLayout(modifier = modifier) {
        ResultsHeader(
            emoji = gradeEmoji,
            title = stringResource(GameR.string.game_complete),
            score = result.totalScore
        )

        ResultsStats(
            stats = listOf(
                stringResource(GameR.string.correct_answers) to stringResource(
                    GameR.string.correct_answers_format,
                    result.correctAnswers,
                    result.totalQuestions
                ),
                stringResource(GameR.string.accuracy) to stringResource(
                    GameR.string.accuracy_format,
                    percentage
                ),
                stringResource(GameR.string.best_streak) to "${result.bestStreak}",
                stringResource(GameR.string.time_taken) to stringResource(
                    GameR.string.time_taken_format,
                    result.timeTakenSeconds
                )
            )
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(GameR.string.answer_summary),
                    color = colors.goldenYellow,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                result.answers.forEachIndexed { _, answer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (answer.isCorrect) "\u2705" else "\u274C",
                            style = typography.titleMedium
                        )

                        Spacer(modifier = Modifier.width(spacing.spacing8))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = answer.correctAnswer,
                                color = colors.textLight,
                                style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            if (!answer.isCorrect && answer.userAnswer != null) {
                                Text(
                                    text = stringResource(
                                        GameR.string.your_answer_format,
                                        answer.userAnswer ?: ""
                                    ),
                                    color = colors.textMuted,
                                    style = typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.spacing24))

        ResultsActions(onPlayAgain = onPlayAgain, onExit = onExit)
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = colors.textMuted,
            style = typography.bodyMedium
        )
        Text(
            text = value,
            color = colors.textLight,
            style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
