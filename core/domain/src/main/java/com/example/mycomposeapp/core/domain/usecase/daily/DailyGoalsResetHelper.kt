package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class DailyGoalsResetHelper @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend fun ensureTodayReset() {
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
    }
}
