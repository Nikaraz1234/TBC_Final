package com.example.mycomposeapp.feature.game.archive

import androidx.lifecycle.SavedStateHandle
import com.example.mycomposeapp.core.domain.model.CategoryType
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmojiArchiveViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dailyPuzzleRepositories: Map<String, @JvmSuppressWildcards DailyPuzzleRepository>
) : BaseViewModel<EmojiArchiveContract.State, EmojiArchiveContract.SideEffect, EmojiArchiveContract.Event>(
    EmojiArchiveContract.State()
) {

    private val categoryType: String =
        savedStateHandle["categoryType"] ?: CategoryType.MOVIES.name

    private val dailyPuzzleRepository = dailyPuzzleRepositories[categoryType]
        ?: error("No daily puzzle repository for $categoryType")

    init {
        loadArchive()
    }

    fun onEvent(event: EmojiArchiveContract.Event) {
        when (event) {
            EmojiArchiveContract.Event.OpenDatePicker -> {
                setState { copy(isDatePickerOpen = true) }
            }
            EmojiArchiveContract.Event.CloseDatePicker -> {
                setState { copy(isDatePickerOpen = false) }
            }
            is EmojiArchiveContract.Event.DatePicked -> {
                setState {
                    copy(
                        selectedDate = event.date,
                        isDatePickerOpen = false
                    )
                }
            }
            EmojiArchiveContract.Event.ClearDateFilter -> {
                setState { copy(selectedDate = null) }
            }
        }
    }

    private fun loadArchive() {
        handleResponse(
            apiCall = { dailyPuzzleRepository.getArchivePuzzles() },
            onSuccess = { puzzles ->
                setState { copy(puzzles = puzzles, isLoading = false) }
            },
            onError = { message ->
                setState { copy(isLoading = false, error = message) }
            },
            onLoading = {
                setState { copy(isLoading = true) }
            }
        )
    }

}
