package com.example.mycomposeapp.feature.game.presentation.delegate.movies

import com.example.mycomposeapp.core.domain.LevelingRules
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.usecase.FetchCoverBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MovieCoverDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val fetchCoverBatchUseCase: FetchCoverBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenItemIds = mutableSetOf<String>()
    private val questionQueue = mutableListOf<com.example.mycomposeapp.feature.game.domain.model.Question>()
    private var prefetchJob: Job? = null

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.Cover()
                )
            }

            val user = getCurrentUserUseCase().firstOrNull()
            val initialCoins = user?.stats?.coins ?: 0

            scope.updateState {
                copy(
                    currentStreak = 0,
                    correctAnswersCount = 0,
                    modeState = GameContract.ModeState.Cover(coins = initialCoins)
                )
            }

            fetchCoverBatchAndStart()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val currentQuestion = scope.currentState().currentQuestion ?: return
        val isCorrect = answer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)

        if (isCorrect) {
            val state = scope.currentState()
            val cover = state.coverState ?: return
            val newStreak = state.currentStreak + 1
            val newBestStreak = maxOf(cover.bestSessionStreak, newStreak)
            val scoreGain = GameConstants.COVER_BASE_POINTS + GameConstants.COVER_STREAK_BONUS * newStreak
            val newScore = cover.currentScore + scoreGain
            val newCoins = cover.coins + GameConstants.COVER_COINS_PER_CORRECT
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
                    searchResults = emptyList(),
                    modeState = cover.copy(
                        bestSessionStreak = newBestStreak,
                        currentScore = newScore,
                        coins = newCoins,
                        revealedCells = (0 until GameConstants.COVER_GRID_CELLS).toSet()
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
            val state = scope.currentState()
            val cover = state.coverState ?: return
            val newLives = cover.livesRemaining - 1

            val hiddenCells = (0 until GameConstants.COVER_GRID_CELLS).filter { it !in cover.revealedCells }
            val cellToReveal = hiddenCells.randomOrNull()
            val newRevealedCells = if (cellToReveal != null) {
                cover.revealedCells + cellToReveal
            } else {
                cover.revealedCells
            }

            if (newLives <= 0) {
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
                        searchResults = emptyList(),
                        modeState = cover.copy(
                            livesRemaining = 0,
                            revealedCells = (0 until GameConstants.COVER_GRID_CELLS).toSet()
                        )
                    )
                }
            } else {
                scope.updateState {
                    copy(
                        currentStreak = 0,
                        userAnswer = "",
                        searchResults = emptyList(),
                        modeState = cover.copy(
                            livesRemaining = newLives,
                            revealedCells = newRevealedCells
                        )
                    )
                }
                scope.emitSideEffect(GameContract.SideEffect.ShowSnackbar("Wrong! $newLives lives remaining"))
            }
        }
    }

    override fun onNextQuestion() {
        val cover = scope.currentState().coverState ?: return
        if (cover.livesRemaining <= 0) {
            finishCoverGame()
        } else {
            presentNextCoverQuestion()
        }
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenItemIds.clear()
        questionQueue.clear()
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val cover = scope.currentState().coverState
            try { if (cover != null) updateCoinsUseCase(cover.coins) } catch (_: Exception) { }
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun onRevealMore() {
        val state = scope.currentState()
        val cover = state.coverState ?: return
        if (!cover.canAffordReveal) return

        val hiddenCells = (0 until GameConstants.COVER_GRID_CELLS).filter { it !in cover.revealedCells }
        val cellToReveal = hiddenCells.randomOrNull() ?: return

        scope.updateState {
            copy(
                modeState = cover.copy(
                    coins = cover.coins - cover.revealCost,
                    revealCost = cover.revealCost * 2,
                    revealedCells = cover.revealedCells + cellToReveal
                )
            )
        }
        scope.coroutineScope.launch {
            val updatedCover = scope.currentState().coverState
            try { if (updatedCover != null) updateCoinsUseCase(updatedCover.coins) } catch (_: Exception) { }
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
    }

    private suspend fun fetchCoverBatchAndStart() {
        val maxPage = calculateMaxPage(scope.currentState().correctAnswersCount)
        fetchCoverBatchUseCase(categoryType, maxPage, 5, seenItemIds).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val questions = resource.data
                    questions.forEach { q ->
                        seenItemIds.add(q.id.removePrefix("cover_"))
                    }
                    questionQueue.addAll(questions)
                    presentNextCoverQuestion()
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

    private fun presentNextCoverQuestion() {
        if (questionQueue.isEmpty()) {
            scope.updateState {
                val cover = coverState ?: return@updateState this
                copy(modeState = cover.copy(isFetchingMore = true))
            }
            scope.coroutineScope.launch { fetchCoverBatchAndStart() }
            return
        }

        val nextQuestion = questionQueue.removeAt(0)
        scope.updateState {
            val cover = coverState ?: GameContract.ModeState.Cover()
            copy(
                questions = listOf(nextQuestion),
                currentQuestionIndex = 0,
                phase = GameContract.GamePhase.Playing,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                searchResults = emptyList(),
                modeState = cover.copy(
                    livesRemaining = GameConstants.COVER_INITIAL_LIVES,
                    revealedCells = setOf((0 until GameConstants.COVER_GRID_CELLS).random()),
                    isFetchingMore = false
                )
            )
        }
        prefetchIfNeeded()
    }

    private fun calculateMaxPage(correctCount: Int): Int {
        return ((correctCount / 10) + 1) * 10
    }

    private fun prefetchIfNeeded() {
        if (questionQueue.size <= 2 && prefetchJob?.isActive != true) {
            prefetchJob = scope.coroutineScope.launch {
                val maxPage = calculateMaxPage(scope.currentState().correctAnswersCount)
                fetchCoverBatchUseCase(categoryType, maxPage, 5, seenItemIds).collect { resource ->
                    if (resource is Resource.Success) {
                        resource.data.forEach { q ->
                            seenItemIds.add(q.id.removePrefix("cover_"))
                        }
                        questionQueue.addAll(resource.data)
                    }
                }
            }
        }
    }

    private fun finishCoverGame() {
        val state = scope.currentState()
        val cover = state.coverState ?: return
        val currentHighScore = 0
        val isNewHigh = cover.currentScore > currentHighScore

        val result = GameResult(
            totalQuestions = state.correctAnswersCount + answerResults.count { !it.isCorrect },
            correctAnswers = state.correctAnswersCount,
            totalScore = cover.currentScore,
            timeTakenSeconds = 0,
            bestStreak = cover.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = isNewHigh,
            coinsEarned = cover.coins,
            finalCoinBalance = cover.coins
        )

        scope.updateState {
            copy(
                phase = GameContract.GamePhase.Results,
                gameResult = result
            )
        }

        scope.coroutineScope.launch {
            try {
                val user = getCurrentUserUseCase().firstOrNull()
                val statsKey = GameModeIds.statsKey(categoryType, gameModeId)
                val actualHigh = user?.stats?.highScore?.get(statsKey) ?: 0
                if (cover.currentScore > actualHigh) {
                    scope.updateState {
                        copy(gameResult = gameResult?.copy(isNewHighScore = true))
                    }
                }
                updateGameStatsUseCase(result, gameModeId, categoryType, false)
            } catch (_: Exception) { }
        }
    }
}
