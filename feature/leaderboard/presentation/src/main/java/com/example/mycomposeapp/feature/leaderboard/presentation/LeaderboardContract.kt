package com.example.mycomposeapp.feature.leaderboard.presentation

import com.example.mycomposeapp.core.domain.model.User

object LeaderboardContract {
    data class State(
        val isLoading: Boolean = true,
        val users: List<User> = emptyList(),
        val filteredUsers: List<User> = emptyList(),
        val categories: List<CategoryType> = CategoryType.entries.toList(),
        val selectedCategory: CategoryType? = null,
        val modes: List<String> = emptyList(),
        val selectedMode: String = "",
        val displayScore: Int = 0,
        val error: String? = null,
        val selectedStatsKey: String? = null
    )

    sealed interface Event {
        data object LoadLeaderboard: Event
        data class CategoryChanged(val category: CategoryType) : Event
        data class ModeChanged(val mode: String) : Event

    }

    sealed interface SideEffect {
        data class ShowSnackBar(val message: String) : SideEffect
    }
}