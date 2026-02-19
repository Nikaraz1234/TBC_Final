package com.example.mycomposeapp.feature.leaderboard.presentation

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.usecase.user.GetAllUsersUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
) : BaseViewModel<
        LeaderboardContract.State,
        LeaderboardContract.SideEffect,
        LeaderboardContract.Event
        >(
    initialState = LeaderboardContract.State()
){

    fun onEvent(event: LeaderboardContract.Event){
        when(event){
            is LeaderboardContract.Event.CategoryChanged -> categoryChanged(event.category)
            LeaderboardContract.Event.LoadLeaderboard -> loadUsers()
            is LeaderboardContract.Event.ModeChanged -> modeChanged(event.mode)
        }
    }

    private fun loadUsers() {
        handleResponse(
            apiCall = { getAllUsersUseCase() },
            onSuccess = { users ->
                setState {
                    copy(
                        isLoading = false,
                        users = users,
                        filteredUsers = users.sortedByDescending { it.stats.points },
                        error = null,
                        selectedStatsKey = null
                    )
                }
            },
            onError = { message ->
                setState { copy(isLoading = false, error = message) }
            },
            onLoading = {
                setState { copy(isLoading = true) }
            }
        )
    }
    private fun categoryChanged(category: CategoryType) {
        val modes = getModesForCategory(category)
        val defaultMode = modes.firstOrNull().orEmpty()
        val key = firestoreStatsKey(category, defaultMode)

        setState {
            copy(
                selectedCategory = category,
                modes = modes,
                selectedMode = defaultMode,
                selectedStatsKey = key,
                filteredUsers = users.sortedByDescending { it.stats.highScore[key] ?: 0 }
            )
        }
    }


    private fun modeChanged(mode: String) {
        val key = firestoreStatsKey(uiState.value.selectedCategory, mode)

        setState {
            copy(
                selectedMode = mode,
                selectedStatsKey = key,
                filteredUsers = users.sortedByDescending { it.stats.highScore[key] ?: 0 }
            )
        }
    }


    private fun firestoreModeId(modeLabel: String): String = when (modeLabel) {
        "Cover" -> GameModeIds.COVER
        "Emoji" -> GameModeIds.EMOJI
        "Plot"  -> GameModeIds.PLOT

        "Screenshot"  -> GameModeIds.GAME_SCREENSHOT
        "Description" -> GameModeIds.GAME_DESCRIPTION
        "Achievement" -> GameModeIds.GAME_ACHIEVEMENT

        "Rating" -> GameModeIds.MANGA_RATING
        "Rankle" -> GameModeIds.RANKLE

        else -> modeLabel.lowercase()
    }

    private fun firestoreStatsKey(
        category: CategoryType?,
        modeLabel: String
    ): String {
        if (category == null) return ""
        val modeId = firestoreModeId(modeLabel)
        return GameModeIds.statsKey(category.firestorePrefix, modeId)
    }
    private fun getModesForCategory(category: CategoryType): List<String> {
        return when (category) {
            CategoryType.Movie -> listOf("Cover", "Emoji", "Plot")
            CategoryType.Game -> listOf("Screenshot", "Description", "Achievement")
            CategoryType.Comics -> listOf("Rankle", "Rating", "Emoji")
        }
    }

}