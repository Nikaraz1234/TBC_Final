package com.example.mycomposeapp.feature.game.domain.constants

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import org.junit.Test
import kotlin.test.assertEquals

class GameXpCalculatorTest {

    // ==================== Score XP ====================

    @Test
    fun `calculate returns correct scoreXp for positive score`() {
        val result = createGameResult(totalScore = 100)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 100/10 = 10
        assertEquals(10, xp)
    }

    @Test
    fun `calculate returns correct scoreXp for large score`() {
        val result = createGameResult(totalScore = 1000)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 1000/10 = 100
        assertEquals(100, xp)
    }

    @Test
    fun `calculate returns zero scoreXp for zero score`() {
        val result = createGameResult(totalScore = 0)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate floors scoreXp for non-divisible score`() {
        val result = createGameResult(totalScore = 15)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 15/10 = 1 (integer division)
        assertEquals(1, xp)
    }

    @Test
    fun `calculate coerces negative score to zero scoreXp`() {
        val result = createGameResult(totalScore = -50)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = (-50/10).coerceAtLeast(0) = 0
        assertEquals(0, xp)
    }

    @Test
    fun `calculate returns zero scoreXp for score less than 10`() {
        val result = createGameResult(totalScore = 9)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 9/10 = 0
        assertEquals(0, xp)
    }

    // ==================== Correct Answers XP ====================

    @Test
    fun `calculate returns correct xp for correct answers`() {
        val result = createGameResult(correctAnswers = 5)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // correctXp = 5 * 5 = 25
        assertEquals(25, xp)
    }

    @Test
    fun `calculate returns zero xp for zero correct answers`() {
        val result = createGameResult(correctAnswers = 0)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate returns correct xp for one correct answer`() {
        val result = createGameResult(correctAnswers = 1)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // correctXp = 1 * 5 = 5
        assertEquals(5, xp)
    }

    @Test
    fun `calculate returns correct xp for many correct answers`() {
        val result = createGameResult(correctAnswers = 20)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // correctXp = 20 * 5 = 100
        assertEquals(100, xp)
    }

    // ==================== Streak XP ====================

    @Test
    fun `calculate returns correct xp for best streak`() {
        val result = createGameResult(bestStreak = 5)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // streakXp = 5 * 2 = 10
        assertEquals(10, xp)
    }

    @Test
    fun `calculate returns zero xp for zero streak`() {
        val result = createGameResult(bestStreak = 0)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate returns correct xp for one streak`() {
        val result = createGameResult(bestStreak = 1)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // streakXp = 1 * 2 = 2
        assertEquals(2, xp)
    }

    @Test
    fun `calculate returns correct xp for large streak`() {
        val result = createGameResult(bestStreak = 50)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // streakXp = 50 * 2 = 100
        assertEquals(100, xp)
    }

    // ==================== Daily Mode Bonus ====================

    @Test
    fun `calculate adds daily bonus when isDailyMode is true`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = true)

        // dailyBonus = 20
        assertEquals(20, xp)
    }

    @Test
    fun `calculate does not add daily bonus when isDailyMode is false`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    // ==================== Cover Mode Bonus ====================

    @Test
    fun `calculate adds cover mode bonus when gameModeId is COVER`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, GameModeIds.COVER, isDailyMode = false)

        // modeBonus = 10
        assertEquals(10, xp)
    }

    @Test
    fun `calculate does not add mode bonus for non-cover mode`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "quiz", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate does not add mode bonus for emoji mode`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, GameModeIds.EMOJI, isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate does not add mode bonus for plot mode`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, GameModeIds.PLOT, isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate does not add mode bonus for empty gameModeId`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "", isDailyMode = false)

        assertEquals(0, xp)
    }

    // ==================== Combined Calculations ====================

    @Test
    fun `calculate combines all xp components correctly`() {
        val result = createGameResult(
            totalScore = 200,
            correctAnswers = 10,
            bestStreak = 5
        )

        val xp = GameXpCalculator.calculate(result, GameModeIds.COVER, isDailyMode = true)

        // scoreXp = 200/10 = 20
        // correctXp = 10 * 5 = 50
        // streakXp = 5 * 2 = 10
        // dailyBonus = 20
        // modeBonus = 10
        // total = 20 + 50 + 10 + 20 + 10 = 110
        assertEquals(110, xp)
    }

    @Test
    fun `calculate combines score and correct answers`() {
        val result = createGameResult(totalScore = 100, correctAnswers = 5)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 10, correctXp = 25
        assertEquals(35, xp)
    }

    @Test
    fun `calculate combines all without bonuses`() {
        val result = createGameResult(
            totalScore = 100,
            correctAnswers = 5,
            bestStreak = 3
        )

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        // scoreXp = 10, correctXp = 25, streakXp = 6
        assertEquals(41, xp)
    }

    @Test
    fun `calculate combines daily and cover bonuses`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, GameModeIds.COVER, isDailyMode = true)

        // dailyBonus = 20, modeBonus = 10
        assertEquals(30, xp)
    }

    // ==================== Final Coerce ====================

    @Test
    fun `calculate never returns negative value`() {
        val result = createGameResult(totalScore = -1000)

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate returns zero for all zero inputs`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "other", isDailyMode = false)

        assertEquals(0, xp)
    }

    // ==================== Realistic Scenarios ====================

    @Test
    fun `calculate returns correct xp for typical easy game`() {
        val result = createGameResult(
            totalScore = 50,
            correctAnswers = 3,
            bestStreak = 2
        )

        val xp = GameXpCalculator.calculate(result, "quiz", isDailyMode = false)

        // scoreXp = 5, correctXp = 15, streakXp = 4
        assertEquals(24, xp)
    }

    @Test
    fun `calculate returns correct xp for perfect daily cover game`() {
        val result = createGameResult(
            totalScore = 500,
            correctAnswers = 10,
            bestStreak = 10
        )

        val xp = GameXpCalculator.calculate(result, GameModeIds.COVER, isDailyMode = true)

        // scoreXp = 50, correctXp = 50, streakXp = 20, dailyBonus = 20, modeBonus = 10
        assertEquals(150, xp)
    }

    @Test
    fun `calculate returns correct xp for worst possible game`() {
        val result = createGameResult()

        val xp = GameXpCalculator.calculate(result, "quiz", isDailyMode = false)

        assertEquals(0, xp)
    }

    @Test
    fun `calculate returns correct xp for game screenshot mode`() {
        val result = createGameResult(totalScore = 100, correctAnswers = 5, bestStreak = 3)

        val xp = GameXpCalculator.calculate(result, GameModeIds.GAME_SCREENSHOT, isDailyMode = false)

        // scoreXp = 10, correctXp = 25, streakXp = 6, no bonus
        assertEquals(41, xp)
    }

    @Test
    fun `calculate returns correct xp for book synopsis mode`() {
        val result = createGameResult(totalScore = 100, correctAnswers = 5, bestStreak = 3)

        val xp = GameXpCalculator.calculate(result, GameModeIds.BOOK_SYNOPSIS, isDailyMode = false)

        // scoreXp = 10, correctXp = 25, streakXp = 6, no bonus
        assertEquals(41, xp)
    }

    // ==================== Helper ====================

    private fun createGameResult(
        totalQuestions: Int = 10,
        correctAnswers: Int = 0,
        totalScore: Int = 0,
        timeTakenSeconds: Int = 60,
        bestStreak: Int = 0,
        answers: List<com.example.mycomposeapp.feature.game.domain.model.AnswerResult> = emptyList(),
        isNewHighScore: Boolean = false,
        coinsEarned: Int = 0,
        finalCoinBalance: Int = 0
    ): GameResult = GameResult(
        totalQuestions = totalQuestions,
        correctAnswers = correctAnswers,
        totalScore = totalScore,
        timeTakenSeconds = timeTakenSeconds,
        bestStreak = bestStreak,
        answers = answers,
        isNewHighScore = isNewHighScore,
        coinsEarned = coinsEarned,
        finalCoinBalance = finalCoinBalance
    )
}