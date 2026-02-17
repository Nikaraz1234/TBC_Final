package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateDailyGoalProgressUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val resetHelper: DailyGoalsResetHelper
) {
    suspend fun recordGamePlayed(categoryType: String, gameModeId: String, wasPerfect: Boolean) {
        resetHelper.ensureTodayReset()

        val currentGames = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, 0)
            .first()
        dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, currentGames + 1)

        if (wasPerfect) {
            val currentPerfect = dataStoreManager
                .getPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, 0)
                .first()
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, currentPerfect + 1)
        }

        val currentCategories = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
            .first()
        val categoriesList = if (currentCategories.isBlank()) mutableListOf()
        else currentCategories.split(",").toMutableList()
        if (categoryType !in categoriesList) {
            categoriesList.add(categoryType)
            dataStoreManager.setPreference(
                PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED,
                categoriesList.joinToString(",")
            )
        }

        val currentGameModes = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED, "")
            .first()
        val gameModesList = if (currentGameModes.isBlank()) mutableListOf()
        else currentGameModes.split(",").toMutableList()

        val gameModeKey = "${categoryType}_${gameModeId}"
        if (gameModeKey !in gameModesList) {
            gameModesList.add(gameModeKey)
            dataStoreManager.setPreference(
                PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED,
                gameModesList.joinToString(",")
            )
        }
    }
}
