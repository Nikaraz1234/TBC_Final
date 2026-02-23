package com.example.mycomposeapp.feature.game.domain.constants

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.domain.model.GameResult

object GameXpCalculator {
    fun calculate(result: GameResult, gameModeId: String, isDailyMode: Boolean): Int {
        val scoreXp = (result.totalScore / 10).coerceAtLeast(0)
        val correctXp = result.correctAnswers * 5
        val streakXp = result.bestStreak * 2
        val dailyBonus = if (isDailyMode) 20 else 0
        val modeBonus = if (gameModeId == GameModeIds.COVER) 10 else 0
        return (scoreXp + correctXp + streakXp + dailyBonus + modeBonus).coerceAtLeast(0)
    }
}
