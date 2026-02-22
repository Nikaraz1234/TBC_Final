package com.example.mycomposeapp.feature.game.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.achievements.domain.usecase.CheckAndUnlockAchievementsUseCase
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.usecase.movies.SearchMoviesUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.DelegateScope
import com.example.mycomposeapp.feature.game.presentation.delegate.GameDelegateFactory
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import com.example.mycomposeapp.feature.game.presentation.navigation.GameRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    gameDelegateFactory: GameDelegateFactory,
    private val searchUseCase: SearchMoviesUseCase,
    private val checkAndUnlockAchievementsUseCase: CheckAndUnlockAchievementsUseCase
) : BaseViewModel<GameContract.State, GameContract.SideEffect, GameContract.Event>(
    GameContract.State()
), DelegateScope {

    private val route = savedStateHandle.toRoute<GameRoute>()
    private val delegate: GameModeDelegate = gameDelegateFactory.create(
        gameModeId = route.gameModeId,
        categoryType = route.categoryType,
        archiveDate = route.archiveDate
    )

    private var searchJob: Job? = null

    init {
        delegate.attach(this)
        delegate.loadGame()
    }

    // DelegateScope implementation
    override val coroutineScope get() = viewModelScope
    override fun currentState(): GameContract.State = uiState.value
    override fun updateState(reducer: GameContract.State.() -> GameContract.State) = setState(reducer)
    override fun emitSideEffect(effect: GameContract.SideEffect) = sendSideEffect(effect)

    fun onEvent(event: GameContract.Event) {
        when (event) {
            is GameContract.Event.OnAnswerTextChanged -> onAnswerTextChanged(event.text)
            is GameContract.Event.OnAnswerSubmitted -> delegate.onAnswerSubmitted(event.answer)
            is GameContract.Event.OnMangaSelected -> delegate.onAnswerSubmitted(event.selectedId.toString())
            is GameContract.Event.OnSuggestionSelected -> onSuggestionSelected(event.title)
            is GameContract.Event.OnNextQuestion -> delegate.onNextQuestion()
            is GameContract.Event.OnRetryGame -> delegate.onRetryGame()
            is GameContract.Event.OnExitGame -> delegate.onExitGame()
            is GameContract.Event.OnRevealMore -> delegate.onRevealMore()
            is GameContract.Event.OnUseHint -> delegate.onUseHint()
        }
    }

    private fun onAnswerTextChanged(text: String) {
        setState { copy(userAnswer = text) }
        if (text.length >= GameConstants.MIN_SEARCH_QUERY_LENGTH) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(GameConstants.SEARCH_DEBOUNCE_MS)
                searchUseCase(route.categoryType, text, route.gameModeId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> setState {
                            copy(searchResults = resource.data, isSearching = false)
                        }
                        is Resource.Loading -> setState { copy(isSearching = true) }
                        is Resource.Error -> setState {
                            copy(searchResults = emptyList(), isSearching = false)
                        }
                    }
                }
            }
        } else {
            setState { copy(searchResults = emptyList(), isSearching = false) }
        }
    }

    private fun onSuggestionSelected(title: String) {
        setState { copy(userAnswer = title, searchResults = emptyList()) }
        delegate.onAnswerSubmitted(title)
    }

    override suspend fun onGameCompleted(updatedStats: UserStats?) {
        val stats = updatedStats ?: return
        try {
            val checkResult = checkAndUnlockAchievementsUseCase(stats)
            if (checkResult.newlyUnlocked.isNotEmpty()) {
                val names = checkResult.newlyUnlocked.joinToString { it.name }
                sendSideEffect(
                    GameContract.SideEffect.ShowSnackbar(
                        UiText.StringResource(R.string.achievement_unlocked_format, listOf(names))
                    )
                )
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        delegate.onCleared()
    }
}
