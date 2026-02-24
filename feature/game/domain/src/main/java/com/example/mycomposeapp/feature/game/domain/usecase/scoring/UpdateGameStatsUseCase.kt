package com.example.mycomposeapp.feature.game.domain.usecase.scoring

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.rules.LevelingRules
import com.example.mycomposeapp.feature.game.domain.constants.GameXpCalculator
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import javax.inject.Inject

class UpdateGameStatsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        result: GameResult,
        gameModeId: String,
        categoryType: String,
        isDailyMode: Boolean,
        isDailyChallengeMode: Boolean = false
    ): UserStats? {
        val user = userRepository.getCurrentUser().firstOrNull() ?: return null
        val currentStats = user.stats

        val statsKey = GameModeIds.statsKey(categoryType, gameModeId)

        val currentHigh = currentStats.highScore[statsKey] ?: 0
        val updatedHighScore = if (result.totalScore > currentHigh) {
            currentStats.highScore + (statsKey to result.totalScore)
        } else {
            currentStats.highScore
        }

        var updatedCurrentStreak = currentStats.currentStreak
        var updatedLastEmojiDate = currentStats.lastEmojiDate

        if (isDailyMode && gameModeId == GameModeIds.EMOJI) {
            val today = LocalDate.now()
            val lastDate = currentStats.lastEmojiDate.takeIf { it.isNotEmpty() }?.let {
                try { LocalDate.parse(it) } catch (_: Exception) { null }
            }
            val currentModeStreak = updatedCurrentStreak[statsKey] ?: 0
            val newStreak = if (lastDate != null && lastDate == today.minusDays(1)) {
                currentModeStreak + 1
            } else {
                1
            }
            updatedCurrentStreak = updatedCurrentStreak + (statsKey to newStreak)
            updatedLastEmojiDate = today.toString()
        } else if (isDailyMode) {
            val currentModeStreak = updatedCurrentStreak[statsKey] ?: 0
            updatedCurrentStreak = updatedCurrentStreak + (statsKey to currentModeStreak + 1)
        }

        val updatedBestStreak = maxOf(currentStats.bestStreak, result.bestStreak)
        val newCoins = if (gameModeId == GameModeIds.COVER) result.finalCoinBalance else currentStats.coins

        val xpGain = GameXpCalculator.calculate(result, gameModeId, isDailyMode)
        val newTotalXp = currentStats.totalXp + xpGain
        val newLevel = LevelingRules.calculateLevel(newTotalXp)

        val updatedStats = currentStats.copy(
            gamesPlayed = currentStats.gamesPlayed + 1,
            correctAnswers = currentStats.correctAnswers + result.correctAnswers,
            points = currentStats.points + result.totalScore,
            bestStreak = updatedBestStreak,
            currentStreak = updatedCurrentStreak,
            highScore = updatedHighScore,
            coins = newCoins,
            lastEmojiDate = updatedLastEmojiDate,
            totalXp = newTotalXp,
            level = newLevel
        )

        userRepository.updateUserStats(updatedStats)
        return updatedStats
    }
}
