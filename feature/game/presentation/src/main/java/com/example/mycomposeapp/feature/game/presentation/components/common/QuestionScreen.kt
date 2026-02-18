package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.components.cover.CoverGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.cover.CoverQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.emoji.EmojiGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.emoji.EmojiQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.games.achievement.AchievementGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.games.achievement.AchievementQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.games.description.DescriptionQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.games.screenshot.ScreenshotQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.plot.PlotGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.plot.PlotQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.rankle.RankleQuestionView

@Composable
fun QuestionScreen(
    state: GameContract.State,
    onEvent: (GameContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = state.currentQuestion ?: return
    val isRevealed = state.phase == GameContract.GamePhase.AnswerRevealed

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar based on mode state
        when (val mode = state.modeState) {
            is GameContract.ModeState.Emoji -> {
                EmojiGameTopBar(
                    guessesRemaining = mode.guessesRemaining,
                    coins = mode.coins,
                    isFromArchive = mode.isFromArchive
                )
            }

            is GameContract.ModeState.Cover -> {
                CoverGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.Plot -> {
                PlotGameTopBar(
                    guessesRemaining = mode.guessesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.Screenshot -> {
                CoverGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.Achievement -> {
                AchievementGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.Description -> {
                CoverGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.Rankle -> {
                CoverGameTopBar(
                    livesRemaining = mode.attemptsLeft,
                    coins = mode.coins,
                    score = 0,
                    streak = 0
                )
            }

            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val content = question.content) {
            is QuestionContent.Cover -> {
                val cover = state.coverState
                CoverQuestionView(
                    content = content,
                    revealedCells = if (isRevealed) (0..8).toSet() else cover?.revealedCells
                        ?: emptySet(),
                    coins = cover?.coins ?: 0,
                    revealCost = cover?.revealCost ?: 0,
                    canAffordReveal = cover?.canAffordReveal == true && !isRevealed,
                    onRevealMore = { onEvent(GameContract.Event.OnRevealMore) }
                )
            }

            is QuestionContent.Emoji -> {
                val emoji = state.emojiState
                EmojiQuestionView(
                    content = content,
                    guessesRemaining = emoji?.guessesRemaining ?: 0,
                    isHintUsed = emoji?.isHintUsed ?: false,
                    hintText = emoji?.hintText ?: "",
                    coins = emoji?.coins ?: 0,
                    canAffordHint = emoji?.canAffordHint ?: false,
                    isFromArchive = emoji?.isFromArchive ?: false,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Plot -> {
                val plotState = state.plotState ?: GameContract.ModeState.Plot()
                PlotQuestionView(
                    content = content,
                    plotState = plotState,
                    isRevealed = isRevealed,
                    showInsufficientFunds = plotState.showInsufficientFundsWarning,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Screenshot -> {
                val ssState = state.screenshotState ?: GameContract.ModeState.Screenshot()
                ScreenshotQuestionView(
                    content = content,
                    coins = ssState.coins,
                    hintStep = ssState.hintStep,
                    hintCost = ssState.hintCost,
                    studioHint = ssState.studioHint,
                    genreHint = ssState.genreHint,
                    yearHint = ssState.yearHint,
                    showInsufficientFunds = ssState.showInsufficientFundsWarning,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Description -> {
                val descState = state.descriptionState ?: GameContract.ModeState.Description()
                DescriptionQuestionView(
                    content = content,
                    guessesRemaining = descState.guessesRemaining,
                    coins = descState.coins,
                    hintStep = descState.hintStep,
                    hintCost = descState.hintCost,
                    studioHint = descState.studioHint,
                    genreHint = descState.genreHint,
                    yearHint = descState.yearHint,
                    showInsufficientFunds = descState.showInsufficientFundsWarning,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Achievements -> {
                val achState = state.achievementState ?: GameContract.ModeState.Achievement()
                AchievementQuestionView(
                    content = content,
                    achievementState = achState,
                    isRevealed = isRevealed,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Rankle -> {
                val rankleState = state.rankleState ?: GameContract.ModeState.Rankle()
                RankleQuestionView(
                    content = content,
                    rankleState = rankleState,
                    isRevealed = isRevealed,
                    onAnswerSubmit = { onEvent(GameContract.Event.OnAnswerSubmitted(it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val isRankleMode = state.rankleState != null

        if (isRevealed) {
            // Don't show AnswerFeedbackView for Rankle — it has its own result card
            if (!isRankleMode) {
                AnswerFeedbackView(
                    isCorrect = state.isAnswerCorrect,
                    correctAnswer = question.correctAnswer,
                    userAnswer = state.userAnswer
                )
            }

            // Show coin reward for emoji mode daily
            if (state.isEmojiMode && state.isAnswerCorrect && state.emojiState?.isFromArchive != true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(GameR.string.coins_reward),
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            NextButton(
                isLastQuestion = when {
                    state.isCoverMode -> state.coverState?.livesRemaining == 0
                    state.isPlotMode -> state.plotState?.guessesRemaining == 0
                    state.isAchievementMode -> state.achievementState?.livesRemaining == 0
                    state.screenshotState != null -> state.screenshotState?.livesRemaining == 0
                    state.descriptionState != null -> state.descriptionState?.livesRemaining == 0
                    isRankleMode -> state.rankleState?.livesRemaining == 0
                        || (!state.isAnswerCorrect && (state.rankleState?.livesRemaining ?: 0) <= 1)
                    state.isEmojiMode -> true
                    else -> state.currentQuestionIndex >= state.questions.size - 1
                },
                buttonText = if (state.isEmojiMode) stringResource(GameR.string.btn_done) else null,
                onClick = { onEvent(GameContract.Event.OnNextQuestion) }
            )
        } else if (!isRankleMode) {
            // Rankle has its own input — skip shared AnswerInputView
            val achievementMode = state.achievementState
            if (achievementMode != null && achievementMode.livesRemaining < GameConstants.ACHIEVEMENT_INITIAL_LIVES) {
                Text(
                    text = stringResource(
                        GameR.string.lives_remaining_format,
                        achievementMode.livesRemaining,
                        stringResource(if (achievementMode.livesRemaining == 1) GameR.string.life_singular else GameR.string.lives_plural)
                    ),
                    color = Color(0xFFF44336),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            val cover = state.coverState
            if (cover != null && cover.livesRemaining < 3) {
                Text(
                    text = stringResource(
                        GameR.string.lives_remaining_format,
                        cover.livesRemaining,
                        stringResource(if (cover.livesRemaining == 1) GameR.string.life_singular else GameR.string.lives_plural)
                    ),
                    color = Color(0xFFF44336),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            AnswerInputView(
                userAnswer = state.userAnswer,
                onAnswerChanged = { onEvent(GameContract.Event.OnAnswerTextChanged(it)) },
                onSubmit = { onEvent(GameContract.Event.OnAnswerSubmitted(it)) },
                searchResults = state.searchResults,
                isSearching = state.isSearching,
                onSuggestionSelected = { onEvent(GameContract.Event.OnSuggestionSelected(it)) },
                enabled = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
