package com.example.mycomposeapp.feature.game.domain.usecase.scoring

import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.rules.LevelingRules
import com.example.mycomposeapp.feature.game.domain.model.GameResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UpdateGameStatsUseCaseTest {

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: UpdateGameStatsUseCase

    @Before
    fun setUp() {
        userRepository = mockk(relaxUnitFun = true)
        useCase = UpdateGameStatsUseCase(userRepository)
    }

    // ==================== User Not Found ====================

    @Test
    fun `invoke returns null when user is null`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } returns flowOf(null)

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke returns null when user flow is empty`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } returns emptyFlow()

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke does not update stats when user is null`() = runTest {
        // Given
        coEvery { userRepository.getCurrentUser() } returns flowOf(null)

        // When
        useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        coVerify(exactly = 0) { userRepository.updateUserStats(any()) }
    }

    // ==================== Games Played ====================

    @Test
    fun `invoke increments games played by one`() = runTest {
        // Given
        givenUser(stats = UserStats(gamesPlayed = 10))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(11, result?.gamesPlayed)
    }

    @Test
    fun `invoke increments games played from zero`() = runTest {
        // Given
        givenUser(stats = UserStats(gamesPlayed = 0))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(1, result?.gamesPlayed)
    }

    // ==================== Correct Answers ====================

    @Test
    fun `invoke adds correct answers to total`() = runTest {
        // Given
        givenUser(stats = UserStats(correctAnswers = 50))

        // When
        val result = useCase(
            result = createGameResult(correctAnswers = 7),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(57, result?.correctAnswers)
    }

    @Test
    fun `invoke handles zero correct answers`() = runTest {
        // Given
        givenUser(stats = UserStats(correctAnswers = 50))

        // When
        val result = useCase(
            result = createGameResult(correctAnswers = 0),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(50, result?.correctAnswers)
    }

    // ==================== Points ====================

    @Test
    fun `invoke adds score to total points`() = runTest {
        // Given
        givenUser(stats = UserStats(points = 1000))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 150),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(1150, result?.points)
    }

    @Test
    fun `invoke handles zero score`() = runTest {
        // Given
        givenUser(stats = UserStats(points = 1000))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 0),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(1000, result?.points)
    }

    // ==================== High Score ====================

    @Test
    fun `invoke updates high score when new score is higher`() = runTest {
        // Given
        val statsKey = "movies_quiz"
        givenUser(stats = UserStats(highScore = mapOf(statsKey to 100)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 150),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(150, result?.highScore?.get(statsKey))
    }

    @Test
    fun `invoke does not update high score when new score is lower`() = runTest {
        // Given
        val statsKey = "movies_quiz"
        givenUser(stats = UserStats(highScore = mapOf(statsKey to 200)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 150),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(200, result?.highScore?.get(statsKey))
    }

    @Test
    fun `invoke does not update high score when scores are equal`() = runTest {
        // Given
        val statsKey = "movies_quiz"
        givenUser(stats = UserStats(highScore = mapOf(statsKey to 150)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 150),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(150, result?.highScore?.get(statsKey))
    }

    @Test
    fun `invoke sets high score when no previous score exists`() = runTest {
        // Given
        givenUser(stats = UserStats(highScore = emptyMap()))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(100, result?.highScore?.get("movies_quiz"))
    }

    @Test
    fun `invoke preserves other high scores when updating one`() = runTest {
        // Given
        givenUser(stats = UserStats(highScore = mapOf("anime_emoji" to 200)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(200, result?.highScore?.get("anime_emoji"))
        assertEquals(100, result?.highScore?.get("movies_quiz"))
    }

    // ==================== Best Streak ====================

    @Test
    fun `invoke updates best streak when result streak is higher`() = runTest {
        // Given
        givenUser(stats = UserStats(bestStreak = 5))

        // When
        val result = useCase(
            result = createGameResult(bestStreak = 8),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(8, result?.bestStreak)
    }

    @Test
    fun `invoke keeps best streak when result streak is lower`() = runTest {
        // Given
        givenUser(stats = UserStats(bestStreak = 10))

        // When
        val result = useCase(
            result = createGameResult(bestStreak = 5),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(10, result?.bestStreak)
    }

    @Test
    fun `invoke keeps best streak when streaks are equal`() = runTest {
        // Given
        givenUser(stats = UserStats(bestStreak = 5))

        // When
        val result = useCase(
            result = createGameResult(bestStreak = 5),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(5, result?.bestStreak)
    }

    // ==================== Daily Mode Streak (Non-Emoji) ====================

    @Test
    fun `invoke increments current streak for daily mode`() = runTest {
        // Given
        val statsKey = "movies_quiz"
        givenUser(stats = UserStats(currentStreak = mapOf(statsKey to 3)))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = true
        )

        // Then
        assertEquals(4, result?.currentStreak?.get(statsKey))
    }

    @Test
    fun `invoke starts current streak at 1 for new daily mode`() = runTest {
        // Given
        givenUser(stats = UserStats(currentStreak = emptyMap()))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = true
        )

        // Then
        assertEquals(1, result?.currentStreak?.get("movies_quiz"))
    }

    @Test
    fun `invoke does not update current streak for non-daily mode`() = runTest {
        // Given
        givenUser(stats = UserStats(currentStreak = mapOf("movies_quiz" to 3)))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(3, result?.currentStreak?.get("movies_quiz"))
    }

    // ==================== Daily Emoji Mode Streak ====================

    @Test
    fun `invoke increments emoji streak when played consecutively`() = runTest {
        // Given
        val yesterday = LocalDate.now().minusDays(1).toString()
        val statsKey = "anime_emoji"
        givenUser(
            stats = UserStats(
                currentStreak = mapOf(statsKey to 5),
                lastEmojiDate = yesterday
            )
        )

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertEquals(6, result?.currentStreak?.get(statsKey))
        assertEquals(LocalDate.now().toString(), result?.lastEmojiDate)
    }

    @Test
    fun `invoke resets emoji streak to 1 when not consecutive`() = runTest {
        // Given
        val twoDaysAgo = LocalDate.now().minusDays(2).toString()
        val statsKey = "anime_emoji"
        givenUser(
            stats = UserStats(
                currentStreak = mapOf(statsKey to 5),
                lastEmojiDate = twoDaysAgo
            )
        )

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertEquals(1, result?.currentStreak?.get(statsKey))
    }

    @Test
    fun `invoke resets emoji streak to 1 when lastEmojiDate is empty`() = runTest {
        // Given
        givenUser(
            stats = UserStats(
                currentStreak = emptyMap(),
                lastEmojiDate = ""
            )
        )

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertEquals(1, result?.currentStreak?.get("anime_emoji"))
    }

    @Test
    fun `invoke resets emoji streak to 1 when lastEmojiDate is invalid`() = runTest {
        // Given
        givenUser(
            stats = UserStats(
                currentStreak = mapOf("anime_emoji" to 5),
                lastEmojiDate = "invalid-date"
            )
        )

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertEquals(1, result?.currentStreak?.get("anime_emoji"))
    }

    @Test
    fun `invoke updates lastEmojiDate for daily emoji mode`() = runTest {
        // Given
        givenUser(stats = UserStats(lastEmojiDate = ""))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertEquals(LocalDate.now().toString(), result?.lastEmojiDate)
    }

    @Test
    fun `invoke does not update lastEmojiDate for non-emoji daily mode`() = runTest {
        // Given
        givenUser(stats = UserStats(lastEmojiDate = "2024-01-01"))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = true
        )

        // Then
        assertEquals("2024-01-01", result?.lastEmojiDate)
    }

    @Test
    fun `invoke does not update lastEmojiDate for non-daily emoji mode`() = runTest {
        // Given
        givenUser(stats = UserStats(lastEmojiDate = "2024-01-01"))

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = false
        )

        // Then
        assertEquals("2024-01-01", result?.lastEmojiDate)
    }

    // ==================== Coins ====================

    @Test
    fun `invoke updates coins for cover mode`() = runTest {
        // Given
        givenUser(stats = UserStats(coins = 100))

        // When
        val result = useCase(
            result = createGameResult(finalCoinBalance = 150),
            gameModeId = GameModeIds.COVER,
            categoryType = "anime",
            isDailyMode = false
        )

        // Then
        assertEquals(150, result?.coins)
    }

    @Test
    fun `invoke keeps current coins for non-cover mode`() = runTest {
        // Given
        givenUser(stats = UserStats(coins = 100))

        // When
        val result = useCase(
            result = createGameResult(finalCoinBalance = 200),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(100, result?.coins)
    }

    @Test
    fun `invoke keeps current coins for emoji mode`() = runTest {
        // Given
        givenUser(stats = UserStats(coins = 100))

        // When
        val result = useCase(
            result = createGameResult(finalCoinBalance = 200),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = false
        )

        // Then
        assertEquals(100, result?.coins)
    }

    // ==================== XP and Level ====================

    @Test
    fun `invoke adds xp to total`() = runTest {
        // Given
        givenUser(stats = UserStats(totalXp = 100))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100, correctAnswers = 5, bestStreak = 3),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        // XP = 100/10 + 5*5 + 3*2 = 10 + 25 + 6 = 41
        assertEquals(141, result?.totalXp)
    }

    @Test
    fun `invoke adds daily bonus xp`() = runTest {
        // Given
        givenUser(stats = UserStats(totalXp = 100))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 0, correctAnswers = 0, bestStreak = 0),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = true
        )

        // Then
        // XP = daily bonus 20
        assertEquals(120, result?.totalXp)
    }

    @Test
    fun `invoke adds cover mode bonus xp`() = runTest {
        // Given
        givenUser(stats = UserStats(totalXp = 100))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 0, correctAnswers = 0, bestStreak = 0),
            gameModeId = GameModeIds.COVER,
            categoryType = "anime",
            isDailyMode = false
        )

        // Then
        // XP = cover bonus 10
        assertEquals(110, result?.totalXp)
    }

    @Test
    fun `invoke calculates correct level after xp gain`() = runTest {
        // Given - 450 XP, will gain 50+ XP to reach level 2
        givenUser(stats = UserStats(totalXp = 450, level = 1))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100, correctAnswers = 10, bestStreak = 5),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        // XP = 10 + 50 + 10 = 70
        // Total = 520, Level 2 at 500 XP
        assertEquals(520, result?.totalXp)
        assertEquals(2, result?.level)
    }

    @Test
    fun `invoke keeps level when xp threshold not reached`() = runTest {
        // Given
        givenUser(stats = UserStats(totalXp = 100, level = 1))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 50, correctAnswers = 2, bestStreak = 1),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        // XP = 5 + 10 + 2 = 17
        // Total = 117, still level 1
        assertEquals(117, result?.totalXp)
        assertEquals(1, result?.level)
    }

    // ==================== Repository Interaction ====================

    @Test
    fun `invoke calls updateUserStats with correct stats`() = runTest {
        // Given
        givenUser(stats = UserStats())
        val statsSlot = slot<UserStats>()
        coEvery { userRepository.updateUserStats(capture(statsSlot)) } returns Unit

        // When
        useCase(
            result = createGameResult(totalScore = 100, correctAnswers = 5),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        coVerify(exactly = 1) { userRepository.updateUserStats(any()) }
        assertEquals(1, statsSlot.captured.gamesPlayed)
        assertEquals(5, statsSlot.captured.correctAnswers)
        assertEquals(100, statsSlot.captured.points)
    }

    @Test
    fun `invoke returns the updated stats`() = runTest {
        // Given
        givenUser(stats = UserStats(gamesPlayed = 5, points = 500))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100, correctAnswers = 3),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertNotNull(result)
        assertEquals(6, result.gamesPlayed)
        assertEquals(600, result.points)
        assertEquals(3, result.correctAnswers)
    }

    // ==================== Stats Key Generation ====================

    @Test
    fun `invoke uses correct stats key format`() = runTest {
        // Given
        givenUser(stats = UserStats())

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertTrue(result?.highScore?.containsKey("movies_quiz") == true)
    }

    @Test
    fun `invoke generates different keys for different categories`() = runTest {
        // Given
        givenUser(stats = UserStats(highScore = mapOf("anime_quiz" to 50)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(50, result?.highScore?.get("anime_quiz"))
        assertEquals(100, result?.highScore?.get("movies_quiz"))
    }

    @Test
    fun `invoke generates different keys for different game modes`() = runTest {
        // Given
        givenUser(stats = UserStats(highScore = mapOf("movies_emoji" to 50)))

        // When
        val result = useCase(
            result = createGameResult(totalScore = 100),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false
        )

        // Then
        assertEquals(50, result?.highScore?.get("movies_emoji"))
        assertEquals(100, result?.highScore?.get("movies_quiz"))
    }

    // ==================== isDailyChallengeMode (unused but covered) ====================

    @Test
    fun `invoke accepts isDailyChallengeMode parameter`() = runTest {
        // Given
        givenUser(stats = UserStats())

        // When
        val result = useCase(
            result = createGameResult(),
            gameModeId = "quiz",
            categoryType = "movies",
            isDailyMode = false,
            isDailyChallengeMode = true
        )

        // Then
        assertNotNull(result)
    }

    // ==================== Complex Scenarios ====================

    @Test
    fun `invoke handles full daily emoji game correctly`() = runTest {
        // Given
        val yesterday = LocalDate.now().minusDays(1).toString()
        givenUser(
            stats = UserStats(
                gamesPlayed = 10,
                correctAnswers = 50,
                points = 1000,
                bestStreak = 5,
                currentStreak = mapOf("anime_emoji" to 3),
                highScore = mapOf("anime_emoji" to 80),
                totalXp = 400,
                level = 1,
                lastEmojiDate = yesterday
            )
        )

        // When
        val result = useCase(
            result = createGameResult(
                totalScore = 100,
                correctAnswers = 8,
                bestStreak = 8
            ),
            gameModeId = GameModeIds.EMOJI,
            categoryType = "anime",
            isDailyMode = true
        )

        // Then
        assertNotNull(result)
        assertEquals(11, result.gamesPlayed)
        assertEquals(58, result.correctAnswers)
        assertEquals(1100, result.points)
        assertEquals(8, result.bestStreak)
        assertEquals(4, result.currentStreak["anime_emoji"])
        assertEquals(100, result.highScore["anime_emoji"])
        assertEquals(LocalDate.now().toString(), result.lastEmojiDate)
        // XP = 10 + 40 + 16 + 20 (daily) = 86
        assertEquals(486, result.totalXp)
    }

    @Test
    fun `invoke handles full cover game correctly`() = runTest {
        // Given
        givenUser(
            stats = UserStats(
                gamesPlayed = 5,
                coins = 100,
                totalXp = 200
            )
        )

        // When
        val result = useCase(
            result = createGameResult(
                totalScore = 150,
                correctAnswers = 10,
                bestStreak = 5,
                finalCoinBalance = 250
            ),
            gameModeId = GameModeIds.COVER,
            categoryType = "anime",
            isDailyMode = false
        )

        // Then
        assertNotNull(result)
        assertEquals(6, result.gamesPlayed)
        assertEquals(250, result.coins)
        // XP = 15 + 50 + 10 + 10 (cover) = 85
        assertEquals(285, result.totalXp)
    }

    // ==================== Helpers ====================

    private fun givenUser(stats: UserStats = UserStats()) {
        coEvery { userRepository.getCurrentUser() } returns flowOf(User(stats = stats))
    }

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