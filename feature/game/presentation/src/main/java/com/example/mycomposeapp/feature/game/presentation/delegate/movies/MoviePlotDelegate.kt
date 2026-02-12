package com.example.mycomposeapp.feature.game.presentation.delegate.movies

import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.feature.game.domain.model.AnswerResult
import com.example.mycomposeapp.feature.game.domain.model.GameConfig
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.usecase.CalculateScoreUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.GetQuestionsUseCase
import com.example.mycomposeapp.feature.game.domain.usecase.UpdateGameStatsUseCase
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MoviePlotDelegate(
    private val categoryType: String,
    private val gameModeId: String,
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : GameModeDelegate {

    private lateinit var scope: DelegateScope
    private val answerResults = mutableListOf<AnswerResult>()
    private var timerJob: Job? = null
    private var questionStartTime = System.currentTimeMillis()
    private val timeLimitSeconds = GameConstants.TIME_LIMIT_SECONDS

    override fun attach(scope: DelegateScope) {
        this.scope = scope
    }

    override fun loadGame() {
        val config = GameConfig(
            categoryType = categoryType,
            gameModeId = gameModeId,
            questionCount = GameConstants.PLOT_QUESTION_COUNT
        )
        scope.coroutineScope.launch {
            getQuestionsUseCase(config).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        scope.updateState {
                            copy(
                                questions = resource.data,
                                phase = GameContract.GamePhase.Playing,
                                modeState = GameContract.ModeState.Plot(
                                    timeRemainingSeconds = timeLimitSeconds,
                                    blurLevel = GameConstants.PLOT_INITIAL_BLUR
                                )
                            )
                        }
                        startTimer()
                        questionStartTime = System.currentTimeMillis()
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
        timerJob?.cancel()
        val currentQuestion = scope.currentState().currentQuestion ?: return
        val timeSpent = ((System.currentTimeMillis() - questionStartTime) / 1000).toInt()
            .coerceAtMost(timeLimitSeconds)
        val isCorrect = answer.trim().equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)

        answerResults.add(
            AnswerResult(
                questionId = currentQuestion.id,
                correctAnswer = currentQuestion.correctAnswer,
                userAnswer = answer,
                isCorrect = isCorrect,
                timeSpentSeconds = timeSpent
            )
        )

        val state = scope.currentState()
        val newStreak = if (isCorrect) state.currentStreak + 1 else 0
        val newCorrectCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount

        scope.updateState {
            copy(
                phase = GameContract.GamePhase.AnswerRevealed,
                isAnswerRevealed = true,
                isAnswerCorrect = isCorrect,
                currentStreak = newStreak,
                correctAnswersCount = newCorrectCount,
                totalTimeSpentSeconds = totalTimeSpentSeconds + timeSpent,
                searchResults = emptyList()
            )
        }
    }

    override fun onNextQuestion() {
        val state = scope.currentState()
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= state.questions.size) {
            finishGame()
            return
        }

        scope.updateState {
            copy(
                currentQuestionIndex = nextIndex,
                userAnswer = "",
                isAnswerRevealed = false,
                isAnswerCorrect = false,
                phase = GameContract.GamePhase.Playing,
                searchResults = emptyList(),
                modeState = GameContract.ModeState.Plot(
                    timeRemainingSeconds = timeLimitSeconds,
                    blurLevel = GameConstants.PLOT_INITIAL_BLUR
                )
            )
        }
        startTimer()
        questionStartTime = System.currentTimeMillis()
    }

    override fun onRetryGame() {
        answerResults.clear()
        scope.updateState { GameContract.State() }
        loadGame()
    }

    override fun onExitGame() {
        timerJob?.cancel()
        scope.emitSideEffect(GameContract.SideEffect.NavigateBack)
    }

    override fun onRevealMore() {
        val state = scope.currentState()
        val plot = state.plotState ?: return
        scope.updateState {
            copy(modeState = plot.copy(blurLevel = (plot.blurLevel - GameConstants.PLOT_BLUR_STEP).coerceAtLeast(0f)))
        }
    }

    override fun getAnswerResults(): List<AnswerResult> = answerResults.toList()

    override fun onCleared() {
        timerJob?.cancel()
    }

    private fun finishGame() {
        timerJob?.cancel()
        val result = calculateScoreUseCase(answerResults, timeLimitSeconds)
        scope.updateState {
            copy(
                phase = GameContract.GamePhase.Results,
                gameResult = result
            )
        }

        scope.coroutineScope.launch {
            try {
                updateGameStatsUseCase(result, gameModeId, categoryType, false)
            } catch (_: Exception) { }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.coroutineScope.launch {
            var remaining = timeLimitSeconds
            while (remaining > 0) {
                val plot = scope.currentState().plotState ?: return@launch
                scope.updateState {
                    copy(modeState = plot.copy(timeRemainingSeconds = remaining))
                }
                delay(1000)
                remaining--
            }
            val plot = scope.currentState().plotState ?: return@launch
            scope.updateState {
                copy(modeState = plot.copy(timeRemainingSeconds = 0))
            }
            handleTimeUp()
        }
    }

    private fun handleTimeUp() {
        val currentQuestion = scope.currentState().currentQuestion ?: return

        answerResults.add(
            AnswerResult(
                questionId = currentQuestion.id,
                correctAnswer = currentQuestion.correctAnswer,
                userAnswer = null,
                isCorrect = false,
                timeSpentSeconds = timeLimitSeconds
            )
        )

        scope.updateState {
            copy(
                phase = GameContract.GamePhase.AnswerRevealed,
                isAnswerRevealed = true,
                isAnswerCorrect = false,
                currentStreak = 0,
                totalTimeSpentSeconds = totalTimeSpentSeconds + timeLimitSeconds,
                searchResults = emptyList()
            )
        }
    }
}
