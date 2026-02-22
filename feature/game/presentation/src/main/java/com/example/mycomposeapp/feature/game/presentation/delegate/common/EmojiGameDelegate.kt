package com.example.mycomposeapp.feature.game.presentation.delegate.common

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.scoring.UpdateGameStatsUseCase
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.R
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class EmojiGameDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val archiveDate: String?,
    private val getDailyPuzzleUseCase: GetDailyPuzzleUseCase,
    private val dailyPuzzleRepository: DailyPuzzleRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val isFromArchive = archiveDate != null
    private var coinMultiplier: Int = 1

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        val dateToLoad = archiveDate ?: LocalDate.now().toString()
        scope.coroutineScope.launch {
            coinMultiplier = if (!isFromArchive) {
                try {
                    dailyGoalsManagerUseCase.getCoinMultiplier(categoryType, gameModeId)
                } catch (_: Exception) { 1 }
            } else 1

            getDailyPuzzleUseCase(categoryType, dateToLoad).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val puzzle = resource.data
                        if (puzzle.isCompleted && !isFromArchive) {
                            scope.updateState {
                                copy(
                                    phase = GameContract.GamePhase.Loading,
                                    isDailyMode = true,
                                    dailyCompleted = true,
                                    errorMessage = "You've already completed today's puzzle!",
                                    modeState = GameContract.ModeState.Emoji()
                                )
                            }
                            return@collect
                        }

                        val emojiContent = puzzle.question.content as? QuestionContent.Emoji

                        val user = getCurrentUserUseCase().firstOrNull()
                        val userCoins = user?.stats?.coins ?: 0

                        scope.updateState {
                            copy(
                                questions = listOf(puzzle.question),
                                phase = GameContract.GamePhase.Playing,
                                isDailyMode = !isFromArchive,
                                modeState = GameContract.ModeState.Emoji(
                                    guessesRemaining = GameConstants.EMOJI_INITIAL_GUESSES,
                                    hintText = emojiContent?.hintText ?: "",
                                    isHintUsed = false,
                                    coins = userCoins,
                                    isFromArchive = this@EmojiGameDelegate.isFromArchive,
                                    hintLabel = if (categoryType == CategoryType.COMICS.name) "Main Character" else "Lead Actor",
                                    instruction = if (categoryType == CategoryType.COMICS.name) "Guess the comic from emojis" else "Guess the movie from emojis"

                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        scope.updateState { copy(phase = GameContract.GamePhase.Loading, errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {
                        scope.updateState { copy(phase = GameContract.GamePhase.Loading) }
                    }
                }
            }
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val currentQuestion = scope.currentState().currentQuestion ?: return
        val isCorrect = answer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)

        if (isCorrect) {
            answerResults.add(
                AnswerResult(
                    questionId = currentQuestion.id,
                    correctAnswer = currentQuestion.correctAnswer,
                    userAnswer = answer,
                    isCorrect = true,
                    timeSpentSeconds = 0
                )
            )

            val state = scope.currentState()
            val emoji = state.emojiState ?: return
            val coinsEarned = if (!emoji.isFromArchive) GameConstants.EMOJI_DAILY_COINS_REWARD * coinMultiplier else 0
            val newCoins = emoji.coins + coinsEarned

            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerRevealed = true,
                    isAnswerCorrect = true,
                    searchResults = emptyList(),
                    modeState = emoji.copy(coins = newCoins)
                )
            }

            val emojiContent = currentQuestion.content as? QuestionContent.Emoji
            val puzzleDate = emojiContent?.date ?: ""

            scope.coroutineScope.launch {
                try {
                    if (!emoji.isFromArchive) {
                        updateCoinsUseCase(newCoins)
                        dailyPuzzleRepository.markDailyCompleted(puzzleDate)

                        val result = GameResult(
                            totalQuestions = 1,
                            correctAnswers = 1,
                            totalScore = coinsEarned,
                            timeTakenSeconds = 0,
                            bestStreak = 1,
                            answers = answerResults.toList(),
                            coinsEarned = coinsEarned,
                            finalCoinBalance = newCoins
                        )
                        val updatedStats = updateGameStatsUseCase(result, gameModeId, categoryType, true)
                        scope.onGameCompleted(updatedStats)
                        val xpResult = updateDailyGoalProgressUseCase.recordGamePlayed(
                            categoryType = categoryType,
                            gameModeId = gameModeId,
                            wasPerfect = true
                        )
                        if (coinMultiplier > 1) {
                            dailyGoalsManagerUseCase.completeDailyChallenge()
                        }
                        if (xpResult.hasAnyXp) {
                            val msg = buildString {
                                if (xpResult.newlyCompletedGoalIds.isNotEmpty()) {
                                    append("Daily goal complete! +${xpResult.xpAwarded} XP")
                                }
                                if (xpResult.allGoalsCompleted) {
                                    append(" • All goals done! +${xpResult.bonusXpAwarded} XP bonus")
                                }
                            }
                            scope.emitSideEffect(
                                GameContract.SideEffect.ShowSnackbar(UiText.DynamicString(msg))
                            )
                        }
                    }
                } catch (_: Exception) { }
            }
        } else {
            val state = scope.currentState()
            val emoji = state.emojiState ?: return
            val newGuesses = emoji.guessesRemaining - 1

            if (newGuesses <= 0) {
                answerResults.add(
                    AnswerResult(
                        questionId = currentQuestion.id,
                        correctAnswer = currentQuestion.correctAnswer,
                        userAnswer = answer,
                        isCorrect = false,
                        timeSpentSeconds = 0
                    )
                )
                scope.updateState {
                    copy(
                        phase = GameContract.GamePhase.AnswerRevealed,
                        isAnswerRevealed = true,
                        isAnswerCorrect = false,
                        searchResults = emptyList(),
                        modeState = emoji.copy(guessesRemaining = 0)
                    )
                }

                val emojiContent = currentQuestion.content as? QuestionContent.Emoji
                val puzzleDate = emojiContent?.date ?: ""

                scope.coroutineScope.launch {
                    try {
                        if (!emoji.isFromArchive) {
                            dailyPuzzleRepository.markDailyCompleted(puzzleDate)
                        }
                    } catch (_: Exception) { }
                }
            } else {
                scope.updateState {
                    copy(
                        userAnswer = "",
                        searchResults = emptyList(),
                        modeState = emoji.copy(guessesRemaining = newGuesses)
                    )
                }
                scope.emitSideEffect(
                    GameContract.SideEffect.ShowSnackbar(
                        UiText.StringResource(R.string.wrong_guesses_format, listOf(newGuesses, if (newGuesses == 1) "guess" else "guesses"))
                    )
                )
            }
        }
    }

    override fun onNextQuestion() {
        scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    }

    override fun onRetryGame() {
        scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    }

    override fun onExitGame() {
        scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    }

    override fun onUseHint() {
        val state = scope.currentState()
        val emoji = state.emojiState ?: return
        if (emoji.isHintUsed || emoji.coins < GameConstants.EMOJI_HINT_COST) return

        val newCoins = emoji.coins - GameConstants.EMOJI_HINT_COST
        scope.updateState {
            copy(modeState = emoji.copy(isHintUsed = true, coins = newCoins))
        }
        scope.coroutineScope.launch {
            try { updateCoinsUseCase(newCoins) } catch (_: Exception) { }
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()
}
