@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.achievements.presentation

import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementCategory
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.feature.achievements.domain.model.CheckResult
import com.example.mycomposeapp.feature.achievements.domain.usecase.CheckAndUnlockAchievementsUseCase
import com.example.mycomposeapp.feature.achievements.domain.usecase.SeedAchievementsUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AchievementsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var checkAndUnlock: CheckAndUnlockAchievementsUseCase
    private lateinit var getCurrentUser: GetCurrentUserUseCase
    private lateinit var seedAchievements: SeedAchievementsUseCase

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        checkAndUnlock = mockk()
        getCurrentUser = mockk()
        seedAchievements = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun buildViewModel(
        user: User = mockk(relaxed = true),
        stats: UserStats = mockk(relaxed = true),
        result: CheckResult = mockk(relaxed = true)
    ): AchievementsViewModel {
        every { user.stats } returns stats

        every { getCurrentUser() } returns flowOf(user)

        coEvery { checkAndUnlock(stats) } returns result

        return AchievementsViewModel(
            checkAndUnlock = checkAndUnlock,
            getCurrentUser = getCurrentUser,
            seedAchievements = seedAchievements
        )
    }


    @Test
    fun `init loads achievements and updates state`() = runTest {
        val stats = mockk<UserStats>(relaxed = true)

        val result = mockk<CheckResult>()
        every { result.allAchievements } returns emptyList()
        every { result.unlockedIds } returns emptyList<String>()
        every { result.newlyUnlocked } returns emptyList()

        val vm = buildViewModel(stats = stats, result = result)

        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.achievements.isEmpty())
        assertTrue(vm.uiState.value.unlockedIds.isEmpty())
        assertTrue(vm.uiState.value.newlyUnlocked.isEmpty())

        coVerify(exactly = 1) { checkAndUnlock(stats) }
    }

    @Test
    fun `init when newlyUnlocked not empty emits snackbar`() = runTest {
        val stats = mockk<UserStats>(relaxed = true)

        val achievement = mockk<AppAchievement>()
        every { achievement.name } returns "Winner"

        val result = mockk<CheckResult>()
        every { result.allAchievements } returns emptyList()
        every { result.unlockedIds } returns emptyList()
        every { result.newlyUnlocked } returns listOf(achievement)

        val vm = buildViewModel(stats = stats, result = result)

        advanceUntilIdle()

        vm.sideEffect.test {
            assertTrue(awaitItem() is AchievementsContract.SideEffect.ShowSnackbar)
        }
    }
    @Test
    fun `init when exception sets errorMessage`() = runTest {
        val stats = mockk<UserStats>(relaxed = true)
        val user = mockk<User>(relaxed = true)
        every { user.stats } returns stats

        every { getCurrentUser() } returns flowOf(user)
        coEvery { checkAndUnlock(stats) } throws RuntimeException("boom")

        val vm = AchievementsViewModel(checkAndUnlock, getCurrentUser, seedAchievements)

        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals("boom", vm.uiState.value.errorMessage)
    }


    @Test
    fun `SelectCategory updates selectedCategory`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        val category = AchievementCategory.values().first()
        vm.onEvent(AchievementsContract.Event.SelectCategory(category))

        assertEquals(category, vm.uiState.value.selectedCategory)
    }

    @Test
    fun `SetCompletionFilter updates completionFilter`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()

        val filter = mockk<AchievementsContract.CompletionFilter>(relaxed = true)
        vm.onEvent(AchievementsContract.Event.SetCompletionFilter(filter))

        assertEquals(filter, vm.uiState.value.completionFilter)
    }

    @Test
    fun `Refresh calls loadAchievements again`() = runTest {
        val stats = mockk<UserStats>(relaxed = true)

        val result = mockk<CheckResult>(relaxed = true)
        val vm = buildViewModel(stats = stats, result = result)

        advanceUntilIdle()

        vm.onEvent(AchievementsContract.Event.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 2) { checkAndUnlock(stats) }
    }

}