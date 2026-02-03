package com.example.mycomposeapp.ui.screen.leaderboard

object LeaderboardContract {
    data class State(
        val isLoading: Boolean = true,
    )

    sealed interface Event {
        data object LoadLeaderboard: Event
    }

    sealed interface SideEffect {
        data class ShowSnackBar(val message: String) : SideEffect
    }
}