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
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.components.cover.CoverGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.cover.CoverQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.emoji.EmojiGameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.emoji.EmojiQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.games.description.DescriptionQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.games.screenshot.ScreenshotQuestionView
import com.example.mycomposeapp.feature.game.presentation.components.plot.GameTopBar
import com.example.mycomposeapp.feature.game.presentation.components.plot.PlotQuestionView

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
            is GameContract.ModeState.Screenshot -> {
                BasicGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }
            is GameContract.ModeState.Description -> {
                BasicGameTopBar(
                    livesRemaining = mode.livesRemaining,
                    coins = mode.coins,
                    score = mode.currentScore,
                    streak = state.currentStreak
                )
            }
            else -> {
                val plot = state.plotState
                GameTopBar(
                    questionIndex = state.currentQuestionIndex,
                    totalQuestions = state.questions.size,
                    timeRemaining = plot?.timeRemainingSeconds ?: 0,
                    streak = state.currentStreak,
                    progress = state.progressFraction
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            is QuestionContent.Plot -> PlotQuestionView(content = content)
            is QuestionContent.Screenshot -> {
                val screenshot = state.screenshotState!!
                ScreenshotQuestionView(
                    content = state.currentQuestion!!.content as QuestionContent.Screenshot,
                    coins = screenshot.coins,
                    hintCost = screenshot.hintCost,
                    hintStep = screenshot.hintStep,
                    studioHint = screenshot.studioHint,
                    genreHint = screenshot.genreHint,
                    yearHint = screenshot.yearHint,
                    onUseHint = { onEvent(GameContract.Event.OnUseHint) }
                )
            }

            is QuestionContent.Description -> {
                val d = state.descriptionState
                if (d != null) {
                    DescriptionQuestionView(
                        content = content,
                        guessesRemaining = d.livesRemaining,
                        coins = d.coins,
                        hintStep = d.hintStep,
                        hintCost = d.hintCost,
                        onUseHint = { onEvent(GameContract.Event.OnUseHint) },
                        studioHint = d.studioHint,
                        genreHint = d.genreHint,
                        yearHint = d.yearHint
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isRevealed) {
            AnswerFeedbackView(
                isCorrect = state.isAnswerCorrect,
                correctAnswer = question.correctAnswer,
                userAnswer = state.userAnswer
            )

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
                    state.isCoverMode -> false
                    state.isEmojiMode -> true
                    else -> state.currentQuestionIndex >= state.questions.size - 1
                },
                buttonText = if (state.isEmojiMode) stringResource(GameR.string.btn_done) else null,
                onClick = { onEvent(GameContract.Event.OnNextQuestion) }
            )
        } else {
            // Show lives warning for cover mode when lives lost
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
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            AnswerInputView(
                userAnswer = state.userAnswer,
                onAnswerChanged = { onEvent(GameContract.Event.OnAnswerTextChanged(it)) },
                onSubmit = { onEvent(GameContract.Event.OnAnswerSubmitted(it)) },
                searchResults = state.searchResults,
                isSearching = state.isSearching,
                onSuggestionSelected = { onEvent(GameContract.Event.OnSuggestionSelected(it)) },
                enabled = !isRevealed
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
