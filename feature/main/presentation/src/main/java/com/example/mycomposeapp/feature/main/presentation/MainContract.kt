package com.example.mycomposeapp.feature.main.presentation

import com.example.mycomposeapp.core.domain.model.DailyChallenge
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.feature.main.presentation.model.Category
import com.example.mycomposeapp.feature.main.presentation.model.GameMode

object MainContract {

    data class State(
        val user: User? = null,
        val categories: List<Category> = emptyList(),
        val selectedCategory: Category? = null,
        val isLoading: Boolean = true,
        val dailyChallenge: DailyChallenge? = null,
        val dailyGoals: DailyGoalsProgress? = null,
        val tipStartIndex: Int = 0
    )

    sealed interface Event {
        data class OnCategoryClicked(val category: Category) : Event
        data class OnGameModeClicked(val gameMode: GameMode) : Event
        data object OnBackFromGameModes : Event
        data object OnProfileClicked : Event
        data object OnQuickPlayClicked : Event
        data object OnDailyChallengeClicked : Event
        data object OnLogoutClicked : Event
        data object OnArchiveClicked : Event
    }

    sealed interface SideEffect {
        data class NavigateToGame(val gameModeId: String, val categoryType: String) : SideEffect
        data object NavigateToProfile : SideEffect
        data class ShowSnackbar(val message: String) : SideEffect
        data object NavigateToWelcome : SideEffect
        data object NavigateToArchive : SideEffect
    }
}
