package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyGoal
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class GetDailyGoalsUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke(): DailyGoalsProgress {
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

        val gamesPlayed = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, 0)
            .first()

        val perfectScores = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, 0)
            .first()

        val categoriesTried = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
            .first()

        val categoriesCount = if (categoriesTried.isBlank()) 0
        else categoriesTried.split(",").distinct().size

        val goals = listOf(
            DailyGoal(
                id = "games_played",
                title = "Play 3 games",
                currentProgress = gamesPlayed,
                targetProgress = 3
            ),
            DailyGoal(
                id = "perfect_score",
                title = "Get a perfect score",
                currentProgress = perfectScores,
                targetProgress = 1
            ),
            DailyGoal(
                id = "categories_tried",
                title = "Try 2 categories",
                currentProgress = categoriesCount,
                targetProgress = 2
            )
        )

        return DailyGoalsProgress(goals = goals)
    }
}
