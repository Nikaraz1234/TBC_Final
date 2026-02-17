package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyGoal
import com.example.mycomposeapp.core.domain.model.DailyGoalsAllProgress
import com.example.mycomposeapp.core.domain.model.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDailyGoalsUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val resetHelper: DailyGoalsResetHelper
) {
    suspend operator fun invoke(): DailyGoalsProgress {
        resetHelper.ensureTodayReset()

        val gamesPlayed = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, 0)
            .first()

        val perfectScores = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, 0)
            .first()

        val categoriesTried = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
            .first()

        val gameModesTried = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED, "")
            .first()

        val categoriesCount = if (categoriesTried.isBlank()) 0
        else categoriesTried.split(",").distinct().size

        val gameModesCount = if (gameModesTried.isBlank()) 0
        else gameModesTried.split(",").distinct().size

        val goals = listOf(
            DailyGoal(
                id = "games_played",
                title = "Play ${DailyGoalsConstants.GAMES_TO_PLAY_TARGET} games",
                currentProgress = gamesPlayed,
                targetProgress = DailyGoalsConstants.GAMES_TO_PLAY_TARGET
            ),
            DailyGoal(
                id = "perfect_score",
                title = "Get a perfect score",
                currentProgress = perfectScores,
                targetProgress = DailyGoalsConstants.PERFECT_SCORES_TARGET
            ),
            DailyGoal(
                id = "categories_tried",
                title = "Try ${DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET} categories",
                currentProgress = categoriesCount,
                targetProgress = DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET
            ),
            DailyGoal(
                id = "game_modes_tried",
                title = "Try ${DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET} different game modes",
                currentProgress = gameModesCount,
                targetProgress = DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET
            )
        )

        return DailyGoalsProgress(goals = goals)
    }

    suspend fun getAllProgress(): DailyGoalsAllProgress {
        resetHelper.ensureTodayReset()

        val categoriesTried = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
            .first()

        val gameModesTried = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED, "")
            .first()

        val categoriesList = if (categoriesTried.isBlank()) emptyList()
        else categoriesTried.split(",").distinct()

        val gameModesList = if (gameModesTried.isBlank()) emptyList()
        else gameModesTried.split(",").distinct()

        return DailyGoalsAllProgress(
            categoriesCount = categoriesList.size,
            gameModesCount = gameModesList.size,
            categoriesTried = categoriesList,
            gameModesTried = gameModesList
        )
    }
}
