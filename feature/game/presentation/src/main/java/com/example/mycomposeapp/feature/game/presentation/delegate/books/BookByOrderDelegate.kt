package com.example.mycomposeapp.feature.game.presentation.delegate.books

import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.usecase.daily.DailyGoalsManagerUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.UpdateDailyGoalProgressUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateCoinsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.UpdateUserStatsUseCase
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import com.example.mycomposeapp.feature.game.domain.usecase.GetDailyPuzzleUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.scoring.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookByOrderDelegate(
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
    private var submittedOnce: Boolean = false

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        val dateToLoad = archiveDate ?: LocalDate.now().toString()

        scope.coroutineScope.launch {
            coinMultiplier = if (!isFromArchive) {
                try { dailyGoalsManagerUseCase.getCoinMultiplier(categoryType, gameModeId) }
                catch (_: Exception) { 1 }
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
                                    modeState = GameContract.ModeState.BookByOrder()
                                )
                            }
                            return@collect
                        }

                        val content = puzzle.question.content as? QuestionContent.BookByOrder
                        if (content == null) {
                            scope.updateState {
                                copy(
                                    phase = GameContract.GamePhase.Loading,
                                    errorMessage = "Invalid content"
                                )
                            }
                            return@collect
                        }

                        val pool = content.events

                        submittedOnce = false
                        answerResults.clear()

                        scope.updateState {
                            copy(
                                questions = listOf(puzzle.question),
                                phase = GameContract.GamePhase.Playing,
                                isDailyMode = !isFromArchive,
                                isAnswerRevealed = false,
                                isAnswerCorrect = false,
                                modeState = GameContract.ModeState.BookByOrder(
                                    bookTitle = content.bookTitle,
                                    date = content.date,
                                    genre = content.genre,
                                    mainCharacter = content.mainCharacter,
                                    allEvents = content.events,
                                    pool = pool,
                                    slots = List(6) { null },
                                    correctOrder = content.answer, // stored but UI must not show it
                                    isFromArchive = isFromArchive,
                                    livesLeft = 1
                                )
                            )
                        }
                    }

                    is Resource.Error -> scope.updateState {
                        copy(
                            phase = GameContract.GamePhase.Loading,
                            errorMessage = resource.message
                        )
                    }

                    is Resource.Loading -> scope.updateState { copy(phase = GameContract.GamePhase.Loading) }
                }
            }
        }
    }

    override fun onAnswerSubmitted(answer: String) {
        val cmd = answer.trim()

        when {
            cmd.equals("SUBMIT", ignoreCase = true) -> submitInternal()

            cmd.startsWith("MOVE:", ignoreCase = true) -> {
                val parts = cmd.split(":")
                if (parts.size != 3) return
                val eventId = parts[1].toIntOrNull() ?: return
                val slotIndex = parts[2].toIntOrNull() ?: return
                moveInternal(eventId, slotIndex)
            }

            else -> {
                val maybeOrder = cmd.split(",").mapNotNull { it.trim().toIntOrNull() }
                val book = scope.currentState().modeState as? GameContract.ModeState.BookByOrder
                if (book != null && maybeOrder.size == 6) {
                    scope.updateState {
                        val cur = modeState as? GameContract.ModeState.BookByOrder ?: return@updateState this
                        copy(modeState = cur.copy(slots = maybeOrder))
                    }
                    submitInternal()
                }
            }
        }
    }

    private fun moveInternal(eventId: Int, targetSlotIndex: Int) {

        val book = scope.currentState().modeState as? GameContract.ModeState.BookByOrder ?: return
        if (targetSlotIndex !in 0..5) return

        val originSlot = book.slots.indexOfFirst { it == eventId }.takeIf { it >= 0 }
        val targetExisting = book.slots[targetSlotIndex]

        val newSlots = book.slots.toMutableList()

        if (originSlot != null) newSlots[originSlot] = null
        newSlots[targetSlotIndex] = eventId

        if (targetExisting != null && originSlot != null) {
            newSlots[originSlot] = targetExisting
        }

        val placed = newSlots.filterNotNull().toSet()
        val newPool = book.allEvents.filter { it.eventId !in placed }

        scope.updateState {
            val cur = modeState as? GameContract.ModeState.BookByOrder ?: return@updateState this
            copy(modeState = cur.copy(slots = newSlots, pool = newPool))
        }
    }

    private fun submitInternal() {
        val state = scope.currentState()
        val currentQuestion = state.currentQuestion ?: return
        val book = state.modeState as? GameContract.ModeState.BookByOrder ?: return

        if (submittedOnce) {
            scope.emitSideEffect(
                GameContract.SideEffect.ShowSnackbar(UiText.DynamicString("Already submitted."))
            )
            return
        }

        if (book.slots.any { it == null }) {
            scope.emitSideEffect(
                GameContract.SideEffect.ShowSnackbar(UiText.DynamicString("Place all 6 events first."))
            )
            return
        }

        val userOrder = book.slots.filterNotNull()
        val isCorrect = userOrder == book.correctOrder

        answerResults.add(
            AnswerResult(
                questionId = currentQuestion.id,
                correctAnswer = book.correctOrder.joinToString(","),
                userAnswer = userOrder.joinToString(","),
                isCorrect = isCorrect,
                timeSpentSeconds = 0
            )
        )

        submittedOnce = true

        scope.updateState {
            val cur = modeState as? GameContract.ModeState.BookByOrder ?: return@updateState this
            copy(
                phase = GameContract.GamePhase.AnswerRevealed,
                isAnswerRevealed = true,
                isAnswerCorrect = isCorrect,
                modeState = cur.copy(livesLeft = if (isCorrect) cur.livesLeft else 0),
                searchResults = emptyList()
            )
        }

        finalizeRun(isCorrect = isCorrect, currentQuestion = currentQuestion)
    }

    private fun finalizeRun(isCorrect: Boolean, currentQuestion: Question) {
        val coinsEarned =
            if (!isFromArchive && isCorrect) (GameConstants.EMOJI_DAILY_COINS_REWARD * coinMultiplier) else 0

        scope.coroutineScope.launch {
            try {
                val content = currentQuestion.content as? QuestionContent.BookByOrder
                val puzzleDate = content?.date ?: ""

                if (!isFromArchive) {
                    dailyPuzzleRepository.markDailyCompleted(puzzleDate)

                    if (coinsEarned > 0) {
                        val user = getCurrentUserUseCase().firstOrNull()
                        val oldCoins = user?.stats?.coins ?: 0
                        updateCoinsUseCase(oldCoins + coinsEarned)
                    }

                    val result = GameResult(
                        totalQuestions = 1,
                        correctAnswers = if (isCorrect) 1 else 0,
                        totalScore = coinsEarned,
                        timeTakenSeconds = 0,
                        bestStreak = if (isCorrect) 1 else 0,
                        answers = answerResults.toList(),
                        coinsEarned = coinsEarned,
                        finalCoinBalance = 0
                    )

                    val updatedStats = updateGameStatsUseCase(result, gameModeId, categoryType, true)
                    scope.onGameCompleted(updatedStats)

                    updateDailyGoalProgressUseCase.recordGamePlayed(
                        categoryType = categoryType,
                        gameModeId = gameModeId,
                        wasPerfect = isCorrect
                    )

                    if (coinMultiplier > 1 && isCorrect) {
                        dailyGoalsManagerUseCase.completeDailyChallenge()
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onNextQuestion() = scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    override fun onRetryGame() = scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    override fun onExitGame() = scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {}
}