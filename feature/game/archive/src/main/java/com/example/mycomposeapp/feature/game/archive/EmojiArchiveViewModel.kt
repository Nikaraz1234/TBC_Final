package com.example.mycomposeapp.feature.game.archive

import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.game.domain.repository.DailyPuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmojiArchiveViewModel @Inject constructor(
    private val dailyPuzzleRepository: DailyPuzzleRepository
) : BaseViewModel<EmojiArchiveContract.State, EmojiArchiveContract.SideEffect, EmojiArchiveContract.Event>(
    EmojiArchiveContract.State()
) {

    init {
        loadArchive()
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
