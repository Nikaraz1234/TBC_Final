package com.example.mycomposeapp.core.domain.usecase.daily

import com.example.mycomposeapp.core.domain.constants.DailyGoalsConstants
import com.example.mycomposeapp.core.domain.model.DailyGoalRawProgress
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.repository.DailyGoalRepository
import com.example.mycomposeapp.core.domain.repository.UserRepository
import com.example.mycomposeapp.core.domain.rules.LevelingRules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.todayIn
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdateDailyGoalProgressUseCaseTest {

    private lateinit var repository: DailyGoalRepository
    private lateinit var userRepository: UserRepository
    private lateinit var useCase: UpdateDailyGoalProgressUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxUnitFun = true)
        userRepository = mockk(relaxUnitFun = true)
        useCase = UpdateDailyGoalProgressUseCase(repository, userRepository)
    }

    // ==================== Reset & Basic Flow ====================

    @Test
    fun `recordGamePlayed calls resetIfNewDay first`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 1) { repository.resetIfNewDay() }
    }

    @Test
    fun `recordGamePlayed reads current progress`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 1) { repository.getProgress() }
    }

    @Test
    fun `recordGamePlayed saves updated progress`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 1) { repository.saveProgress(any()) }
    }

    // ==================== No Goals Completed ====================

    @Test
    fun `recordGamePlayed returns no completed goals when targets not met`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertTrue(result.newlyCompletedGoalIds.isEmpty())
        assertEquals(0, result.xpAwarded)
        assertFalse(result.allGoalsCompleted)
        assertEquals(0, result.bonusXpAwarded)
    }

    @Test
    fun `recordGamePlayed does not award xp when no goals completed`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 0) { userRepository.updateUserStats(any()) }
    }

    // ==================== Games Played Goal ====================

    @Test
    fun `recordGamePlayed completes games_played goal at target`() = runTest {
        // Given - 2 games played, target is 3
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertTrue(result.newlyCompletedGoalIds.contains("games_played"))
    }

    @Test
    fun `recordGamePlayed does not complete games_played when already completed`() = runTest {
        // Given - already at target
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET
        )
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("games_played"))
    }

    @Test
    fun `recordGamePlayed does not complete games_played when below target`() = runTest {
        // Given - 0 games, target is 3, after recording will be 1
        givenProgress(gamesPlayed = 0)
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("games_played"))
    }

    // ==================== Perfect Score Goal ====================

    @Test
    fun `recordGamePlayed completes perfect_score goal at target`() = runTest {
        // Given - 0 perfect scores, target is 1
        givenProgress(
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = true)

        // Then
        assertTrue(result.newlyCompletedGoalIds.contains("perfect_score"))
    }

    @Test
    fun `recordGamePlayed does not complete perfect_score when not perfect`() = runTest {
        // Given
        givenProgress(perfectScores = 0)
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("perfect_score"))
    }

    @Test
    fun `recordGamePlayed does not complete perfect_score when already completed`() = runTest {
        // Given - already at target
        givenProgress(
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET
        )
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = true)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("perfect_score"))
    }

    // ==================== Categories Tried Goal ====================

    @Test
    fun `recordGamePlayed completes categories_tried goal at target`() = runTest {
        // Given - 1 category tried, target is 2
        val existingCategories = (1 until DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Category$it" }
            .toSet()
        givenProgress(categoriesTried = existingCategories)
        givenUser()

        // When - adding a new category
        val result = useCase.recordGamePlayed("NewCategory", "quiz", wasPerfect = false)

        // Then
        assertTrue(result.newlyCompletedGoalIds.contains("categories_tried"))
    }

    @Test
    fun `recordGamePlayed does not complete categories_tried when duplicate category`() = runTest {
        // Given - 1 category tried
        givenProgress(categoriesTried = setOf("Sports"))
        givenNoUser()

        // When - same category again
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("categories_tried"))
    }

    @Test
    fun `recordGamePlayed does not complete categories_tried when already completed`() = runTest {
        // Given - already at target
        val categories = (1..DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Category$it" }
            .toSet()
        givenProgress(categoriesTried = categories)
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("AnotherCategory", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("categories_tried"))
    }

    // ==================== Game Modes Tried Goal ====================

    @Test
    fun `recordGamePlayed completes game_modes_tried goal at target`() = runTest {
        // Given - 3 game modes tried, target is 4
        val existingModes = (1 until DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(gameModesTried = existingModes)
        givenUser()

        // When - adding a new game mode
        val result = useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = false)

        // Then
        assertTrue(result.newlyCompletedGoalIds.contains("game_modes_tried"))
    }

    @Test
    fun `recordGamePlayed does not complete game_modes_tried when duplicate mode`() = runTest {
        // Given
        givenProgress(gameModesTried = setOf("Sports_quiz"))
        givenNoUser()

        // When - same mode again
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("game_modes_tried"))
    }

    @Test
    fun `recordGamePlayed does not complete game_modes_tried when already completed`() = runTest {
        // Given - already at target
        val modes = (1..DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(gameModesTried = modes)
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = false)

        // Then
        assertFalse(result.newlyCompletedGoalIds.contains("game_modes_tried"))
    }

    // ==================== XP Awarded ====================

    @Test
    fun `recordGamePlayed awards correct xp for one completed goal`() = runTest {
        // Given - games played about to complete
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals(DailyGoalsConstants.GOAL_COMPLETION_XP, result.xpAwarded)
    }

    @Test
    fun `recordGamePlayed awards correct xp for two completed goals`() = runTest {
        // Given - games played and perfect score about to complete
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = true)

        // Then
        assertEquals(DailyGoalsConstants.GOAL_COMPLETION_XP * 2, result.xpAwarded)
    }

    @Test
    fun `recordGamePlayed awards zero xp when no goals completed`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals(0, result.xpAwarded)
    }

    // ==================== All Goals Completed Bonus ====================

    @Test
    fun `recordGamePlayed awards bonus when all goals completed`() = runTest {
        // Given - all goals about to complete with this game
        val categories = (1 until DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Cat$it" }
            .toSet()
        val modes = (1 until DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1,
            categoriesTried = categories,
            gameModesTried = modes
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = true)

        // Then
        assertTrue(result.allGoalsCompleted)
        assertEquals(DailyGoalsConstants.ALL_GOALS_BONUS_XP, result.bonusXpAwarded)
    }

    @Test
    fun `recordGamePlayed does not award bonus when not all goals completed`() = runTest {
        // Given - only games played about to complete
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertFalse(result.allGoalsCompleted)
        assertEquals(0, result.bonusXpAwarded)
    }

    @Test
    fun `recordGamePlayed does not award bonus when already awarded today`() = runTest {
        // Given - all goals met but bonus already awarded today
        val categories = (1..DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Cat$it" }
            .toSet()
        val modes = (1..DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        val today = kotlinx.datetime.Clock.System
            .todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
            .toString()
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET,
            categoriesTried = categories,
            gameModesTried = modes,
            allCompletedDate = today
        )
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("AnotherCat", "anotherMode", wasPerfect = false)

        // Then
        assertFalse(result.allGoalsCompleted)
        assertEquals(0, result.bonusXpAwarded)
    }

    // ==================== Save Progress ====================

    @Test
    fun `recordGamePlayed saves progress with allCompletedDate when all goals completed`() = runTest {
        // Given
        val categories = (1 until DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Cat$it" }
            .toSet()
        val modes = (1 until DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1,
            categoriesTried = categories,
            gameModesTried = modes
        )
        givenUser()

        val savedProgress = slot<DailyGoalRawProgress>()
        coEvery { repository.saveProgress(capture(savedProgress)) } returns Unit

        // When
        useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = true)

        // Then
        val today = kotlinx.datetime.Clock.System
            .todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
            .toString()
        assertEquals(today, savedProgress.captured.allCompletedDate)
    }

    @Test
    fun `recordGamePlayed saves progress without allCompletedDate when not all goals completed`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        val savedProgress = slot<DailyGoalRawProgress>()
        coEvery { repository.saveProgress(capture(savedProgress)) } returns Unit

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals("", savedProgress.captured.allCompletedDate)
    }

    @Test
    fun `recordGamePlayed saves correct game count in progress`() = runTest {
        // Given
        givenProgress(gamesPlayed = 5)
        givenNoUser()

        val savedProgress = slot<DailyGoalRawProgress>()
        coEvery { repository.saveProgress(capture(savedProgress)) } returns Unit

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals(6, savedProgress.captured.gamesPlayed)
    }

    @Test
    fun `recordGamePlayed saves correct categories in progress`() = runTest {
        // Given
        givenProgress(categoriesTried = setOf("Sports"))
        givenNoUser()

        val savedProgress = slot<DailyGoalRawProgress>()
        coEvery { repository.saveProgress(capture(savedProgress)) } returns Unit

        // When
        useCase.recordGamePlayed("Music", "quiz", wasPerfect = false)

        // Then
        assertEquals(setOf("Sports", "Music"), savedProgress.captured.categoriesTried)
    }

    @Test
    fun `recordGamePlayed saves correct game modes in progress`() = runTest {
        // Given
        givenProgress(gameModesTried = setOf("Sports_quiz"))
        givenNoUser()

        val savedProgress = slot<DailyGoalRawProgress>()
        coEvery { repository.saveProgress(capture(savedProgress)) } returns Unit

        // When
        useCase.recordGamePlayed("Music", "guess", wasPerfect = false)

        // Then
        assertEquals(
            setOf("Sports_quiz", "Music_guess"),
            savedProgress.captured.gameModesTried
        )
    }

    // ==================== Award XP ====================

    @Test
    fun `recordGamePlayed awards xp to user when goals completed`() = runTest {
        // Given
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        val user = User(stats = UserStats(totalXp = 100, level = 1))
        coEvery { userRepository.getCurrentUser() } returns flowOf(user)

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        val expectedXp = 100 + DailyGoalsConstants.GOAL_COMPLETION_XP
        val expectedLevel = LevelingRules.calculateLevel(expectedXp)
        coVerify {
            userRepository.updateUserStats(
                UserStats(totalXp = expectedXp, level = expectedLevel)
            )
        }
    }

    @Test
    fun `recordGamePlayed does not award xp when user is null`() = runTest {
        // Given
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        coEvery { userRepository.getCurrentUser() } returns flowOf(null)

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 0) { userRepository.updateUserStats(any()) }
    }

    @Test
    fun `recordGamePlayed does not award xp when user flow is empty`() = runTest {
        // Given
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        coEvery { userRepository.getCurrentUser() } returns emptyFlow()

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        coVerify(exactly = 0) { userRepository.updateUserStats(any()) }
    }

    @Test
    fun `recordGamePlayed calculates correct level after xp award`() = runTest {
        // Given - user at 450 XP, about to get 50 XP = 500 XP = level 2
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        val user = User(stats = UserStats(totalXp = 450, level = 1))
        coEvery { userRepository.getCurrentUser() } returns flowOf(user)

        val statsSlot = slot<UserStats>()
        coEvery { userRepository.updateUserStats(capture(statsSlot)) } returns Unit

        // When
        useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals(500, statsSlot.captured.totalXp)
        assertEquals(2, statsSlot.captured.level)
    }

    @Test
    fun `recordGamePlayed awards combined goal and bonus xp`() = runTest {
        // Given - all goals about to complete
        val categories = (1 until DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Cat$it" }
            .toSet()
        val modes = (1 until DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1,
            categoriesTried = categories,
            gameModesTried = modes
        )
        val user = User(stats = UserStats(totalXp = 0, level = 1))
        coEvery { userRepository.getCurrentUser() } returns flowOf(user)

        val statsSlot = slot<UserStats>()
        coEvery { userRepository.updateUserStats(capture(statsSlot)) } returns Unit

        // When
        val result = useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = true)

        // Then
        val expectedGoalXp = 4 * DailyGoalsConstants.GOAL_COMPLETION_XP  // 4 goals completed
        val expectedTotalXp = expectedGoalXp + DailyGoalsConstants.ALL_GOALS_BONUS_XP
        assertEquals(expectedTotalXp, statsSlot.captured.totalXp)
    }

    // ==================== Return Value ====================

    @Test
    fun `recordGamePlayed returns correct result for single goal completion`() = runTest {
        // Given
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertEquals(listOf("games_played"), result.newlyCompletedGoalIds)
        assertEquals(DailyGoalsConstants.GOAL_COMPLETION_XP, result.xpAwarded)
        assertFalse(result.allGoalsCompleted)
        assertEquals(0, result.bonusXpAwarded)
    }

    @Test
    fun `recordGamePlayed returns correct result for all goals completion`() = runTest {
        // Given
        val categories = (1 until DailyGoalsConstants.CATEGORIES_TO_TRY_TARGET)
            .map { "Cat$it" }
            .toSet()
        val modes = (1 until DailyGoalsConstants.GAME_MODES_TO_TRY_TARGET)
            .map { "Cat${it}_mode${it}" }
            .toSet()
        givenProgress(
            gamesPlayed = DailyGoalsConstants.GAMES_TO_PLAY_TARGET - 1,
            perfectScores = DailyGoalsConstants.PERFECT_SCORES_TARGET - 1,
            categoriesTried = categories,
            gameModesTried = modes
        )
        givenUser()

        // When
        val result = useCase.recordGamePlayed("NewCat", "newMode", wasPerfect = true)

        // Then
        assertEquals(4, result.newlyCompletedGoalIds.size)
        assertTrue(result.newlyCompletedGoalIds.contains("games_played"))
        assertTrue(result.newlyCompletedGoalIds.contains("perfect_score"))
        assertTrue(result.newlyCompletedGoalIds.contains("categories_tried"))
        assertTrue(result.newlyCompletedGoalIds.contains("game_modes_tried"))
        assertEquals(4 * DailyGoalsConstants.GOAL_COMPLETION_XP, result.xpAwarded)
        assertTrue(result.allGoalsCompleted)
        assertEquals(DailyGoalsConstants.ALL_GOALS_BONUS_XP, result.bonusXpAwarded)
    }

    @Test
    fun `recordGamePlayed returns correct result when no goals completed`() = runTest {
        // Given
        givenEmptyProgress()
        givenNoUser()

        // When
        val result = useCase.recordGamePlayed("Sports", "quiz", wasPerfect = false)

        // Then
        assertTrue(result.newlyCompletedGoalIds.isEmpty())
        assertEquals(0, result.xpAwarded)
        assertFalse(result.allGoalsCompleted)
        assertEquals(0, result.bonusXpAwarded)
    }

    // ==================== Helpers ====================

    private fun givenEmptyProgress() {
        coEvery { repository.getProgress() } returns DailyGoalRawProgress()
    }

    private fun givenProgress(
        gamesPlayed: Int = 0,
        perfectScores: Int = 0,
        categoriesTried: Set<String> = emptySet(),
        gameModesTried: Set<String> = emptySet(),
        allCompletedDate: String = ""
    ) {
        coEvery { repository.getProgress() } returns DailyGoalRawProgress(
            gamesPlayed = gamesPlayed,
            perfectScores = perfectScores,
            categoriesTried = categoriesTried,
            gameModesTried = gameModesTried,
            allCompletedDate = allCompletedDate
        )
    }

    private fun givenUser(
        totalXp: Int = 0,
        level: Int = 1
    ) {
        coEvery { userRepository.getCurrentUser() } returns flowOf(
            User(stats = UserStats(totalXp = totalXp, level = level))
        )
    }

    private fun givenNoUser() {
        coEvery { userRepository.getCurrentUser() } returns flowOf(null)
    }
}