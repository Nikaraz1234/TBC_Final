package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.constants.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.model.DailyGoalRawProgress
import com.example.mycomposeapp.core.domain.model.DailyGoalXpResult
import com.example.mycomposeapp.core.domain.model.recordGame
import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.rules.LevelingRules
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

class UpdateDailyGoalProgressUseCase @Inject constructor(
    private val repository: DailyGoalRepository,
    private val userRepository: UserRepository
) {
    suspend fun recordGamePlayed(
        categoryType: String,
        gameModeId: String,
        wasPerfect: Boolean
    ): DailyGoalXpResult {
        repository.resetIfNewDay()

        val before = repository.getProgress()
        val after = before.recordGame(categoryType, gameModeId, wasPerfect)

        val newlyCompleted = calculateNewlyCompleted(before, after)
        val (goalXp, bonusXp, allGoalsCompleted) = calculateXp(after, newlyCompleted)

        val progressToSave = if (allGoalsCompleted) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
            after.copy(allCompletedDate = today)
        } else {
            after
        }
        repository.saveProgress(progressToSave)

        if (goalXp + bonusXp > 0) {
            awardXp(goalXp + bonusXp)
        }

        return DailyGoalXpResult(
            newlyCompletedGoalIds = newlyCompleted,
            xpAwarded = goalXp,
            allGoalsCompleted = allGoalsCompleted,
            bonusXpAwarded = bonusXp
        )
    }

    private fun calculateNewlyCompleted(
        before: DailyGoalRawProgress,
        after: DailyGoalRawProgress
    ): List<String> = buildList {
        if (before.gamesPlayed < DailyGoalsConstants.GAMES_TO_PLAY_TARGET &&
            after.gamesPlayed >= DailyGoalsConstants.GAMES_TO_PLAY_TARGET) {
            add("games_played")
        }
        if (before.perfectScores < DailyGoalsConstants.PERFECT_SCORES_TARGET &&
            after.perfectScores >= DailyGoalsConstants.PERFECT_SCORES_TARGET) {
            add("perfect_score")
        }
        if (before.categoriesTried.size < DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET &&
            after.categoriesTried.size >= DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET) {
            add("categories_tried")
        }
        if (before.gameModesTried.size < DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET &&
            after.gameModesTried.size >= DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET) {
            add("game_modes_tried")
        }
    }

    private data class XpCalculationResult(
        val goalXp: Int,
        val bonusXp: Int,
        val allGoalsCompleted: Boolean
    )

    private fun calculateXp(
        after: DailyGoalRawProgress,
        newlyCompleted: List<String>
    ): XpCalculationResult {
        val goalXp = newlyCompleted.size * DailyGoalsConstants.GOAL_COMPLETION_XP

        val allGoalsMet = after.gamesPlayed >= DailyGoalsConstants.GAMES_TO_PLAY_TARGET &&
                after.perfectScores >= DailyGoalsConstants.PERFECT_SCORES_TARGET &&
                after.categoriesTried.size >= DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET &&
                after.gameModesTried.size >= DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return if (allGoalsMet && after.allCompletedDate != today) {
            XpCalculationResult(goalXp, DailyGoalsConstants.ALL_GOALS_BONUS_XP, allGoalsCompleted = true)
        } else {
            XpCalculationResult(goalXp, 0, allGoalsCompleted = false)
        }
    }

    private suspend fun awardXp(xp: Int) {
        val user = userRepository.getCurrentUser().firstOrNull() ?: return
        val stats = user.stats
        val newTotalXp = stats.totalXp + xp
        val newLevel = LevelingRules.calculateLevel(newTotalXp)
        userRepository.updateUserStats(stats.copy(totalXp = newTotalXp, level = newLevel))
    }
}
