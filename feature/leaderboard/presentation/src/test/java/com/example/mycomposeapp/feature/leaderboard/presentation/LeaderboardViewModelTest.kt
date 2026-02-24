package com.example.mycomposeapp.feature.leaderboard.presentation

import com.example.test_utils.MainDispatcherRule
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.GameModeIds
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.usecase.user.GetAllUsersUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAllUsersUseCase: GetAllUsersUseCase = mockk()
    private lateinit var viewModel: LeaderboardViewModel

    @Before
    fun setup() {
        clearAllMocks()
        viewModel = LeaderboardViewModel(getAllUsersUseCase)
    }

    @Test
    fun givenUsersFlowLoadingThenSuccess_whenLoadLeaderboard_thenLoadingTogglesAndSortedByPoints() = runTest {
        // Given
        val u1 = fakeUser(id = "1", points = 10)
        val u2 = fakeUser(id = "2", points = 50)
        val u3 = fakeUser(id = "3", points = 30)

        coEvery { getAllUsersUseCase() } returns flow {
            emit(Resource.Loading)
            emit(Resource.Success(listOf(u1, u2, u3)))
        }

        // When
        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
        advanceUntilIdle()

        // Then
        coVerify { getAllUsersUseCase() }

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)

        assertEquals(3, viewModel.uiState.value.users.size)
        assertEquals(3, viewModel.uiState.value.filteredUsers.size)

        // sortedByDescending points => 2(50),3(30),1(10)
        assertEquals("2", viewModel.uiState.value.filteredUsers[0].userId)
        assertEquals("3", viewModel.uiState.value.filteredUsers[1].userId)
        assertEquals("1", viewModel.uiState.value.filteredUsers[2].userId)

        assertNull(viewModel.uiState.value.selectedStatsKey)
    }


    @Test
    fun givenUsersFlowLoadingThenError_whenLoadLeaderboard_thenErrorSetAndLoadingFalse() = runTest {
        // Given
        coEvery { getAllUsersUseCase() } returns flow {
            emit(Resource.Loading)
            emit(Resource.Error("fail"))
        }

        // When
        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("fail", viewModel.uiState.value.error)
    }

    @Test
    fun givenLoadedUsers_whenCategoryChanged_thenModesDefaultModeKeyAndSortingApplied() = runTest {
        // Given
        val category = LeaderboardFilter.Movie
        val key = GameModeIds.statsKey(category.firestorePrefix, GameModeIds.COVER) // default Movie mode = "Cover"

        val u1 = fakeUser(id = "1", points = 0, highScore = mapOf(key to 10))
        val u2 = fakeUser(id = "2", points = 0, highScore = mapOf(key to 99))
        val u3 = fakeUser(id = "3", points = 0, highScore = mapOf(key to 50))

        coEvery { getAllUsersUseCase() } returns flowOf(Resource.Success(listOf(u1, u2, u3)))

        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
        advanceUntilIdle()

        // When
        viewModel.onEvent(LeaderboardContract.Event.CategoryChanged(category))
        advanceUntilIdle()

        // Then
        assertEquals(category, viewModel.uiState.value.selectedCategory)
        assertEquals(listOf("Cover", "Emoji", "Plot"), viewModel.uiState.value.modes)
        assertEquals("Cover", viewModel.uiState.value.selectedMode)
        assertEquals(key, viewModel.uiState.value.selectedStatsKey)

        // sorted by highScore[key] => 2(99),3(50),1(10)
        assertEquals("2", viewModel.uiState.value.filteredUsers[0].userId)
        assertEquals("3", viewModel.uiState.value.filteredUsers[1].userId)
        assertEquals("1", viewModel.uiState.value.filteredUsers[2].userId)
    }

    @Test
    fun givenCategorySelected_whenModeChanged_thenKeyAndSortingUpdated() = runTest {
        // Given
        val category = LeaderboardFilter.Movie
        val coverKey = GameModeIds.statsKey(category.firestorePrefix, GameModeIds.COVER)
        val emojiKey = GameModeIds.statsKey(category.firestorePrefix, GameModeIds.EMOJI)

        val u1 = fakeUser(id = "1", points = 0, highScore = mapOf(coverKey to 10, emojiKey to 5))
        val u2 = fakeUser(id = "2", points = 0, highScore = mapOf(coverKey to 20, emojiKey to 100))
        val u3 = fakeUser(id = "3", points = 0, highScore = mapOf(coverKey to 30, emojiKey to 50))

        coEvery { getAllUsersUseCase() } returns flowOf(Resource.Success(listOf(u1, u2, u3)))

        viewModel.onEvent(LeaderboardContract.Event.LoadLeaderboard)
        advanceUntilIdle()

        viewModel.onEvent(LeaderboardContract.Event.CategoryChanged(category))
        advanceUntilIdle()

        // When
        viewModel.onEvent(LeaderboardContract.Event.ModeChanged("Emoji"))
        advanceUntilIdle()

        // Then
        assertEquals("Emoji", viewModel.uiState.value.selectedMode)
        assertEquals(emojiKey, viewModel.uiState.value.selectedStatsKey)

        // sorted by emojiKey => 2(100),3(50),1(5)
        assertEquals("2", viewModel.uiState.value.filteredUsers[0].userId)
        assertEquals("3", viewModel.uiState.value.filteredUsers[1].userId)
        assertEquals("1", viewModel.uiState.value.filteredUsers[2].userId)
    }

    @Test
    fun givenNoCategorySelected_whenModeChanged_thenStatsKeyEmptyAndNoCrash() = runTest {
        // Given (initial state selectedCategory == null)

        // When
        viewModel.onEvent(LeaderboardContract.Event.ModeChanged("Cover"))
        advanceUntilIdle()

        // Then
        assertEquals("Cover", viewModel.uiState.value.selectedMode)
        assertEquals("", viewModel.uiState.value.selectedStatsKey)
        assertTrue(viewModel.uiState.value.filteredUsers.isEmpty())
    }

    private fun fakeUser(
        id: String,
        points: Int,
        highScore: Map<String, Int> = emptyMap()
    ): User {
        return User(
            userId = id,
            username = "u$id",
            stats = UserStats(
                points = points,
                highScore = highScore
            )
        )
    }
}