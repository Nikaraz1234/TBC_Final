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

    suspend fun getDailyGoals(): DailyGoalsProgress = getDailyGoalsUseCase()

    suspend fun getAllProgress(): DailyGoalsAllProgress = getDailyGoalsUseCase.getAllProgress()
    

    suspend fun recordGamePlayed(
        categoryType: String,
        gameModeId: String,
        wasPerfect: Boolean
    ): DailyGoalXpResult =
        updateDailyGoalProgressUseCase.recordGamePlayed(categoryType, gameModeId, wasPerfect)

    suspend fun getDailyChallenge(): DailyChallenge? = getDailyChallengeUseCase()

    suspend fun getCoinMultiplier(
        categoryType: String,
        gameModeId: String
    ): Int = getDailyChallengeMultiplierUseCase(categoryType, gameModeId)

    suspend fun completeDailyChallenge() {
        completeDailyChallengeUseCase()
    }

    suspend fun isDailyChallengeGame(
        categoryType: String,
        gameModeId: String
    ): Boolean {
        val dailyChallenge = getDailyChallenge() ?: return false
        return dailyChallenge.categoryType == categoryType && dailyChallenge.gameModeId == gameModeId
    }

    fun getAvailableGameModes() = getAvailableGameModesUseCase()
}
