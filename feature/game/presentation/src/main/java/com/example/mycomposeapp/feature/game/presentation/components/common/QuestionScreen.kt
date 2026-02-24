package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.GameContract.Event.*
import com.example.mycomposeapp.feature.game.presentation.GameContract.ModeState.*
import com.example.mycomposeapp.feature.game.presentation.components.books.BookByOrderQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.common.AnswerInputView
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
import com.example.mycomposeapp.feature.game.presentation.components.books.OddOneOutGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.books.OddOneOutQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.books.SynopsisGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.books.SynopsisQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.rankle.RankleQuestionView
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun QuestionScreen(
    state: GameContract.State,
    onEvent: (GameContract.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = state.currentQuestion ?: return
    val isRevealed = state.phase == GameContract.GamePhase.AnswerRevealed

    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

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
            is GameContract.ModeState.BookByOrder -> {
            }

            is GameContract.ModeState.BookSynopsis -> {
                SynopsisGameTopBar(
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            is GameContract.ModeState.BookOddOneOut -> {
                OddOneOutGameTopBar(
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }

            else -> Unit
        }

        Spacer(modifier = Modifier.height(spacing.spacing16))

        when (val content = question.content) {
            is QuestionContent.Cover -> {
                val cover = state.coverState
                CoverQuestionView(
                    content = content,
                    revealedCells = if (isRevealed) (0..8).toSet() else cover?.revealedCells ?: emptySet(),
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
                val plotState = state.plotState ?: Plot()
                PlotQuestionView(
                    content = content,
                    plotState = plotState,
                    isRevealed = isRevealed,
                    showInsufficientFunds = plotState.showInsufficientFundsWarning,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Screenshot -> {
                val ssState = state.screenshotState ?: Screenshot()
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
                val descState = state.descriptionState ?: Description()
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
                val achState = state.achievementState ?: Achievement()
                AchievementQuestionView(
                    content = content,
                    achievementState = achState,
                    isRevealed = isRevealed,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Rankle -> {
                val rankleState = state.rankleState ?: Rankle()
                RankleQuestionView(
                    content = content,
                    rankleState = rankleState,
                    isRevealed = isRevealed,
                    onAnswerSubmit = { onEvent(OnAnswerSubmitted(it)) }
                )
            }

            is QuestionContent.BookByOrder -> {
                val orderState = state.bookOrderState ?: BookByOrder()
                BookByOrderQuestionView(
                    state = orderState,
                    onMove = { eventId, slotIndex ->
                        onEvent(OnAnswerSubmitted("MOVE:$eventId:$slotIndex"))
                    },
                    onSubmit = {
                        onEvent(OnAnswerSubmitted("SUBMIT"))
                    }
                )
            }

            is QuestionContent.BookSynopsis -> {
                val bookState = state.bookSynopsisState ?: GameContract.ModeState.BookSynopsis()
                SynopsisQuestionView(
                    content = content,
                    bookSynopsisState = bookState,
                    correctAnswer = question.correctAnswer,
                    isRevealed = isRevealed,
                    onOptionSelected = { onEvent(GameContract.Event.OnAnswerSubmitted(it)) },
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.BookOddOneOut -> {
                val bookState = state.bookOddOneOutState ?: GameContract.ModeState.BookOddOneOut()
                OddOneOutQuestionView(
                    content = content,
                    bookOddOneOutState = bookState,
                    correctAnswer = question.correctAnswer,
                    isRevealed = isRevealed,
                    onBookSelected = { onEvent(GameContract.Event.OnAnswerSubmitted(it)) },
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) },
                    onUseCategoryHint = { onEvent(GameContract.Event.OnUseCategoryHint) }
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.spacing20))

        val isRankleMode = state.rankleState != null
        val isOrderMode = state.bookOrderState != null
        val isBookMode = state.isBookSynopsisMode || state.isBookOddOneOutMode

        if (isRevealed) {
            // Don't show AnswerFeedbackView for Rankle or Book modes (they have their own UI)
            if (!isRankleMode && !isBookMode) {
                AnswerFeedbackView(
                    isCorrect = state.isAnswerCorrect,
                    correctAnswer = question.correctAnswer,
                    userAnswer = state.userAnswer
                )
            }

            // Show coin reward for emoji mode daily
            if (state.isEmojiMode && state.isAnswerCorrect && state.emojiState?.isFromArchive != true) {
                Spacer(modifier = Modifier.height(spacing.spacing8))
                Text(
                    text = stringResource(GameR.string.coins_reward),
                    color = Color(0xFFFFD700),
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(spacing.spacing20))

            NextButton(
                isLastQuestion = when {
                    state.isCoverMode -> state.coverState?.livesRemaining == 0
                    state.isPlotMode -> state.plotState?.guessesRemaining == 0
                    state.isAchievementMode -> state.achievementState?.livesRemaining == 0
                    state.screenshotState != null -> state.screenshotState?.livesRemaining == 0
                    state.descriptionState != null -> state.descriptionState?.livesRemaining == 0
                    isRankleMode -> state.rankleState?.livesRemaining == 0
                        || (!state.isAnswerCorrect && (state.rankleState?.livesRemaining ?: 0) <= 1)
                    isBookMode -> !state.isAnswerCorrect
                    state.isEmojiMode -> true
                    else -> state.currentQuestionIndex >= state.questions.size - 1
                },
                buttonText = if (state.isEmojiMode) stringResource(GameR.string.btn_done) else null,
                onClick = { onEvent(GameContract.Event.OnNextQuestion) }
            )
        } else if (!isRankleMode && !isBookMode) {
            // Rankle and Book modes have their own input — skip shared AnswerInputView
            val achievementMode = state.achievementState
            if (achievementMode != null &&
                achievementMode.livesRemaining < GameConstants.ACHIEVEMENT_INITIAL_LIVES
            ) {
                Text(
                    text = stringResource(
                        GameR.string.lives_remaining_format,
                        achievementMode.livesRemaining,
                        stringResource(
                            if (achievementMode.livesRemaining == 1)
                                GameR.string.life_singular
                            else
                                GameR.string.lives_plural
                        )
                    ),
                    color = Color(0xFFF44336),
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.spacing8)
                )
            }

            val cover = state.coverState
            if (cover != null && cover.livesRemaining < 3) {
                Text(
                    text = stringResource(
                        GameR.string.lives_remaining_format,
                        cover.livesRemaining,
                        stringResource(
                            if (cover.livesRemaining == 1)
                                GameR.string.life_singular
                            else
                                GameR.string.lives_plural
                        )
                    ),
                    color = Color(0xFFF44336),
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.spacing8)
                )
            }
            if(!isOrderMode){
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

        }

        Spacer(modifier = Modifier.height(spacing.spacing24))
    }
}
