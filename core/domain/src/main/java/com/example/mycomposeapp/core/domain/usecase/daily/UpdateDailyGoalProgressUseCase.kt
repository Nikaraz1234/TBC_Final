package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class UpdateDailyGoalProgressUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun recordGamePlayed(categoryType: String, wasPerfect: Boolean) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val savedDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_DATE, "")
            .first()

        if (savedDate != today) {
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_DATE, today)
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, 0)
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, 0)
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
        }

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
    }
}
