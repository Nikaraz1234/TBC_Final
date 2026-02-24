package com.example.mycomposeapp.feature.achievements.domain.usecase

import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementCategory
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementConditionType
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementConditionEvaluatorTest {

    // ==================== GAMES_PLAYED ====================

    @Test
    fun `isMet returns true when gamesPlayed exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 10
        )
        val stats = UserStats(gamesPlayed = 15)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when gamesPlayed equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 10
        )
        val stats = UserStats(gamesPlayed = 10)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when gamesPlayed below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 10
        )
        val stats = UserStats(gamesPlayed = 5)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when gamesPlayed is zero`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 1
        )
        val stats = UserStats(gamesPlayed = 0)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== CORRECT_ANSWERS ====================

    @Test
    fun `isMet returns true when correctAnswers exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.CORRECT_ANSWERS,
            conditionValue = 100
        )
        val stats = UserStats(correctAnswers = 150)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when correctAnswers equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.CORRECT_ANSWERS,
            conditionValue = 100
        )
        val stats = UserStats(correctAnswers = 100)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when correctAnswers below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.CORRECT_ANSWERS,
            conditionValue = 100
        )
        val stats = UserStats(correctAnswers = 50)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== BEST_STREAK ====================

    @Test
    fun `isMet returns true when bestStreak exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.BEST_STREAK,
            conditionValue = 5
        )
        val stats = UserStats(bestStreak = 10)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when bestStreak equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.BEST_STREAK,
            conditionValue = 5
        )
        val stats = UserStats(bestStreak = 5)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when bestStreak below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.BEST_STREAK,
            conditionValue = 10
        )
        val stats = UserStats(bestStreak = 5)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== TOTAL_XP ====================

    @Test
    fun `isMet returns true when totalXp exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.TOTAL_XP,
            conditionValue = 1000
        )
        val stats = UserStats(totalXp = 1500)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when totalXp equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.TOTAL_XP,
            conditionValue = 1000
        )
        val stats = UserStats(totalXp = 1000)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when totalXp below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.TOTAL_XP,
            conditionValue = 1000
        )
        val stats = UserStats(totalXp = 500)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== LEVEL_REACHED ====================

    @Test
    fun `isMet returns true when level exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.LEVEL_REACHED,
            conditionValue = 5
        )
        val stats = UserStats(level = 10)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when level equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.LEVEL_REACHED,
            conditionValue = 5
        )
        val stats = UserStats(level = 5)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when level below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.LEVEL_REACHED,
            conditionValue = 10
        )
        val stats = UserStats(level = 5)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== POINTS_SCORED ====================

    @Test
    fun `isMet returns true when points exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.POINTS_SCORED,
            conditionValue = 5000
        )
        val stats = UserStats(points = 7500)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when points equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.POINTS_SCORED,
            conditionValue = 5000
        )
        val stats = UserStats(points = 5000)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when points below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.POINTS_SCORED,
            conditionValue = 5000
        )
        val stats = UserStats(points = 2500)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== HIGH_SCORE ====================

    @Test
    fun `isMet returns true when highScore exceeds condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "anime_emoji"
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 150))

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when highScore equals condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "anime_emoji"
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 100))

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when highScore below condition`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "anime_emoji"
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 50))

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when conditionKey is null for HIGH_SCORE`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = null
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 150))

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when highScore key not found in stats`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "movies_quiz"
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 150))

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns false when highScore map is empty`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "anime_emoji"
        )
        val stats = UserStats(highScore = emptyMap())

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet returns true when highScore is zero and condition is zero`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 0,
            conditionKey = "anime_emoji"
        )
        val stats = UserStats(highScore = mapOf("anime_emoji" to 0))

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet checks correct key for HIGH_SCORE`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.HIGH_SCORE,
            conditionValue = 100,
            conditionKey = "movies_quiz"
        )
        val stats = UserStats(
            highScore = mapOf(
                "anime_emoji" to 500,
                "movies_quiz" to 50
            )
        )

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== Edge Cases ====================

    @Test
    fun `isMet handles zero condition value`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 0
        )
        val stats = UserStats(gamesPlayed = 0)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet handles large values`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.TOTAL_XP,
            conditionValue = 1_000_000
        )
        val stats = UserStats(totalXp = 1_500_000)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet handles one below threshold`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 100
        )
        val stats = UserStats(gamesPlayed = 99)

        assertFalse(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    @Test
    fun `isMet handles one above threshold`() {
        val achievement = createAchievement(
            conditionType = AchievementConditionType.GAMES_PLAYED,
            conditionValue = 100
        )
        val stats = UserStats(gamesPlayed = 101)

        assertTrue(AchievementConditionEvaluator.isMet(achievement, stats))
    }

    // ==================== Helper ====================

    private fun createAchievement(
        conditionType: AchievementConditionType,
        conditionValue: Int,
        conditionKey: String? = null
    ): AppAchievement = AppAchievement(
        id = "test_achievement",
        name = "Test Achievement",
        description = "Test description",
        icon = "icon_test",
        category = AchievementCategory.GENERAL,
        xpReward = 100,
        conditionType = conditionType,
        conditionValue = conditionValue,
        conditionKey = conditionKey
    )
}