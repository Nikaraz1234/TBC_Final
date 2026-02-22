package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.constants.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.math.abs

class GetDailyChallengeMultiplierUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val getAvailableGameModesUseCase: GetAvailableGameModesUseCase
) {
    suspend operator fun invoke(
        currentCategoryType: String,
        currentGameModeId: String
    ): Int {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        
        // Check if daily challenge is already completed today
        val completedDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_CHALLENGE_COMPLETED_DATE, "")
            .first()
        
        if (completedDate == today) {
            return 1 // No bonus if already completed
        }
        
        // Get today's daily challenge (same logic as GetDailyChallengeUseCase)
        val availableGameModes = getAvailableGameModesUseCase()
        
        if (availableGameModes.isEmpty()) return 1
        
        val seed = abs(today.hashCode())
        val selectedIndex = seed % availableGameModes.size
        val todayChallenge = availableGameModes[selectedIndex]
        
        // Check if current game matches today's daily challenge
        return if (todayChallenge.categoryType == currentCategoryType && 
                   todayChallenge.gameModeId == currentGameModeId) {
            DailyGoalsConstants.DAILY_CHALLENGE_MULTIPLIER
        } else {
            1
        }
    }
}
