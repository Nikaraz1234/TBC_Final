package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.model.DailyChallenge
import com.example.mycomposeapp.core.domain.model.DailyGoalXpResult
import com.example.mycomposeapp.core.domain.model.DailyGoalsAllProgress
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import javax.inject.Inject

class DailyGoalsManagerUseCase @Inject constructor(
    private val getDailyGoalsUseCase: GetDailyGoalsUseCase,
    private val updateDailyGoalProgressUseCase: UpdateDailyGoalProgressUseCase,
    private val getDailyChallengeUseCase: GetDailyChallengeUseCase,
    private val getDailyChallengeMultiplierUseCase: GetDailyChallengeMultiplierUseCase,
    private val completeDailyChallengeUseCase: CompleteDailyChallengeUseCase,
    private val getAvailableGameModesUseCase: GetAvailableGameModesUseCase
) {
    
    /**
     * Get current daily goals progress
     */
    suspend fun getDailyGoals(): DailyGoalsProgress = getDailyGoalsUseCase()
    
    /**
     * Get detailed progress for all categories and game modes
     */
    suspend fun getAllProgress(): DailyGoalsAllProgress = getDailyGoalsUseCase.getAllProgress()
    
    /**
     * Record that a game was played and update daily goals progress
     */
    suspend fun recordGamePlayed(
        categoryType: String,
        gameModeId: String,
        wasPerfect: Boolean
    ): DailyGoalXpResult =
        updateDailyGoalProgressUseCase.recordGamePlayed(categoryType, gameModeId, wasPerfect)
    
    /**
     * Get today's daily challenge
     */
    suspend fun getDailyChallenge(): DailyChallenge? = getDailyChallengeUseCase()
    
    /**
     * Get the coin multiplier for the current game (3x if it's the daily challenge and not completed)
     */
    suspend fun getCoinMultiplier(
        categoryType: String,
        gameModeId: String
    ): Int = getDailyChallengeMultiplierUseCase(categoryType, gameModeId)
    
    /**
     * Mark today's daily challenge as completed
     */
    suspend fun completeDailyChallenge() {
        completeDailyChallengeUseCase()
    }
    
    /**
     * Check if the current game is today's daily challenge
     */
    suspend fun isDailyChallengeGame(
        categoryType: String,
        gameModeId: String
    ): Boolean {
        val dailyChallenge = getDailyChallenge() ?: return false
        return dailyChallenge.categoryType == categoryType && dailyChallenge.gameModeId == gameModeId
    }
    
    /**
     * Get all available game modes for the daily challenge system
     */
    fun getAvailableGameModes() = getAvailableGameModesUseCase()
}
