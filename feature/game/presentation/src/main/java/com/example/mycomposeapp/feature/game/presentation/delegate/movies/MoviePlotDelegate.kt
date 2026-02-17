package com.example.mycomposeapp.feature.game.presentation.delegate.movies

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.usecase.FetchPlotBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MoviePlotDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val fetchPlotBatchUseCase: FetchPlotBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenItemIds = mutableSetOf<String>()
    private val questionQueue = mutableListOf<Question>()
    private var prefetchJob: Job? = null
    private var coinMultiplier: Int = 1

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.Plot()
                )
            }

            coinMultiplier = try {
                dailyGoalsManagerUseCase.getCoinMultiplier(categoryType, gameModeId)
            } catch (_: Exception) { 1 }

            val user = getCurrentUserUseCase().firstOrNull()
            val initialCoins = user?.stats?.coins ?: 0

            scope.updateState {
                copy(
                    currentStreak = 0,
                    correctAnswersCount = 0,
                    modeState = GameContract.ModeState.Plot(coins = initialCoins)
                )
            }

            fetchPlotBatchAndStart()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val currentQuestion = scope.currentState().currentQuestion ?: return
        val isCorrect = answer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
        val state = scope.currentState()
        val plot = state.plotState ?: return

        if (isCorrect) {
            val newStreak = state.currentStreak + 1
            val newBestStreak = maxOf(plot.bestSessionStreak, newStreak)
            val scoreGain = GameConstants.PLOT_BASE_POINTS + GameConstants.PLOT_STREAK_BONUS * newStreak
            val newScore = plot.currentScore + scoreGain
            val newCoins = plot.coins + GameConstants.PLOT_COINS_PER_CORRECT * coinMultiplier
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
                    modeState = plot.copy(
                        bestSessionStreak = newBestStreak,
                        currentScore = newScore,
                        coins = newCoins
                    )
                )
            }
            scope.coroutineScope.launch { try { updateCoinsUseCase(newCoins) } catch (_: Exception) { } }
        } else {
            val newGuesses = plot.guessesRemaining - 1

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

                val content = currentQuestion.content as? QuestionContent.Plot
                val allIndices = (0 until (content?.hints?.size ?: 0)).toSet()

                scope.updateState {
                    copy(
                        phase = GameContract.GamePhase.AnswerRevealed,
                        isAnswerRevealed = true,
                        isAnswerCorrect = false,
                        currentStreak = 0,
                        searchResults = emptyList(),
                        modeState = plot.copy(
                            guessesRemaining = 0,
                            revealedHintIndices = allIndices
                        )
                    )
                }
            } else {
                // Auto-reveal one random unrevealed hint
                val content = currentQuestion.content as? QuestionContent.Plot
                val totalHints = content?.hints?.size ?: 0
                val unrevealed = (0 until totalHints).filter { it !in plot.revealedHintIndices }
                val newRevealed = if (unrevealed.isNotEmpty()) {
                    plot.revealedHintIndices + unrevealed.random()
                } else {
                    plot.revealedHintIndices
                }

                scope.updateState {
                    copy(
                        userAnswer = "",
                        searchResults = emptyList(),
                        modeState = plot.copy(
                            guessesRemaining = newGuesses,
                            revealedHintIndices = newRevealed
                        )
                    )
                }
                scope.emitSideEffect(
                    GameContract.SideEffect.ShowSnackbar(
                        "Wrong! $newGuesses ${if (newGuesses == 1) "guess" else "guesses"} remaining"
                    )
                )
            }
        }
    }

    override fun onNextQuestion() {
        val plot = scope.currentState().plotState ?: return
        if (plot.guessesRemaining <= 0) {
            finishPlotGame()
        } else {
            presentNextPlotQuestion()
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
            val plot = scope.currentState().plotState
            try { if (plot != null) updateCoinsUseCase(plot.coins) } catch (_: Exception) { }
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun onUseHint() {
        val state = scope.currentState()
        val plot = state.plotState ?: return
        if (plot.allHintsRevealed) return

        if (plot.coins < GameConstants.PLOT_HINT_COST) {
            scope.updateState {
                copy(modeState = plot.copy(showInsufficientFundsWarning = true))
            }
            return
        }

        val currentQuestion = state.currentQuestion ?: return
        val content = currentQuestion.content as? QuestionContent.Plot ?: return
        val totalHints = content.hints.size
        val unrevealed = (0 until totalHints).filter { it !in plot.revealedHintIndices }
        if (unrevealed.isEmpty()) return

        val newCoins = plot.coins - GameConstants.PLOT_HINT_COST
        val newRevealed = plot.revealedHintIndices + unrevealed.random()

        scope.updateState {
            copy(
                modeState = plot.copy(
                    coins = newCoins,
                    revealedHintIndices = newRevealed,
                    showInsufficientFundsWarning = false
                )
            )
        }

        scope.coroutineScope.launch {
            try { updateCoinsUseCase(newCoins) } catch (_: Exception) { }
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
    }

    private suspend fun fetchPlotBatchAndStart() {
        val maxPage = calculateMaxPage(scope.currentState().correctAnswersCount)
        fetchPlotBatchUseCase(categoryType, maxPage, 5, seenItemIds).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val questions = resource.data
                    questions.forEach { q ->
                        seenItemIds.add(q.id.removePrefix("plot_"))
                    }
                    questionQueue.addAll(questions)
                    presentNextPlotQuestion()
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

    private fun presentNextPlotQuestion() {
        if (questionQueue.isEmpty()) {
            scope.updateState {
                val plot = plotState ?: return@updateState this
                copy(modeState = plot.copy(isFetchingMore = true))
            }
            scope.coroutineScope.launch { fetchPlotBatchAndStart() }
            return
        }

        val nextQuestion = questionQueue.removeAt(0)
        scope.updateState {
            val plot = plotState ?: GameContract.ModeState.Plot()
            copy(
                questions = listOf(nextQuestion),
                currentQuestionIndex = 0,
                phase = GameContract.GamePhase.Playing,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                searchResults = emptyList(),
                modeState = plot.copy(
                    guessesRemaining = GameConstants.PLOT_INITIAL_GUESSES,
                    revealedHintIndices = emptySet(),
                    isFetchingMore = false,
                    showInsufficientFundsWarning = false
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
                fetchPlotBatchUseCase(categoryType, maxPage, 5, seenItemIds).collect { resource ->
                    if (resource is Resource.Success) {
                        resource.data.forEach { q ->
                            seenItemIds.add(q.id.removePrefix("plot_"))
                        }
                        questionQueue.addAll(resource.data)
                    }
                }
            }
        }
    }

    private fun finishPlotGame() {
        val state = scope.currentState()
        val plot = state.plotState ?: return
        val currentHighScore = 0
        val isNewHigh = plot.currentScore > currentHighScore

        val result = GameResult(
            totalQuestions = state.correctAnswersCount + answerResults.count { !it.isCorrect },
            correctAnswers = state.correctAnswersCount,
            totalScore = plot.currentScore,
            timeTakenSeconds = 0,
            bestStreak = plot.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = isNewHigh,
            coinsEarned = plot.coins,
            finalCoinBalance = plot.coins
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
                if (plot.currentScore > actualHigh) {
                    scope.updateState {
                        copy(gameResult = gameResult?.copy(isNewHighScore = true))
                    }
                }
                updateGameStatsUseCase(result, gameModeId, categoryType, false)
                updateDailyGoalProgressUseCase.recordGamePlayed(
                    categoryType = categoryType,
                    gameModeId = gameModeId,
                    wasPerfect = false
                )
                if (coinMultiplier > 1) {
                    dailyGoalsManagerUseCase.completeDailyChallenge()
                }
            } catch (_: Exception) { }
        }
    }
}
