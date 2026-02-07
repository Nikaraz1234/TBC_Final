package com.example.mycomposeapp.ui.screen.leaderboard

import com.example.mycomposeapp.ui.model.UserUi

object LeaderboardContract {
    data class State(
        val isLoading: Boolean = true,
        val users: List<UserUi> = emptyList(),
        val categories: List<String> = emptyList(),
        val selectedCategory: String = "",
        val modes: List<String> = emptyList(),
        val selectedMode: String = ""
    )

    sealed interface Event {
        data object LoadLeaderboard: Event
        data class CategoryChanged(val category: String) : Event
        data class ModeChanged(val mode: String) : Event

    }

    sealed interface SideEffect {
        data class ShowSnackBar(val message: String) : SideEffect
    }
}