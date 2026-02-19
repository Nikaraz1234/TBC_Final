package com.example.mycomposeapp.feature.game.presentation.delegate.comics

import com.example.mycomposeapp.core.domain.LevelingRules
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.ArrowDirection
import com.example.mycomposeapp.feature.game.domain.model.FeedbackColor
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameConstants.BASE_POINTS
import com.example.mycomposeapp.feature.game.domain.model.GameConstants.RANKLE_COINS_WIN
import com.example.mycomposeapp.feature.game.domain.model.GameConstants.RANKLE_INITIAL_ATTEMPTS
import com.example.mycomposeapp.feature.game.domain.model.GameConstants.WIN_THRESHOLD
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.RankleGuess
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.comics.GetRankleMangaUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.abs

class RankleDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateCoinsUseCase: UpdateCoinsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val getRankleMangaUseCase: GetRankleMangaUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val dailyGoalsManagerUseCase: DailyGoalsManagerUseCase,
    private val updateUserStatsUseCase: UpdateUserStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private val seenIds = mutableSetOf<Long>()
    private var roundsPlayed = 0
    private var totalCorrect = 0
    private var coinMultiplier: Int = 1
    private var gameStartTimeMs = 0L

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        scope.coroutineScope.launch {
            scope.updateState {
                copy(
                    phase = GameContract.GamePhase.Loading,
                    modeState = GameContract.ModeState.Rankle()
                )
            }

            coinMultiplier = try {
                dailyGoalsManagerUseCase.getCoinMultiplier(categoryType, gameModeId)
            } catch (_: Exception) { 1 }

            val user = getCurrentUserUseCase().firstOrNull()
            val initialCoins = user?.stats?.coins ?: 0

            scope.updateState {
                copy(
                    modeState = GameContract.ModeState.Rankle(coins = initialCoins)
                )
            }
            fetchMangaAndStart()
            gameStartTimeMs = System.currentTimeMillis()
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val state = scope.currentState()
        val rankle = state.rankleState ?: return
        val actualRating = rankle.actualRating ?: return
        val guessValue = answer.toDoubleOrNull() ?: return

        val diff = abs(guessValue - actualRating)
        val isCorrect = diff <= WIN_THRESHOLD
        val newStreak = state.currentStreak + 1

        val scoreGain = BASE_POINTS + GameConstants.STREAK_BONUS_MULTIPLIER * newStreak


        val color = when {
            isCorrect -> FeedbackColor.GREEN
            diff <= 0.1 -> FeedbackColor.RED
            diff <= 0.2 -> FeedbackColor.ORANGE
            diff <= 0.5 -> FeedbackColor.YELLOW
            diff <= 1.0 -> FeedbackColor.WHITE
            else -> FeedbackColor.GRAY
        }
        val direction = when {
            isCorrect -> ArrowDirection.NONE
            guessValue < actualRating -> ArrowDirection.UP
            else -> ArrowDirection.DOWN
        }

        val newGuesses = rankle.guesses + RankleGuess(
            input = guessValue,
            color = color,
            direction = direction
        )
        val newAttemptsLeft = rankle.attemptsLeft - 1
        val isRoundOver = isCorrect || newAttemptsLeft <= 0

        // Coin rewards: 5 for 1st try, 4 for 2nd, 3 for 3rd, 2 for 4th, 1 for 5th
        val attemptNumber = RANKLE_INITIAL_ATTEMPTS - rankle.attemptsLeft + 1
        val coinsEarned = if (isCorrect) (RANKLE_COINS_WIN - attemptNumber + 1).coerceAtLeast(0) * coinMultiplier else 0
        val newCoins = rankle.coins + coinsEarned

        if (isCorrect) totalCorrect++

        scope.updateState {
            copy(
                phase = if (isRoundOver) GameContract.GamePhase.AnswerRevealed else GameContract.GamePhase.Playing,
                isAnswerCorrect = isCorrect,
                modeState = rankle.copy(
                    guesses = newGuesses,
                    attemptsLeft = newAttemptsLeft,
                    isRoundOver = isRoundOver,
                    coins = newCoins
                )
            )
        }

        if (isCorrect) {
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
        }
    }

    override fun onNextQuestion() {
        roundsPlayed++
        val rankle = scope.currentState().rankleState ?: return
        val wasCorrect = scope.currentState().isAnswerCorrect

        answerResults.add(
            AnswerResult(
                questionId = "rankle_${rankle.mangaTitle}",
                correctAnswer = "${rankle.actualRating}",
                userAnswer = rankle.guesses.lastOrNull()?.input?.let { "%.2f".format(it) } ?: "",
                isCorrect = wasCorrect,
                timeSpentSeconds = 0
            )
        )

        if (!wasCorrect) {
            val newLives = rankle.livesRemaining - 1
            scope.updateState {
                copy(modeState = rankle.copy(livesRemaining = newLives))
            }
            if (newLives <= 0) {
                finishGame()
                return
            }
        }
        scope.coroutineScope.launch { fetchMangaAndStart() }
    }

    override fun onRetryGame() {
        answerResults.clear()
        seenIds.clear()
        roundsPlayed = 0
        totalCorrect = 0
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        scope.coroutineScope.launch {
            val rankle = scope.currentState().rankleState
            try { if (rankle != null) updateCoinsUseCase(rankle.coins) } catch (_: Exception) { }
            scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    private suspend fun fetchMangaAndStart() {
        getRankleMangaUseCase(seenIds).collect { resource ->
            when (resource) {
                is Resource.Success -> {
                    val manga = resource.data
                    seenIds.add(manga.id)

                    scope.updateState {
                        val rankle = rankleState ?: GameContract.ModeState.Rankle()
                        copy(
                            phase = GameContract.GamePhase.Playing,
                            questions = listOf(
                                Question(
                                    id = "rankle_${manga.id}",
                                    correctAnswer = "${manga.rating}",
                                    content = QuestionContent.Rankle(
                                        id = manga.id,
                                        imageUrl = manga.imageUrl,
                                        title = manga.title,
                                        rating = manga.rating
                                    )
                                )
                            ),
                            currentQuestionIndex = 0,
                            modeState = rankle.copy(
                                mangaTitle = manga.title,
                                mangaImageUrl = manga.imageUrl,
                                actualRating = manga.rating,
                                guesses = emptyList(),
                                attemptsLeft = RANKLE_INITIAL_ATTEMPTS,
                                isRoundOver = false
                            )
                        )
                    }
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

    private fun finishGame() {
        val state = scope.currentState()
        val rankle = state.rankleState ?: return

        val elapsed = ((System.currentTimeMillis() - gameStartTimeMs) / 1000).toInt()
        val score = totalCorrect * BASE_POINTS

        val result = GameResult(
            totalQuestions = answerResults.size,
            correctAnswers = totalCorrect,
            totalScore = score,
            timeTakenSeconds = elapsed,
            bestStreak = totalCorrect,
            answers = answerResults.toList(),
            isNewHighScore = false,
            coinsEarned = rankle.coins,
            finalCoinBalance = rankle.coins
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
