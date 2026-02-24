package com.example.mycomposeapp.feature.game.archive

import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle

object EmojiArchiveContract {

    data class State(
        val puzzles: List<DailyPuzzle> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val isDatePickerOpen: Boolean = false,
        val selectedDate: String? = null
    ){
        val visiblePuzzles: List<DailyPuzzle>
            get() = selectedDate?.let { d -> puzzles.filter { it.date == d } } ?: puzzles
    }

    sealed interface SideEffect

    sealed interface Event{
        data object OpenDatePicker : Event
        data object CloseDatePicker : Event
        data class DatePicked(val date: String) : Event
        data object ClearDateFilter : Event
    }
}
