package com.example.mycomposeapp.feature.game.presentation.delegate.comics

import com.example.mycomposeapp.core.domain.rules.LevelingRules
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.comics.MangaPair
import com.example.mycomposeapp.feature.game.domain.usecase.scoring.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.comics.FetchMangaPairsUseCase
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MangaRatingDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val fetchMangaPairsUseCase: FetchMangaPairsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenIds = mutableSetOf<Long>()
    private val pairQueue = mutableListOf<MangaPair>()
    private var prefetchJob: Job? = null
    private var lastAnswerWrong = false
    private var coinMultiplier: Int = 1

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.MangaRating()
                )
            }

            coinMultiplier = try {
                dailyGoalsManagerUseCase.getCoinMultiplier(categoryType, gameModeId)
            } catch (_: Exception) { 1 }

            val user = getCurrentUserUseCase().firstOrNull()
            val initialCoins = user?.stats?.coins ?: 0

            scope.updateState {
                copy(
                    modeState = GameContract.ModeState.MangaRating(coins = initialCoins)
                )
            }

            fetchBatchAndStart()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val state = scope.currentState()
        val manga = state.mangaRatingState ?: return
        val pair = manga.currentPair ?: return
        val selectedId = answer.toLongOrNull() ?: return

        val selected = if (pair.mangaA.id == selectedId) pair.mangaA else pair.mangaB
        val other = if (pair.mangaA.id == selectedId) pair.mangaB else pair.mangaA
        val isCorrect = selected.rating >= other.rating

        if (isCorrect) {
            val newStreak = manga.currentStreak + 1
            val newBestStreak = maxOf(manga.bestSessionStreak, newStreak)
            val scoreGain =
                GameConstants.BASE_POINTS + GameConstants.STREAK_BONUS_MULTIPLIER * newStreak
            val newCoins = manga.coins + GameConstants.MANGA_RATING_COINS_PER_CORRECT * coinMultiplier

            answerResults.add(
                AnswerResult(
                    questionId = "manga_${pair.mangaA.id}_${pair.mangaB.id}",
                    correctAnswer = "${selected.title} (${selected.rating})",
                    userAnswer = selected.title,
                    isCorrect = true,
                    timeSpentSeconds = 0
                )
            )

            lastAnswerWrong = false
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerCorrect = true,
                    modeState = manga.copy(
                        currentStreak = newStreak,
                        bestSessionStreak = newBestStreak,
                        coins = newCoins,
                        isAnswerCorrect = true,
                        selectedId = selectedId
                    )
                )
            }
            scope.coroutineScope.launch {
                try {
                    val user = getCurrentUserUseCase().firstOrNull() ?: return@launch
                    val stats = user.stats

                    val xpGain = GameConstants.XP_GAIN
                    val newTotalXp = stats.totalXp + xpGain
                    val newLevel = LevelingRules.calculateLevel(newTotalXp)

                    val updatedStats = stats.copy(
                        coins = stats.coins + GameConstants.COVER_COINS_PER_CORRECT,
                        points = stats.points + scoreGain,
                        totalXp = newTotalXp,
                        level = newLevel,
                        correctAnswers = stats.correctAnswers + 1,
                        bestStreak = maxOf(stats.bestStreak, newStreak)
                    )

                    updateUserStatsUseCase(updatedStats)
                } catch (_: Exception) { }
            }
        } else {
            answerResults.add(
                AnswerResult(
                    questionId = "manga_${pair.mangaA.id}_${pair.mangaB.id}",
                    correctAnswer = "${other.title} (${other.rating})",
                    userAnswer = selected.title,
                    isCorrect = false,
                    timeSpentSeconds = 0
                )
            )

            lastAnswerWrong = true
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.AnswerRevealed,
                    isAnswerCorrect = false,
                    modeState = manga.copy(
                        isAnswerCorrect = false,
                        selectedId = selectedId
                    )
                )
            }
        }
    }

    override fun onNextQuestion() {
        if (lastAnswerWrong) {
            finishGame()
        } else {
            presentNextPair()
        }
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenIds.clear()
        pairQueue.clear()
        lastAnswerWrong = false
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val manga = scope.currentState().mangaRatingState
            try { if (manga != null) updateCoinsUseCase(manga.coins) } catch (_: Exception) { }
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        prefetchJob?.cancel()
    }

    private suspend fun fetchBatchAndStart() {
        fetchMangaPairsUseCase(GameConstants.MANGA_RATING_BATCH_SIZE, seenIds).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val pairs = resource.data
                    pairs.forEach { pair ->
                        seenIds.add(pair.mangaA.id)
                        seenIds.add(pair.mangaB.id)
                    }
                    pairQueue.addAll(pairs)
                    presentNextPair()
                }
                is Resource.Error -> {
                    scope.updateState {
                        copy(phase = GameContract.GamePhase.Loading, errorMessage = resource.message)
                    }
                }
                is Resource.Loading -> {
                    scope.updateState { copy(phase = GameContract.GamePhase.Loading) }
                }
            }
        }
    }

    private fun presentNextPair() {
        if (pairQueue.isEmpty()) {
            scope.updateState {
                val manga = mangaRatingState ?: return@updateState this
                copy(modeState = manga.copy(isFetchingMore = true))
            }
            scope.coroutineScope.launch { fetchBatchAndStart() }
            return
        }

        val nextPair = pairQueue.removeAt(0)
        scope.updateState {
            val manga = mangaRatingState ?: GameContract.ModeState.MangaRating()
            copy(
                phase = GameContract.GamePhase.Playing,
                isAnswerCorrect = false,
                modeState = manga.copy(
                    currentPair = nextPair,
                    isFetchingMore = false,
                    isAnswerCorrect = false,
                    selectedId = null
                )
            )
        }
        prefetchIfNeeded()
    }

    private fun prefetchIfNeeded() {
        if (pairQueue.size <= GameConstants.MANGA_RATING_PREFETCH_THRESHOLD && prefetchJob?.isActive != true) {
            prefetchJob = scope.coroutineScope.launch {
                fetchMangaPairsUseCase(GameConstants.MANGA_RATING_BATCH_SIZE, seenIds).collect { resource ->
                    if (resource is Resource.Success) {
                        resource.data.forEach { pair ->
                            seenIds.add(pair.mangaA.id)
                            seenIds.add(pair.mangaB.id)
                        }
                        pairQueue.addAll(resource.data)
                    }
                }
            }
        }
    }

    private fun finishGame() {
        val state = scope.currentState()
        val manga = state.mangaRatingState ?: return

        val totalCorrect = answerResults.count { it.isCorrect }
        val score = totalCorrect * (GameConstants.COVER_BASE_POINTS + GameConstants.COVER_STREAK_BONUS * manga.bestSessionStreak)

        val result = GameResult(
            totalQuestions = answerResults.size,
            correctAnswers = totalCorrect,
            totalScore = score,
            timeTakenSeconds = 0,
            bestStreak = manga.bestSessionStreak,
            answers = answerResults.toList(),
            isNewHighScore = false,
            coinsEarned = manga.coins,
            finalCoinBalance = manga.coins
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
                if (score > actualHigh) {
                    scope.updateState {
                        copy(gameResult = gameResult?.copy(isNewHighScore = true))
                    }
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
            } catch (_: Exception) { }
        }
    }
}
