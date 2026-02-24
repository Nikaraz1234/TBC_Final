package com.example.mycomposeapp.feature.achievements.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.achievements.domain.usecase.CheckAndUnlockAchievementsUseCase
import com.example.mycomposeapp.feature.achievements.domain.usecase.SeedAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val checkAndUnlock: CheckAndUnlockAchievementsUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val seedAchievements: SeedAchievementsUseCase
) : BaseViewModel<
        AchievementsContract.State,
        AchievementsContract.SideEffect,
        AchievementsContract.Event>(
    initialState = AchievementsContract.State()
) {

    init {
        loadAchievements()
    }

    fun onEvent(event: AchievementsContract.Event) {
        when (event) {
            is AchievementsContract.Event.SelectCategory ->
                setState { copy(selectedCategory = event.category) }

            is AchievementsContract.Event.SetCompletionFilter ->
                setState { copy(completionFilter = event.filter) }

            AchievementsContract.Event.Refresh ->
                loadAchievements()

            AchievementsContract.Event.SeedAchievements -> viewModelScope.launch {
                try {
                    seedAchievements()
                    sendSideEffect(
                        AchievementsContract.SideEffect.ShowSnackbar(
                            UiText.DynamicString("Achievements seeded!")
                        )
                    )
                    loadAchievements()
                } catch (e: Exception) {
                    sendSideEffect(
                        AchievementsContract.SideEffect.ShowSnackbar(
                            UiText.DynamicString("Seed failed: ${e.message}")
                        )
                    )
                }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            try {
                val user = getCurrentUser()
                    .filterNotNull()
                    .first()

                val result = checkAndUnlock(user.stats)

                setState {
                    copy(
                        isLoading = false,
                        achievements = result.allAchievements,
                        unlockedIds = result.unlockedIds,
                        newlyUnlocked = result.newlyUnlocked,
                        userStats = user.stats
                    )
                }

                if (result.newlyUnlocked.isNotEmpty()) {
                    val names = result.newlyUnlocked.joinToString { it.name }
                    sendSideEffect(
                        AchievementsContract.SideEffect.ShowSnackbar(
                            UiText.StringResource(R.string.achievement_unlocked_format, listOf(names))
                        )
                    )
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false, errorMessage = e.message ?: "") }
            }
        }
    }
}
