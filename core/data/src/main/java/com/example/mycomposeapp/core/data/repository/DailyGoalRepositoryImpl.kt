package com.example.mycomposeapp.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyGoalRawProgress
import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class DailyGoalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DailyGoalRepository {

    override suspend fun getProgress(): DailyGoalRawProgress {
        val prefs = dataStore.data.first()
        return DailyGoalRawProgress(
            date = prefs[PreferenceKeys.DAILY_GOALS_DATE] ?: "",
            gamesPlayed = prefs[PreferenceKeys.DAILY_GOALS_GAMES_PLAYED] ?: 0,
            perfectScores = prefs[PreferenceKeys.DAILY_GOALS_PERFECT_SCORES] ?: 0,
            categoriesTried = parseCsv(prefs[PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED] ?: ""),
            gameModesTried = parseCsv(prefs[PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED] ?: ""),
            allCompletedDate = prefs[PreferenceKeys.DAILY_GOALS_ALL_COMPLETED_DATE] ?: ""
        )
    }

    override suspend fun saveProgress(progress: DailyGoalRawProgress) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.DAILY_GOALS_DATE] = progress.date
            prefs[PreferenceKeys.DAILY_GOALS_GAMES_PLAYED] = progress.gamesPlayed
            prefs[PreferenceKeys.DAILY_GOALS_PERFECT_SCORES] = progress.perfectScores
            prefs[PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED] =
                progress.categoriesTried.joinToString(",")
            prefs[PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED] =
                progress.gameModesTried.joinToString(",")
            prefs[PreferenceKeys.DAILY_GOALS_ALL_COMPLETED_DATE] = progress.allCompletedDate
        }
    }

    override suspend fun resetIfNewDay() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val savedDate = dataStore.data.first()[PreferenceKeys.DAILY_GOALS_DATE] ?: ""
        if (savedDate != today) {
            dataStore.edit { prefs ->
                prefs[PreferenceKeys.DAILY_GOALS_DATE] = today
                prefs[PreferenceKeys.DAILY_GOALS_GAMES_PLAYED] = 0
                prefs[PreferenceKeys.DAILY_GOALS_PERFECT_SCORES] = 0
                prefs[PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED] = ""
                prefs[PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED] = ""
            }
        }
    }

    private fun parseCsv(value: String): Set<String> =
        if (value.isBlank()) emptySet() else value.split(",").toSet()
}
