package com.example.mycomposeapp.core.domain.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DailyGoalRawProgressTest {

    // ==================== Default Values ====================

    @Test
    fun `default progress has empty date`() {
        val progress = DailyGoalRawProgress()
        assertEquals("", progress.date)
    }

    @Test
    fun `default progress has zero games played`() {
        val progress = DailyGoalRawProgress()
        assertEquals(0, progress.gamesPlayed)
    }

    @Test
    fun `default progress has zero perfect scores`() {
        val progress = DailyGoalRawProgress()
        assertEquals(0, progress.perfectScores)
    }

    @Test
    fun `default progress has empty categories`() {
        val progress = DailyGoalRawProgress()
        assertTrue(progress.categoriesTried.isEmpty())
    }

    @Test
    fun `default progress has empty game modes`() {
        val progress = DailyGoalRawProgress()
        assertTrue(progress.gameModesTried.isEmpty())
    }

    @Test
    fun `default progress has empty allCompletedDate`() {
        val progress = DailyGoalRawProgress()
        assertEquals("", progress.allCompletedDate)
    }

    // ==================== recordGame - Games Played ====================

    @Test
    fun `recordGame increments games played by one`() {
        val progress = DailyGoalRawProgress(gamesPlayed = 0)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(1, result.gamesPlayed)
    }

    @Test
    fun `recordGame increments games played from existing count`() {
        val progress = DailyGoalRawProgress(gamesPlayed = 5)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(6, result.gamesPlayed)
    }

    @Test
    fun `recordGame increments games played on multiple calls`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = false)
            .recordGame("Music", "guess", wasPerfect = false)
            .recordGame("Movies", "trivia", wasPerfect = false)

        assertEquals(3, result.gamesPlayed)
    }

    // ==================== recordGame - Perfect Scores ====================

    @Test
    fun `recordGame increments perfect scores when wasPerfect is true`() {
        val progress = DailyGoalRawProgress(perfectScores = 0)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = true)

        assertEquals(1, result.perfectScores)
    }

    @Test
    fun `recordGame does not increment perfect scores when wasPerfect is false`() {
        val progress = DailyGoalRawProgress(perfectScores = 0)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(0, result.perfectScores)
    }

    @Test
    fun `recordGame increments perfect scores from existing count`() {
        val progress = DailyGoalRawProgress(perfectScores = 3)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = true)

        assertEquals(4, result.perfectScores)
    }

    @Test
    fun `recordGame keeps perfect scores unchanged when not perfect from existing count`() {
        val progress = DailyGoalRawProgress(perfectScores = 3)

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(3, result.perfectScores)
    }

    @Test
    fun `recordGame tracks perfect scores correctly over multiple games`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = true)
            .recordGame("Music", "guess", wasPerfect = false)
            .recordGame("Movies", "trivia", wasPerfect = true)

        assertEquals(2, result.perfectScores)
    }

    // ==================== recordGame - Categories Tried ====================

    @Test
    fun `recordGame adds new category`() {
        val progress = DailyGoalRawProgress()

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertTrue(result.categoriesTried.contains("Sports"))
        assertEquals(1, result.categoriesTried.size)
    }

    @Test
    fun `recordGame does not duplicate existing category`() {
        val progress = DailyGoalRawProgress(categoriesTried = setOf("Sports"))

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(1, result.categoriesTried.size)
        assertTrue(result.categoriesTried.contains("Sports"))
    }

    @Test
    fun `recordGame adds multiple different categories`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = false)
            .recordGame("Music", "guess", wasPerfect = false)
            .recordGame("Movies", "trivia", wasPerfect = false)

        assertEquals(3, result.categoriesTried.size)
        assertTrue(result.categoriesTried.containsAll(setOf("Sports", "Music", "Movies")))
    }

    @Test
    fun `recordGame preserves existing categories when adding new one`() {
        val progress = DailyGoalRawProgress(
            categoriesTried = setOf("Sports", "Music")
        )

        val result = progress.recordGame("Movies", "quiz", wasPerfect = false)

        assertEquals(3, result.categoriesTried.size)
        assertTrue(result.categoriesTried.containsAll(setOf("Sports", "Music", "Movies")))
    }

    @Test
    fun `recordGame with same category played multiple times only counts once`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = false)
            .recordGame("Sports", "guess", wasPerfect = false)
            .recordGame("Sports", "trivia", wasPerfect = false)

        assertEquals(1, result.categoriesTried.size)
    }

    // ==================== recordGame - Game Modes Tried ====================

    @Test
    fun `recordGame adds new game mode with correct format`() {
        val progress = DailyGoalRawProgress()

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertTrue(result.gameModesTried.contains("Sports_quiz"))
        assertEquals(1, result.gameModesTried.size)
    }

    @Test
    fun `recordGame does not duplicate existing game mode`() {
        val progress = DailyGoalRawProgress(
            gameModesTried = setOf("Sports_quiz")
        )

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals(1, result.gameModesTried.size)
    }

    @Test
    fun `recordGame adds different game modes for same category`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = false)
            .recordGame("Sports", "guess", wasPerfect = false)

        assertEquals(2, result.gameModesTried.size)
        assertTrue(result.gameModesTried.containsAll(setOf("Sports_quiz", "Sports_guess")))
    }

    @Test
    fun `recordGame adds different game modes for different categories`() {
        val progress = DailyGoalRawProgress()

        val result = progress
            .recordGame("Sports", "quiz", wasPerfect = false)
            .recordGame("Music", "quiz", wasPerfect = false)

        assertEquals(2, result.gameModesTried.size)
        assertTrue(result.gameModesTried.containsAll(setOf("Sports_quiz", "Music_quiz")))
    }

    @Test
    fun `recordGame preserves existing game modes when adding new one`() {
        val progress = DailyGoalRawProgress(
            gameModesTried = setOf("Sports_quiz", "Music_guess")
        )

        val result = progress.recordGame("Movies", "trivia", wasPerfect = false)

        assertEquals(3, result.gameModesTried.size)
        assertTrue(result.gameModesTried.containsAll(
            setOf("Sports_quiz", "Music_guess", "Movies_trivia")
        ))
    }

    // ==================== recordGame - Unchanged Fields ====================

    @Test
    fun `recordGame does not change date`() {
        val progress = DailyGoalRawProgress(date = "2024-01-15")

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals("2024-01-15", result.date)
    }

    @Test
    fun `recordGame does not change allCompletedDate`() {
        val progress = DailyGoalRawProgress(allCompletedDate = "2024-01-14")

        val result = progress.recordGame("Sports", "quiz", wasPerfect = false)

        assertEquals("2024-01-14", result.allCompletedDate)
    }

    // ==================== recordGame - Combined Behavior ====================

    @Test
    fun `recordGame updates all fields correctly for perfect game`() {
        val progress = DailyGoalRawProgress(
            date = "2024-01-15",
            gamesPlayed = 2,
            perfectScores = 1,
            categoriesTried = setOf("Sports"),
            gameModesTried = setOf("Sports_quiz"),
            allCompletedDate = ""
        )

        val result = progress.recordGame("Music", "guess", wasPerfect = true)

        assertEquals("2024-01-15", result.date)
        assertEquals(3, result.gamesPlayed)
        assertEquals(2, result.perfectScores)
        assertEquals(setOf("Sports", "Music"), result.categoriesTried)
        assertEquals(setOf("Sports_quiz", "Music_guess"), result.gameModesTried)
        assertEquals("", result.allCompletedDate)
    }

    @Test
    fun `recordGame updates all fields correctly for non-perfect game`() {
        val progress = DailyGoalRawProgress(
            date = "2024-01-15",
            gamesPlayed = 2,
            perfectScores = 1,
            categoriesTried = setOf("Sports"),
            gameModesTried = setOf("Sports_quiz"),
            allCompletedDate = ""
        )

        val result = progress.recordGame("Music", "guess", wasPerfect = false)

        assertEquals("2024-01-15", result.date)
        assertEquals(3, result.gamesPlayed)
        assertEquals(1, result.perfectScores)
        assertEquals(setOf("Sports", "Music"), result.categoriesTried)
        assertEquals(setOf("Sports_quiz", "Music_guess"), result.gameModesTried)
        assertEquals("", result.allCompletedDate)
    }

    @Test
    fun `recordGame returns new instance without modifying original`() {
        val original = DailyGoalRawProgress(
            gamesPlayed = 0,
            perfectScores = 0,
            categoriesTried = emptySet(),
            gameModesTried = emptySet()
        )

        val result = original.recordGame("Sports", "quiz", wasPerfect = true)

        // Original unchanged
        assertEquals(0, original.gamesPlayed)
        assertEquals(0, original.perfectScores)
        assertTrue(original.categoriesTried.isEmpty())
        assertTrue(original.gameModesTried.isEmpty())

        // Result updated
        assertEquals(1, result.gamesPlayed)
        assertEquals(1, result.perfectScores)
        assertFalse(result.categoriesTried.isEmpty())
        assertFalse(result.gameModesTried.isEmpty())
    }
}