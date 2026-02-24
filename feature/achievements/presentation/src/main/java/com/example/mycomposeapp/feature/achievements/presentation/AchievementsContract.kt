package com.example.mycomposeapp.feature.achievements.presentation

import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.ui.util.UiText
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementCategory
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement

object AchievementsContract {

    data class State(
        val isLoading: Boolean = true,
        val achievements: List<AppAchievement> = emptyList(),
        val unlockedIds: List<String> = emptyList(),
        val newlyUnlocked: List<AppAchievement> = emptyList(),
        val selectedCategory: AchievementCategory? = null,
        val completionFilter: CompletionFilter = CompletionFilter.ALL,
        val errorMessage: String? = null,
        val userStats: UserStats? = null
    ) {
        val filteredAchievements: List<AppAchievement>
            get() {
                var list = achievements
                if (selectedCategory != null) {
                    list = list.filter { it.category == selectedCategory }
                }
                return when (completionFilter) {
                    CompletionFilter.ALL -> list
                    CompletionFilter.DONE -> list.filter { it.id in unlockedIds }
                    CompletionFilter.PENDING -> list.filter { it.id !in unlockedIds }
                }
            }

        val unlockedCount: Int get() = achievements.count { it.id in unlockedIds }
    }

    enum class CompletionFilter(val label: String) {
        ALL("All"),
        DONE("Done"),
        PENDING("Pending")
    }

    sealed interface Event {
        data class SelectCategory(val category: AchievementCategory?) : Event
        data class SetCompletionFilter(val filter: CompletionFilter) : Event
        data object Refresh : Event
        data object SeedAchievements : Event
    }

    sealed interface SideEffect {
        data class ShowSnackbar(val message: UiText) : SideEffect
    }
}
