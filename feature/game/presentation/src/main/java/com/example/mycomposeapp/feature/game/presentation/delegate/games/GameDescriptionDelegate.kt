package com.example.mycomposeapp.feature.game.presentation.delegate.games

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.FetchDescriptionBatchUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.games.SearchGamesUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class GameDescriptionDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val fetchDescriptionBatchUseCase: FetchDescriptionBatchUseCase,
    private val searchGamesUseCase: SearchGamesUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()

    private val seenIds = mutableSetOf<String>()
    private val questionQueue =
        mutableListOf<com.example.mycomposeapp.feature.game.domain.model.Question>()

    private var prefetchJob: Job? = null
    private var searchJob: Job? = null
    private var coinMultiplier: Int = 1

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.Description()
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
                    modeState = GameContract.ModeState.Description(
                        coins = initialCoins,
                        isFetchingMore = false
                    )
                )
            }

            fetchDescriptionBatchAndStart()
        }
    }

    override fun onNextQuestion() {
        val description = scope.currentState().descriptionState ?: return
        if (description.livesRemaining <= 0) finishDescriptionGame()
        else presentNextDescriptionQuestion()
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenIds.clear()
        questionQueue.clear()
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val description = scope.currentState().descriptionState
            try {
                if (description != null) updateCoinsUseCase(description.coins)
            } catch (_: Exception) {}
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun onUseHint() {
        val state = scope.currentState()
        val mode = state.descriptionState ?: return
        val q = state.currentQuestion?.content as? QuestionContent.Description ?: return

        if (mode.hintStep >= 3) return
        if (mode.coins < mode.hintCost) {
            scope.updateState {
                copy(modeState = mode.copy(showInsufficientFundsWarning = true))
            }
            return
        }

        val nextStep = mode.hintStep + 1

        val updated = when (nextStep) {
            1 -> mode.copy(studioHint = q.studio ?: "Unknown studio")
            2 -> mode.copy(
                genreHint = q.genres.takeIf { it.isNotEmpty() }?.joinToString(", ")
                    ?: "Unknown genre"
            )
            3 -> mode.copy(yearHint = q.releaseYear?.toString() ?: "Unknown year")
            else -> mode
        }.copy(
            coins = mode.coins - mode.hintCost,
            hintCost = mode.hintCost * 2,
            hintStep = nextStep,
            showInsufficientFundsWarning = false
        )

        scope.updateState { copy(modeState = updated) }

        scope.coroutineScope.launch {
            try { updateCoinsUseCase(updated.coins) } catch (_: Exception) {}
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
        searchJob?.cancel()
    }

    fun onAnswerChanged(text: String) {
        handleAnswerChanged(text)
    }

    private fun handleAnswerChanged(text: String) {
        scope.updateState { copy(userAnswer = text) }

        if (scope.currentState().isAnswerRevealed) return

        val query = text.trim()
        if (query.length < 2) {
            searchJob?.cancel()
            scope.updateState { copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob?.cancel()
        searchJob = scope.coroutineScope.launch {

            searchGamesUseCase(query).collect { res ->
                when (res) {
                    is Resource.Loading -> scope.updateState { copy(isSearching = true) }

                    is Resource.Error -> scope.updateState {
                        copy(isSearching = false, searchResults = emptyList())
                    }

                    is Resource.Success -> {
                        val mapped = res.data.map { gs ->
                            SearchResult(
                                title = gs.name,
                                id = gs.id.toString(),
                                subtitle = "",
                                imageUrl = gs.screenshotUrls.firstOrNull()
                            )
                        }

                        if (scope.currentState().isAnswerRevealed) {
                            scope.updateState { copy(isSearching = false, searchResults = emptyList()) }
                        } else {
                            scope.updateState { copy(isSearching = false, searchResults = mapped) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchDescriptionBatchAndStart() {
        fetchDescriptionBatchUseCase(
            batchSize = 10,
            seenIds = seenIds
        ).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val fresh = resource.data.filter { it.id !in seenIds }

                    if (fresh.isEmpty()) {
                        scope.updateState {
                            val mode = descriptionState ?: GameContract.ModeState.Description()
                            copy(
                                phase = GameContract.GamePhase.Loading,
                                errorMessage = "No more description questions available.",
                                modeState = mode.copy(isFetchingMore = false)
                            )
                        }
                        return@collect
                    }

                    fresh.forEach { q -> seenIds.add(q.id) }
                    questionQueue.addAll(fresh)

                    presentNextDescriptionQuestion()
                }

                is Resource.Error -> {
                    scope.updateState {
                        copy(
                            phase = GameContract.GamePhase.Loading,
                            errorMessage = resource.message
                        )
                    }
                }

                is Resource.Loading -> scope.updateState { copy(phase = GameContract.GamePhase.Loading) }
            }
        }
    }

    private fun presentNextDescriptionQuestion() {
        if (questionQueue.isEmpty()) {
            scope.updateState {
                val mode = descriptionState ?: GameContract.ModeState.Description()
                copy(
                    modeState = mode.copy(
                        isFetchingMore = true,
                        hintStep = 0,
                        hintCost = GameConstants.EMOJI_HINT_COST,
                        studioHint = "",
                        genreHint = "",
                        yearHint = "",
                        showInsufficientFundsWarning = false
                    )
                )
            }
            scope.coroutineScope.launch { fetchDescriptionBatchAndStart() }
            return
        }

        val current = questionQueue.removeAt(0)

        // 👇 buffer the next question so questions.size >= 2 (so isLastQuestion becomes false)
        val bufferedNext = questionQueue.firstOrNull()

        scope.updateState {
            val mode = descriptionState ?: GameContract.ModeState.Description()

            val listForUi = buildList {
                add(current)
                if (bufferedNext != null) add(bufferedNext)
            }

            copy(
                questions = listForUi,
                currentQuestionIndex = 0, // always pointing at `current`
                phase = GameContract.GamePhase.Playing,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                errorMessage = null,
                modeState = mode.copy(
                    isFetchingMore = false,
                    hintStep = 0,
                    hintCost = GameConstants.EMOJI_HINT_COST,
                    studioHint = "",
                    genreHint = "",
                    yearHint = "",
                    showInsufficientFundsWarning = false
                )
            )
        }

        prefetchIfNeeded()
    }


    private fun prefetchIfNeeded() {
        if (questionQueue.size <= 2 && prefetchJob?.isActive != true) {
            prefetchJob = scope.coroutineScope.launch {
                fetchDescriptionBatchUseCase(
                    batchSize = 10,
                    seenIds = seenIds
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

    override fun onAnswerSubmitted(answer: String) {
        val state = scope.currentState()
        val currentQuestion = state.currentQuestion ?: return
        val mode = state.descriptionState ?: return

        val isCorrect = answer.trim()
            .equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)

        if (isCorrect) {
            val newStreak = state.currentStreak + 1
            val newBestStreak = maxOf(mode.bestSessionStreak, newStreak)

            val scoreGain =
                GameConstants.BASE_POINTS + GameConstants.STREAK_BONUS_MULTIPLIER * newStreak
            val newScore = mode.currentScore + scoreGain

            val newCoins = mode.coins + GameConstants.COVER_COINS_PER_CORRECT * coinMultiplier
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
                val currentMode = descriptionState ?: mode
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerRevealed = true,
                    isAnswerCorrect = true,
                    currentStreak = newStreak,
                    correctAnswersCount = newCorrect,
                    userAnswer = answer,
                    searchResults = emptyList(),
                    modeState = mode.copy(
                        bestSessionStreak = newBestStreak,
                        currentScore = newScore,
                        coins = newCoins
                    )
                )
            }

            scope.coroutineScope.launch {
                try { updateCoinsUseCase(newCoins) } catch (_: Exception) {}
            }

        } else {
            val newLives = (mode.livesRemaining - 1).coerceAtLeast(0)

            if (newLives > 0) {
                scope.updateState {
                    copy(
                        phase = GameContract.GamePhase.Playing,
                        isAnswerRevealed = false,
                        isAnswerCorrect = false,
                        currentStreak = 0,
                        userAnswer = "",
                        searchResults = emptyList(),
                        modeState = mode.copy(livesRemaining = newLives)
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
                    modeState = mode.copy(livesRemaining = 0),
                    questions = listOf(currentQuestion),
                    currentQuestionIndex = 0,

                )
            }
        }
    }

    private fun finishDescriptionGame() {
        val state = scope.currentState()
        val mode = state.descriptionState ?: return

        val result = GameResult(
            totalQuestions = answerResults.size,
            correctAnswers = state.correctAnswersCount,
            totalScore = mode.currentScore,
            timeTakenSeconds = state.totalTimeSpentSeconds,
            bestStreak = mode.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = false,
            coinsEarned = mode.coins,
            finalCoinBalance = mode.coins
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
                updateDailyGoalProgressUseCase.recordGamePlayed(
                    categoryType = categoryType,
                    gameModeId = gameModeId,
                    wasPerfect = false
                )
                if (coinMultiplier > 1) {
                    dailyGoalsManagerUseCase.completeDailyChallenge()
                }
            } catch (_: Exception) {}
        }
    }
}
