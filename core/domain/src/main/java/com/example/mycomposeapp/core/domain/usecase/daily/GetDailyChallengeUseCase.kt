package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyChallenge
import com.example.mycomposeapp.core.domain.model.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.model.GameModeInfo
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.math.abs

class GetDailyChallengeUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val getAvailableGameModesUseCase: GetAvailableGameModesUseCase
) {
    suspend operator fun invoke(): DailyChallenge? {
        val availableGameModes = getAvailableGameModesUseCase()
        if (availableGameModes.isEmpty()) return null

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val seed = abs(today.hashCode())
        val selectedIndex = seed % availableGameModes.size
        val selected = availableGameModes[selectedIndex]

        val completedDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_CHALLENGE_COMPLETED_DATE, "")
            .first()

        val isCompleted = completedDate == today

        return DailyChallenge(
            categoryName = selected.categoryName,
            categoryType = selected.categoryType,
            gameModeName = selected.gameModeName,
            gameModeId = selected.gameModeId,
            bonusMultiplier = DailyGoalsConstants.DAILY_CHALLENGE_MULTIPLIER,
            isCompleted = isCompleted
        )
    }
}
