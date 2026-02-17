package com.example.mycomposeapp.feature.game.presentation.delegate.games

import com.example.mycomposeapp.core.domain.LevelingRules
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchAchievementBatchUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GameAchievementDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val fetchAchievementBatchUseCase: FetchAchievementBatchUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenIds = mutableSetOf<String>()
    private val questionQueue = mutableListOf<Question>()
    private var prefetchJob: Job? = null
    private var correctAnswersTotal = 0

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.Achievement()
                )
            }

            val user = getCurrentUserUseCase().firstOrNull()
            val initialCoins = user?.stats?.coins ?: 0

            scope.updateState {
                copy(
                    currentStreak = 0,
                    correctAnswersCount = 0,
                    modeState = GameContract.ModeState.Achievement(coins = initialCoins)
                )
            }

            fetchBatchAndStart()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val state = scope.currentState()
        val currentQuestion = state.currentQuestion ?: return
        val achievement = state.achievementState ?: return

        val isCorrect = answer.trim()
            .equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)

        if (isCorrect) {
            correctAnswersTotal++
            val newStreak = state.currentStreak + 1
            val newBestStreak = maxOf(achievement.bestSessionStreak, newStreak)
            val scoreGain = GameConstants.ACHIEVEMENT_BASE_POINTS +
                    GameConstants.ACHIEVEMENT_STREAK_BONUS * newStreak
            val newScore = achievement.currentScore + scoreGain
            val newCoins = achievement.coins + GameConstants.ACHIEVEMENT_COINS_PER_CORRECT
            val newCorrect = state.correctAnswersCount + 1

            answerResults.add(
                AnswerResult(
                    questionId = currentQuestion.id,
                    correctAnswer = currentQuestion.correctAnswer,
                    userAnswer = answer,
                    isCorrect = true,
                    timeSpentSeconds = 0
                )
            )

            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerRevealed = true,
                    isAnswerCorrect = true,
                    currentStreak = newStreak,
                    correctAnswersCount = newCorrect,
                    userAnswer = answer,
                    searchResults = emptyList(),
                    modeState = achievement.copy(
                        bestSessionStreak = newBestStreak,
                        currentScore = newScore,
                        coins = newCoins,
                        visibleAchievementCount = GameConstants.ACHIEVEMENT_INITIAL_VISIBLE,
                        revealCost = GameConstants.ACHIEVEMENT_HINT_COST,
                        showInsufficientFundsWarning = false
                    )
                )
            }

            scope.coroutineScope.launch {
                try {
                    val user = getCurrentUserUseCase().firstOrNull() ?: return@launch
                    val stats = user.stats

                    val xpGain = 50
                    val newTotalXp = stats.totalXp + xpGain
                    val newLevel = LevelingRules.calculateLevel(newTotalXp)

                    val updatedStats = stats.copy(
                        coins = stats.coins + GameConstants.COVER_COINS_PER_CORRECT,
                        points = stats.points + scoreGain,
                        totalXp = newTotalXp,
                        level = newLevel,
                        correctAnswers = stats.correctAnswers + 1,
                        bestStreak = maxOf(stats.bestStreak, newBestStreak)
                    )

                    updateUserStatsUseCase(updatedStats)
                } catch (_: Exception) { }
            }
        } else {
            val newLives = (achievement.livesRemaining - 1).coerceAtLeast(0)
            val newVisible = (achievement.visibleAchievementCount + GameConstants.ACHIEVEMENT_REVEAL_STEP)
                .coerceAtMost(GameConstants.ACHIEVEMENT_MAX_VISIBLE)

            if (newLives > 0) {
                scope.updateState {
                    copy(
                        phase = GameContract.GamePhase.Playing,
                        isAnswerRevealed = false,
                        isAnswerCorrect = false,
                        currentStreak = 0,
                        userAnswer = "",
                        searchResults = emptyList(),
                        modeState = achievement.copy(
                            livesRemaining = newLives,
                            visibleAchievementCount = newVisible
                        )
                    )
                }

                scope.emitSideEffect(
                    GameContract.SideEffect.ShowSnackbar("Wrong! $newLives lives remaining")
                )
                return
            }

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
                    currentStreak = 0,
                    userAnswer = answer,
                    searchResults = emptyList(),
                    modeState = achievement.copy(livesRemaining = 0)
                )
            }
        }
    }

    override fun onNextQuestion() {
        val achievement = scope.currentState().achievementState ?: return
        if (achievement.livesRemaining <= 0) {
            finishGame()
        } else {
            presentNextQuestion()
        }
    }

    override fun onUseHint() {
        val achievement = scope.currentState().achievementState ?: return
        if (achievement.visibleAchievementCount >= GameConstants.ACHIEVEMENT_MAX_VISIBLE) return

        if (achievement.coins < achievement.revealCost) {
            scope.updateState {
                copy(modeState = achievement.copy(showInsufficientFundsWarning = true))
            }
            return
        }

        val newCoins = achievement.coins - achievement.revealCost
        scope.updateState {
            copy(
                modeState = achievement.copy(
                    visibleAchievementCount = achievement.nextRevealCount,
                    coins = newCoins,
                    revealCost = achievement.revealCost * 2,
                    showInsufficientFundsWarning = false
                )
            )
        }

        scope.coroutineScope.launch {
            try { updateCoinsUseCase(newCoins) } catch (_: Exception) {}
        }
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenIds.clear()
        questionQueue.clear()
        correctAnswersTotal = 0
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val achievement = scope.currentState().achievementState
            try {
                if (achievement != null) updateCoinsUseCase(achievement.coins)
            } catch (_: Exception) {}
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
    }

    private fun finishGame() {
        val state = scope.currentState()
        val achievement = state.achievementState ?: return

        val result = GameResult(
            totalQuestions = answerResults.size,
            correctAnswers = state.correctAnswersCount,
            totalScore = achievement.currentScore,
            timeTakenSeconds = state.totalTimeSpentSeconds,
            bestStreak = achievement.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = false,
            coinsEarned = achievement.coins,
            finalCoinBalance = achievement.coins
        )

        scope.updateState {
            copy(
                phase = GameContract.GamePhase.Results,
                gameResult = result
            )
        }

        scope.coroutineScope.launch {
            try {
                updateGameStatsUseCase(result, gameModeId, categoryType, false)
            } catch (_: Exception) {}
        }
    }

    private val currentMaxPages: Int
        get() = GameConstants.ACHIEVEMENT_INITIAL_PAGE_LIMIT +
                (correctAnswersTotal / 10) * GameConstants.ACHIEVEMENT_PAGE_EXPAND_STEP

    private suspend fun fetchBatchAndStart() {
        fetchAchievementBatchUseCase(
            batchSize = GameConstants.ACHIEVEMENT_BATCH_SIZE,
            seenIds = seenIds,
            maxPages = currentMaxPages
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val fresh = resource.data.filter { it.id !in seenIds }
                    if (fresh.isEmpty()) {
                        scope.updateState {
                            val ach = achievementState ?: GameContract.ModeState.Achievement()
                            copy(
                                phase = GameContract.GamePhase.Loading,
                                errorMessage = "No more achievement questions available.",
                                modeState = ach.copy(isFetchingMore = false)
                            )
                        }
                        return@collect
                    }

                    fresh.forEach { q -> seenIds.add(q.id) }
                    questionQueue.addAll(fresh)
                    presentNextQuestion()
                }

                is Resource.Error -> {
                    scope.updateState {
                        copy(
                            phase = GameContract.GamePhase.Loading,
                            errorMessage = resource.message
                        )
                    }
                }

                is Resource.Loading -> {
                    scope.updateState { copy(phase = GameContract.GamePhase.Loading) }
                }
            }
        }
    }

    private fun presentNextQuestion() {
        if (questionQueue.isEmpty()) {
            scope.updateState {
                val ach = achievementState ?: GameContract.ModeState.Achievement()
                copy(modeState = ach.copy(isFetchingMore = true))
            }
            scope.coroutineScope.launch { fetchBatchAndStart() }
            return
        }

        val nextQuestion = questionQueue.removeAt(0)

        scope.updateState {
            val ach = achievementState ?: GameContract.ModeState.Achievement()
            copy(
                questions = listOf(nextQuestion),
                currentQuestionIndex = 0,
                phase = GameContract.GamePhase.Playing,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                errorMessage = null,
                modeState = ach.copy(
                    isFetchingMore = false,
                    visibleAchievementCount = GameConstants.ACHIEVEMENT_INITIAL_VISIBLE,
                    revealCost = GameConstants.ACHIEVEMENT_HINT_COST,
                    showInsufficientFundsWarning = false
                )
            )
        }

        prefetchIfNeeded()
    }

    private fun prefetchIfNeeded() {
        if (questionQueue.size <= 2 && prefetchJob?.isActive != true) {
            prefetchJob = scope.coroutineScope.launch {
                fetchAchievementBatchUseCase(
                    batchSize = GameConstants.ACHIEVEMENT_BATCH_SIZE,
                    seenIds = seenIds,
                    maxPages = currentMaxPages
                ).collect { resource ->
                    if (resource is Resource.Success) {
                        val fresh = resource.data.filter { it.id !in seenIds }
                        fresh.forEach { q -> seenIds.add(q.id) }
                        questionQueue.addAll(fresh)
                    }
                }
            }
        }
    }
}
