package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.constants.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyGoalXpResult
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.rules.LevelingRules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class UpdateDailyGoalProgressUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val resetHelper: EnsureDailyGoalsResetUseCase,
    private val userRepository: UserRepository
) {
    suspend fun recordGamePlayed(
        categoryType: String,
        gameModeId: String,
        wasPerfect: Boolean
    ): DailyGoalXpResult {
        resetHelper.ensureTodayReset()

        // --- Read pre-update values ---
        val currentGames = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, 0)
            .first()
        val currentPerfect = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_PERFECT_SCORES, 0)
            .first()
        val currentCategories = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED, "")
            .first()
        val categoriesList = if (currentCategories.isBlank()) mutableListOf()
        else currentCategories.split(",").toMutableList()

        val currentGameModes = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED, "")
            .first()
        val gameModesList = if (currentGameModes.isBlank()) mutableListOf()
        else currentGameModes.split(",").toMutableList()

        val gameModeKey = "${categoryType}_${gameModeId}"

        // --- Detect threshold crossings before update ---
        val gamesGoalNewlyComplete = currentGames < DailyGoalsConstants.GAMES_TO_PLAY_TARGET &&
                currentGames + 1 >= DailyGoalsConstants.GAMES_TO_PLAY_TARGET
        val perfectGoalNewlyComplete = wasPerfect &&
                currentPerfect < DailyGoalsConstants.PERFECT_SCORES_TARGET &&
                currentPerfect + 1 >= DailyGoalsConstants.PERFECT_SCORES_TARGET
        val newCategoryAdded = categoryType !in categoriesList
        val categoriesGoalNewlyComplete = newCategoryAdded &&
                categoriesList.size < DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET &&
                categoriesList.size + 1 >= DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET
        val newGameModeAdded = gameModeKey !in gameModesList
        val gameModesGoalNewlyComplete = newGameModeAdded &&
                gameModesList.size < DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET &&
                gameModesList.size + 1 >= DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET

        // --- Perform updates ---
        dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_GAMES_PLAYED, currentGames + 1)

        if (wasPerfect) {
            dataStoreManager.setPreference(
                PreferenceKeys.DAILY_GOALS_PERFECT_SCORES,
                currentPerfect + 1
            )
        }

        if (newCategoryAdded) {
            categoriesList.add(categoryType)
            dataStoreManager.setPreference(
                PreferenceKeys.DAILY_GOALS_CATEGORIES_TRIED,
                categoriesList.joinToString(",")
            )
        }

        if (newGameModeAdded) {
            gameModesList.add(gameModeKey)
            dataStoreManager.setPreference(
                PreferenceKeys.DAILY_GOALS_GAME_MODES_TRIED,
                gameModesList.joinToString(",")
            )
        }

        // --- Compute per-goal XP ---
        val newlyCompleted = buildList {
            if (gamesGoalNewlyComplete) add("games_played")
            if (perfectGoalNewlyComplete) add("perfect_score")
            if (categoriesGoalNewlyComplete) add("categories_tried")
            if (gameModesGoalNewlyComplete) add("game_modes_tried")
        }
        val goalXp = newlyCompleted.size * DailyGoalsConstants.GOAL_COMPLETION_XP

        // --- Check all-goals bonus (uses post-update counts) ---
        val newGamesCount = currentGames + 1
        val newPerfectCount = if (wasPerfect) currentPerfect + 1 else currentPerfect
        val newCategoriesCount = categoriesList.size  // mutated above
        val newGameModesCount = gameModesList.size     // mutated above

        val allGoalsMet = newGamesCount >= DailyGoalsConstants.GAMES_TO_PLAY_TARGET &&
                newPerfectCount >= DailyGoalsConstants.PERFECT_SCORES_TARGET &&
                newCategoriesCount >= DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET &&
                newGameModesCount >= DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val lastBonusDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_GOALS_ALL_COMPLETED_DATE, "")
            .first()

        val bonusXp: Int
        val allGoalsCompleted: Boolean
        if (allGoalsMet && lastBonusDate != today) {
            bonusXp = DailyGoalsConstants.ALL_GOALS_BONUS_XP
            allGoalsCompleted = true
            dataStoreManager.setPreference(PreferenceKeys.DAILY_GOALS_ALL_COMPLETED_DATE, today)
        } else {
            bonusXp = 0
            allGoalsCompleted = false
        }

        val totalXp = goalXp + bonusXp

        // --- Award XP if any ---
        if (totalXp > 0) {
            try {
                val user = userRepository.getCurrentUser().firstOrNull()
                if (user != null) {
                    val stats = user.stats
                    val newTotalXp = stats.totalXp + totalXp
                    val newLevel = LevelingRules.calculateLevel(newTotalXp)
                    val updatedStats = stats.copy(
                        totalXp = newTotalXp,
                        level = newLevel
                    )
                    userRepository.updateUserStats(updatedStats)
                }
            } catch (_: Exception) { }
        }

        return DailyGoalXpResult(
            newlyCompletedGoalIds = newlyCompleted,
            xpAwarded = goalXp,
            allGoalsCompleted = allGoalsCompleted,
            bonusXpAwarded = bonusXp
        )
    }
}
