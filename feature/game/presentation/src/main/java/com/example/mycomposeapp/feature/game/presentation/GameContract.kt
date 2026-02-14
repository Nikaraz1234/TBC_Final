package com.example.mycomposeapp.feature.game.presentation

import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import com.example.mycomposeapp.feature.game.domain.model.Question
import com.example.mycomposeapp.feature.game.domain.model.SearchResult

object GameContract {

    sealed interface ModeState {
        data class Cover(
            val livesRemaining: Int = GameConstants.COVER_INITIAL_LIVES,
            val coins: Int = 0,
            val revealCost: Int = GameConstants.COVER_INITIAL_REVEAL_COST,
            val revealedCells: Set<Int> = emptySet(),
            val currentScore: Int = 0,
            val bestSessionStreak: Int = 0,
            val isFetchingMore: Boolean = false
        ) : ModeState {
            val hiddenCellCount: Int get() = GameConstants.COVER_GRID_CELLS - revealedCells.size
            val canAffordReveal: Boolean get() = coins >= revealCost && hiddenCellCount > 0
        }

        data class Emoji(
            val guessesRemaining: Int = GameConstants.EMOJI_INITIAL_GUESSES,
            val isHintUsed: Boolean = false,
            val hintText: String = "",
            val coins: Int = 0,
            val isFromArchive: Boolean = false
        ) : ModeState {
            val canAffordHint: Boolean get() = coins >= GameConstants.EMOJI_HINT_COST && !isHintUsed
        }

        data class Plot(
            val guessesRemaining: Int = GameConstants.PLOT_INITIAL_GUESSES,
            val revealedHintIndices: Set<Int> = emptySet(),
            val coins: Int = 0,
            val currentScore: Int = 0,
            val bestSessionStreak: Int = 0,
            val isFetchingMore: Boolean = false,
            val showInsufficientFundsWarning: Boolean = false
        ) : ModeState {
            val allHintsRevealed: Boolean
                get() = revealedHintIndices.size >= GameConstants.PLOT_HINT_COUNT
            val canAffordHint: Boolean
                get() = coins >= GameConstants.PLOT_HINT_COST && !allHintsRevealed
        }

        data class Screenshot(
            val guessesRemaining: Int = GameConstants.EMOJI_INITIAL_GUESSES,
            val coins: Int = 0,
            val currentScore: Int = 0,
            val livesRemaining: Int = 3,
            val bestSessionStreak: Int = 0,
            val isFetchingMore: Boolean = false
        ) : ModeState

        data object None : ModeState
    }

    data class State(
        val phase: GamePhase = GamePhase.Loading,
        val questions: List<Question> = emptyList(),
        val currentQuestionIndex: Int = 0,
        val userAnswer: String = "",
        val isAnswerRevealed: Boolean = false,
        val isAnswerCorrect: Boolean = false,
        val correctAnswersCount: Int = 0,
        val currentStreak: Int = 0,
        val totalTimeSpentSeconds: Int = 0,
        val searchResults: List<SearchResult> = emptyList(),
        val isSearching: Boolean = false,
        val gameResult: GameResult? = null,
        val errorMessage: String? = null,
        val isDailyMode: Boolean = false,
        val dailyCompleted: Boolean = false,
        val modeState: ModeState = ModeState.None
    ) {
        val currentQuestion: Question? get() = questions.getOrNull(currentQuestionIndex)
        val progressFraction: Float
            get() = if (questions.isEmpty()) 0f
            else (currentQuestionIndex + 1f) / questions.size

        val isCoverMode: Boolean get() = modeState is ModeState.Cover
        val isEmojiMode: Boolean get() = modeState is ModeState.Emoji
        val isPlotMode: Boolean get() = modeState is ModeState.Plot
        val coverState: ModeState.Cover? get() = modeState as? ModeState.Cover
        val emojiState: ModeState.Emoji? get() = modeState as? ModeState.Emoji
        val plotState: ModeState.Plot? get() = modeState as? ModeState.Plot

        val screenshotState: ModeState.Screenshot?
            get() = modeState as? ModeState.Screenshot

    }

    enum class GamePhase { Loading, Playing, AnswerRevealed, Results }

    sealed interface Event {
        data class OnAnswerTextChanged(val text: String) : Event
        data class OnAnswerSubmitted(val answer: String) : Event
        data class OnSuggestionSelected(val title: String) : Event
        data object OnNextQuestion : Event
        data object OnRetryGame : Event
        data object OnExitGame : Event
        data object OnRevealMore : Event
        data object OnUseHint : Event
    }

    sealed interface SideEffect {
        data object NavigateBack : SideEffect
        data class ShowSnackbar(val message: String) : SideEffect
    }
}
