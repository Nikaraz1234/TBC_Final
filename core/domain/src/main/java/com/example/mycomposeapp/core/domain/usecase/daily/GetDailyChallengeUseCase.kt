package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.DailyChallenge
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.math.abs

data class GameModeInfo(
    val categoryName: String,
    val categoryType: String,
    val gameModeName: String,
    val gameModeId: String
)

class GetDailyChallengeUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    suspend operator fun invoke(availableGameModes: List<GameModeInfo>): DailyChallenge? {
        if (availableGameModes.isEmpty()) return null

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val seed = abs(today.hashCode())
        val selectedIndex = seed % availableGameModes.size
        val selected = availableGameModes[selectedIndex]

        val bonusOptions = listOf(2, 3, 5)
        val bonusMultiplier = bonusOptions[seed % bonusOptions.size]

        val completedDate = dataStoreManager
            .getPreference(PreferenceKeys.DAILY_CHALLENGE_COMPLETED_DATE, "")
            .first()

        val isCompleted = completedDate == today

        return DailyChallenge(
            categoryName = selected.categoryName,
            categoryType = selected.categoryType,
            gameModeName = selected.gameModeName,
            gameModeId = selected.gameModeId,
            bonusMultiplier = bonusMultiplier,
            isCompleted = isCompleted
        )
    }
}
