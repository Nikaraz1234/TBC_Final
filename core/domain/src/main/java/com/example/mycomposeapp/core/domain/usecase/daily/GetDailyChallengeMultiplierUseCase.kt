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

        val completedDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_CHALLENGE_COMPLETED_DATE, "")
            .first()
        
        if (completedDate == today) {
            return 1
        }

        val availableGameModes = getAvailableGameModesUseCase()
        
        if (availableGameModes.isEmpty()) return 1
        
        val seed = abs(today.hashCode())
        val selectedIndex = seed % availableGameModes.size
        val todayChallenge = availableGameModes[selectedIndex]

        return if (todayChallenge.categoryType == currentCategoryType && 
                   todayChallenge.gameModeId == currentGameModeId) {
            DailyGoalsConstants.DAILY_CHALLENGE_MULTIPLIER
        } else {
            1
        }
    }
}
