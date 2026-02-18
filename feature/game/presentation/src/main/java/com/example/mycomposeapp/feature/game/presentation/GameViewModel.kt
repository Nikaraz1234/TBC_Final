package com.example.mycomposeapp.feature.game.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.game.domain.usecase.SearchUseCase
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
    private val searchUseCase: SearchUseCase
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
        if (text.length >= 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                delay(300)
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

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        delegate.onCleared()
    }
}
