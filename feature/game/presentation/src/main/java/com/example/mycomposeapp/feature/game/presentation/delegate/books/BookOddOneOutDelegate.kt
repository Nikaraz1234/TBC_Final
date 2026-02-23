package com.example.mycomposeapp.feature.game.presentation.delegate.books

import com.example.mycomposeapp.core.domain.rules.LevelingRules
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.usecase.books.FetchBookOddOneOutBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.scoring.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BookOddOneOutDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val fetchBookOddOneOutBatchUseCase: FetchBookOddOneOutBatchUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenItemIds = mutableSetOf<String>()
    private val questionQueue = mutableListOf<Question>()
    private var prefetchJob: Job? = null
    private var coinMultiplier: Int = 1
    private var currentStartIndexMax: Int = GameConstants.BOOK_ODD_ONE_OUT_START_INDEX_MAX_INIT

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.BookOddOneOut()
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
                    modeState = GameContract.ModeState.BookOddOneOut(coins = initialCoins)
                )
            }

            fetchBatchAndStart()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val currentQuestion = scope.currentState().currentQuestion ?: return
        val isCorrect = answer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
        val state = scope.currentState()
        val bookState = state.bookOddOneOutState ?: return

        val bookId = (currentQuestion.content as? QuestionContent.BookOddOneOut)
            ?.books?.find { it.title.equals(answer.trim(), ignoreCase = true) }?.id ?: ""

        if (isCorrect) {
            val newStreak = state.currentStreak + 1
            val newBestStreak = maxOf(bookState.bestSessionStreak, newStreak)
            val scoreGain = GameConstants.BOOK_ODD_ONE_OUT_BASE_POINTS + GameConstants.BOOK_ODD_ONE_OUT_STREAK_BONUS * newStreak
            val newScore = bookState.currentScore + scoreGain
            val newCoins = bookState.coins + GameConstants.BOOK_ODD_ONE_OUT_COINS_PER_CORRECT * coinMultiplier
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

            if (newCorrect % GameConstants.BOOK_ODD_ONE_OUT_START_INDEX_EXPAND_STEP == 0) {
                currentStartIndexMax += GameConstants.BOOK_ODD_ONE_OUT_START_INDEX_EXPAND_AMOUNT
            }

            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerRevealed = true,
                    isAnswerCorrect = true,
                    currentStreak = newStreak,
                    correctAnswersCount = newCorrect,
                    searchResults = emptyList(),
                    modeState = bookState.copy(
                        bestSessionStreak = newBestStreak,
                        currentScore = newScore,
                        coins = newCoins,
                        selectedBookId = bookId
                    )
                )
            }

            scope.coroutineScope.launch {
                try {
                    val user = getCurrentUserUseCase().firstOrNull() ?: return@launch
                    val stats = user.stats
                    val newTotalXp = stats.totalXp + GameConstants.XP_GAIN
                    val newLevel = LevelingRules.calculateLevel(newTotalXp)
                    updateUserStatsUseCase(
                        stats.copy(
                            coins = stats.coins + GameConstants.BOOK_ODD_ONE_OUT_COINS_PER_CORRECT,
                            points = stats.points + scoreGain,
                            totalXp = newTotalXp,
                            level = newLevel,
                            correctAnswers = stats.correctAnswers + 1,
                            bestStreak = maxOf(stats.bestStreak, newBestStreak)
                        )
                    )
                } catch (_: Exception) { }
            }
        } else {
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
                    modeState = bookState.copy(selectedBookId = bookId)
                )
            }
        }
    }

    override fun onNextQuestion() {
        val state = scope.currentState()
        if (!state.isAnswerCorrect) {
            finishGame()
        } else {
            presentNextQuestion()
        }
    }

    override fun onUseHint() {
        val state = scope.currentState()
        val bookState = state.bookOddOneOutState ?: return
        if (bookState.isHintUsed) return

        if (bookState.coins < GameConstants.BOOK_ODD_ONE_OUT_HINT_COST) {
            scope.updateState {
                copy(modeState = bookState.copy(showInsufficientFundsWarning = true))
            }
            return
        }

        val currentQuestion = state.currentQuestion ?: return
        val content = currentQuestion.content as? QuestionContent.BookOddOneOut ?: return
        val sharedBooks = content.books.filter { it.title != currentQuestion.correctAnswer }
        val toRemove = sharedBooks.shuffled().take(2).map { it.id }.toSet()

        val newCoins = bookState.coins - GameConstants.BOOK_ODD_ONE_OUT_HINT_COST
        scope.updateState {
            copy(
                modeState = bookState.copy(
                    coins = newCoins,
                    isHintUsed = true,
                    removedBookIds = toRemove,
                    showInsufficientFundsWarning = false
                )
            )
        }
        scope.coroutineScope.launch {
            try { updateCoinsUseCase(newCoins) } catch (_: Exception) { }
        }
    }

    override fun onUseCategoryHint() {
        val state = scope.currentState()
        val bookState = state.bookOddOneOutState ?: return
        if (bookState.isCategoryHintUsed) return
        if (bookState.coins < GameConstants.BOOK_ODD_ONE_OUT_CATEGORY_HINT_COST) return
        val newCoins = bookState.coins - GameConstants.BOOK_ODD_ONE_OUT_CATEGORY_HINT_COST
        scope.updateState {
            copy(modeState = bookState.copy(coins = newCoins, isCategoryHintUsed = true))
        }
        scope.coroutineScope.launch {
            try { updateCoinsUseCase(newCoins) } catch (_: Exception) { }
        }
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenItemIds.clear()
        questionQueue.clear()
        currentStartIndexMax = GameConstants.BOOK_ODD_ONE_OUT_START_INDEX_MAX_INIT
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val bookState = scope.currentState().bookOddOneOutState
            try { if (bookState != null) updateCoinsUseCase(bookState.coins) } catch (_: Exception) { }
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
    }

    private suspend fun fetchBatchAndStart() {
        fetchBookOddOneOutBatchUseCase(
            categoryType,
            GameConstants.BOOK_ODD_ONE_OUT_BATCH_SIZE,
            currentStartIndexMax,
            seenItemIds
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data.forEach { q -> seenItemIds.add(q.id) }
                    questionQueue.addAll(resource.data)
                    presentNextQuestion()
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

    private fun presentNextQuestion() {
        if (questionQueue.isEmpty()) {
            scope.updateState {
                val bs = bookOddOneOutState ?: return@updateState this
                copy(modeState = bs.copy(isFetchingMore = true))
            }
            scope.coroutineScope.launch { fetchBatchAndStart() }
            return
        }

        val next = questionQueue.removeAt(0)
        scope.updateState {
            val bs = bookOddOneOutState ?: GameContract.ModeState.BookOddOneOut()
            copy(
                questions = listOf(next),
                currentQuestionIndex = 0,
                phase = GameContract.GamePhase.Playing,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                searchResults = emptyList(),
                modeState = bs.copy(
                    isHintUsed = false,
                    isCategoryHintUsed = false,
                    removedBookIds = emptySet(),
                    selectedBookId = null,
                    isFetchingMore = false,
                    showInsufficientFundsWarning = false
                )
            )
        }
        prefetchIfNeeded()
    }

    private fun prefetchIfNeeded() {
        if (questionQueue.size <= GameConstants.BOOK_ODD_ONE_OUT_PREFETCH_THRESHOLD && prefetchJob?.isActive != true) {
            prefetchJob = scope.coroutineScope.launch {
                fetchBookOddOneOutBatchUseCase(
                    categoryType,
                    GameConstants.BOOK_ODD_ONE_OUT_BATCH_SIZE,
                    currentStartIndexMax,
                    seenItemIds
                ).collect { resource ->
                    if (resource is Resource.Success) {
                        resource.data.forEach { q -> seenItemIds.add(q.id) }
                        questionQueue.addAll(resource.data)
                    }
                }
            }
        }
    }

    private fun finishGame() {
        val state = scope.currentState()
        val bookState = state.bookOddOneOutState ?: return

        val result = GameResult(
            totalQuestions = state.correctAnswersCount + answerResults.count { !it.isCorrect },
            correctAnswers = state.correctAnswersCount,
            totalScore = bookState.currentScore,
            timeTakenSeconds = 0,
            bestStreak = bookState.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = false,
            coinsEarned = bookState.coins,
            finalCoinBalance = bookState.coins
        )

        scope.updateState { copy(phase = GameContract.GamePhase.Results, gameResult = result) }

        scope.coroutineScope.launch {
            try {
                val user = getCurrentUserUseCase().firstOrNull()
                val statsKey = GameModeIds.statsKey(categoryType, gameModeId)
                val actualHigh = user?.stats?.highScore?.get(statsKey) ?: 0
                if (bookState.currentScore > actualHigh) {
                    scope.updateState { copy(gameResult = gameResult?.copy(isNewHighScore = true)) }
                }
                val updatedStats = updateGameStatsUseCase(result, gameModeId, categoryType, false)
                scope.onGameCompleted(updatedStats)
                val xpResult = updateDailyGoalProgressUseCase.recordGamePlayed(
                    categoryType = categoryType,
                    gameModeId = gameModeId,
                    wasPerfect = false
                )
                if (coinMultiplier > 1) {
                    dailyGoalsManagerUseCase.completeDailyChallenge()
                }
                if (xpResult.hasAnyXp) {
                    val msg = buildString {
                        if (xpResult.newlyCompletedGoalIds.isNotEmpty()) append("Daily goal complete! +${xpResult.xpAwarded} XP")
                        if (xpResult.allGoalsCompleted) append(" • All goals done! +${xpResult.bonusXpAwarded} XP bonus")
                    }
                    scope.emitSideEffect(GameContract.SideEffect.ShowSnackbar(
                        com.example.mycomposeapp.core.ui.util.UiText.DynamicString(msg)
                    ))
                }
            } catch (_: Exception) { }
        }
    }
}
