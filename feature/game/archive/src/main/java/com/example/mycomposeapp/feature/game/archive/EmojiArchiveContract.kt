package com.example.mycomposeapp.feature.game.archive

import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle

object EmojiArchiveContract {

    data class State(
        val puzzles: List<DailyPuzzle> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    sealed interface SideEffect

    sealed interface Event
}
