package com.example.mycomposeapp.feature.leaderboard.presentation

import com.example.mycomposeapp.core.domain.model.User

object LeaderboardContract {
    data class State(
        val isLoading: Boolean = true,
        val users: List<User> = emptyList(),
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