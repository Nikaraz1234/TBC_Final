package com.example.mycomposeapp.feature.main.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.usecase.auth.LogoutUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.GameModeInfo
import com.example.mycomposeapp.core.domain.usecase.daily.GetDailyChallengeUseCase
import com.example.mycomposeapp.core.domain.usecase.daily.GetDailyGoalsUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.domain.usecase.user.RefreshUserUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.main.presentation.MainContract.Event
import com.example.mycomposeapp.feature.main.presentation.MainContract.SideEffect
import com.example.mycomposeapp.feature.main.presentation.MainContract.State
import com.example.mycomposeapp.feature.main.presentation.model.Categories
import com.example.mycomposeapp.feature.main.presentation.model.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val refreshUserUseCase: RefreshUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getDailyChallengeUseCase: GetDailyChallengeUseCase,
    private val getDailyGoalsUseCase: GetDailyGoalsUseCase
) : BaseViewModel<State, SideEffect, Event>(State()) {

    init {
        loadData()
    }

    private fun loadData() {
        setState {
            copy(
                categories = Categories.all,
                isLoading = false,
                tipStartIndex = generateTipStartIndex()
            )
        }

        viewModelScope.launch {
            try {
                refreshUserUseCase()
            } catch (_: Exception) { }
        }

        getCurrentUserUseCase()
            .onEach { user ->
                setState { copy(user = user) }
            }
            .launchIn(viewModelScope)

        loadDailyChallenge()
        loadDailyGoals()
    }

    private fun loadDailyChallenge() {
        viewModelScope.launch {
            val availableModes = getAvailableGameModeInfos()
            val challenge = getDailyChallengeUseCase(availableModes)
            setState { copy(dailyChallenge = challenge) }
        }
    }

    private fun getAvailableGameModeInfos(): List<GameModeInfo> =
        Categories.all.flatMap { category ->
            category.gameModes
                .filter { it.isAvailable }
                .map { gameMode ->
                    GameModeInfo(
                        categoryName = category.name,
                        categoryType = category.type.name,
                        gameModeName = gameMode.name,
                        gameModeId = gameMode.id
                    )
                }
        }

    private fun loadDailyGoals() {
        viewModelScope.launch {
            val goals = getDailyGoalsUseCase()
            setState { copy(dailyGoals = goals) }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnCategoryClicked -> {
                setState { copy(selectedCategory = event.category) }
            }
            is Event.OnGameModeClicked -> {
                handleGameModeClick(event.gameMode)
            }
            is Event.OnBackFromGameModes -> {
                setState { copy(selectedCategory = null) }
            }
            is Event.OnProfileClicked -> {
                sendSideEffect(SideEffect.NavigateToProfile)
            }
            is Event.OnQuickPlayClicked -> {
                handleQuickPlay()
            }
            is Event.OnDailyChallengeClicked -> {
                handleDailyChallengeClick()
            }
            is Event.OnLogoutClicked -> {
                handleLogout()
            }
            is Event.OnArchiveClicked -> {
                sendSideEffect(SideEffect.NavigateToArchive)
            }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            logoutUseCase()
            sendSideEffect(SideEffect.NavigateToWelcome)
        }
    }

    private fun handleGameModeClick(gameMode: GameMode) {
        if (!gameMode.isAvailable) {
            sendSideEffect(SideEffect.ShowSnackbar("${gameMode.name} is coming soon!"))
            return
        }

        val categoryType = uiState.value.selectedCategory?.type?.name ?: return
        sendSideEffect(SideEffect.NavigateToGame(gameMode.id, categoryType))
    }

    private fun handleQuickPlay() {
        val randomMode = selectRandomGameMode()
        if (randomMode == null) {
            sendSideEffect(SideEffect.ShowSnackbar("No game modes available yet!"))
            return
        }
        val (category, gameMode) = randomMode
        sendSideEffect(SideEffect.NavigateToGame(gameMode.id, category.type.name))
    }

    private fun selectRandomGameMode(): Pair<com.example.mycomposeapp.feature.main.presentation.model.Category, GameMode>? {
        val availableModes = Categories.all.flatMap { category ->
            category.gameModes
                .filter { it.isAvailable }
                .map { gameMode -> category to gameMode }
        }
        return availableModes.randomOrNull()
    }

    private fun handleDailyChallengeClick() {
        val challenge = uiState.value.dailyChallenge ?: return

        if (challenge.isCompleted) {
            sendSideEffect(SideEffect.ShowSnackbar("You've already completed today's challenge!"))
            return
        }

        sendSideEffect(SideEffect.NavigateToGame(challenge.gameModeId, challenge.categoryType))
    }

    private fun generateTipStartIndex(): Int =
        abs(System.currentTimeMillis().toInt()) % TIP_COUNT

    companion object {
        private const val TIP_COUNT = 8
    }
}
