@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.game.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.domain.network.ConnectivityObserver
import com.example.mycomposeapp.feature.achievements.domain.model.AppAchievement
import com.example.mycomposeapp.feature.achievements.domain.model.CheckResult
import com.example.mycomposeapp.feature.achievements.domain.usecase.CheckAndUnlockAchievementsUseCase
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.games.Achievement
import com.example.mycomposeapp.feature.game.domain.usecase.movies.SearchMoviesUseCase
import com.example.mycomposeapp.feature.game.presentation.delegate.GameDelegateFactory
import com.example.mycomposeapp.feature.game.presentation.delegate.GameModeDelegate
import com.example.mycomposeapp.feature.game.presentation.navigation.GameRoute
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.*

class GameViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var delegateFactory: GameDelegateFactory
    private lateinit var delegate: GameModeDelegate
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var searchUseCase: SearchMoviesUseCase
    private lateinit var checkAchievements: CheckAndUnlockAchievementsUseCase

    private lateinit var viewModel: GameViewModel

    private val route = GameRoute(
        gameModeId = "1",
        categoryType = "movie",
        archiveDate = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        savedStateHandle = mockk()
        delegateFactory = mockk()
        delegate = mockk(relaxed = true)
        connectivityObserver = mockk()
        searchUseCase = mockk()
        checkAchievements = mockk()

        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<GameRoute>() } returns route

        every {
            delegateFactory.create(any(), any(), any())
        } returns delegate

        every { connectivityObserver.hasInternet } returns flowOf(true)

        viewModel = GameViewModel(
            savedStateHandle,
            delegateFactory,
            connectivityObserver,
            searchUseCase,
            checkAchievements
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init attaches delegate and loads game`() {
        verify { delegate.attach(any()) }
        verify { delegate.loadGame() }
    }

    @Test
    fun `text shorter than min clears results`() {
        viewModel.onEvent(GameContract.Event.OnAnswerTextChanged("a"))

        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `search success updates results`() = runTest {
        coEvery { searchUseCase(any(), any(), any()) } returns flow {
            emit(Resource.Loading)
            emit(Resource.Success(listOf(mockk(relaxed = true))))
        }

        viewModel.onEvent(GameContract.Event.OnAnswerTextChanged("batman"))

        advanceTimeBy(GameConstants.SEARCH_DEBOUNCE_MS)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchResults.isNotEmpty())
        assertFalse(viewModel.uiState.value.isSearching)

        coVerify { searchUseCase(route.categoryType, "batman", route.gameModeId) }
    }

    @Test
    fun `search error clears results`() = runTest {
        coEvery { searchUseCase(any(), any(), any()) } returns flow {
            emit(Resource.Loading)
            emit(Resource.Error("error"))
        }

        viewModel.onEvent(
            GameContract.Event.OnAnswerTextChanged("batman")
        )

        advanceTimeBy(GameConstants.SEARCH_DEBOUNCE_MS)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `suggestion selected submits answer`() {
        viewModel.onEvent(
            GameContract.Event.OnSuggestionSelected("Titanic")
        )

        verify { delegate.onAnswerSubmitted("Titanic") }
        assertEquals("Titanic", viewModel.uiState.value.userAnswer)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `internet lost exits game and shows snackbar`() = runTest {
        every { connectivityObserver.hasInternet } returns flowOf(false)

        viewModel = GameViewModel(
            savedStateHandle,
            delegateFactory,
            connectivityObserver,
            searchUseCase,
            checkAchievements
        )

        viewModel.sideEffect.test {
            assertTrue(awaitItem() is GameContract.SideEffect.Exit)
            assertTrue(awaitItem() is GameContract.SideEffect.ShowSnackbar)
        }

        verify { delegate.onExitGame() }
    }

    @Test
    fun `all delegate events are forwarded`() {
        viewModel.onEvent(GameContract.Event.OnNextQuestion)
        viewModel.onEvent(GameContract.Event.OnRetryGame)
        viewModel.onEvent(GameContract.Event.OnExitGame)
        viewModel.onEvent(GameContract.Event.OnRevealMore)
        viewModel.onEvent(GameContract.Event.OnUseHint)
        viewModel.onEvent(GameContract.Event.OnUseCategoryHint)
        viewModel.onEvent(GameContract.Event.OnAnswerSubmitted("a"))
        viewModel.onEvent(GameContract.Event.OnMangaSelected(5))

        verify { delegate.onNextQuestion() }
        verify { delegate.onRetryGame() }
        verify { delegate.onExitGame() }
        verify { delegate.onRevealMore() }
        verify { delegate.onUseHint() }
        verify { delegate.onUseCategoryHint() }
        verify { delegate.onAnswerSubmitted("a") }
        verify { delegate.onAnswerSubmitted("5") }
    }

    @Test
    fun `achievement unlocked shows snackbar`() = runTest {
        val stats = mockk<UserStats>()

        val achievement = mockk<AppAchievement>()
        every { achievement.name } returns "Winner"

        val result = mockk<CheckResult>()
        every { result.newlyUnlocked } returns listOf(achievement)

        coEvery { checkAchievements(stats) } returns result

        viewModel.onGameCompleted(stats)

        viewModel.sideEffect.test {
            assertTrue(awaitItem() is GameContract.SideEffect.ShowSnackbar)
        }
    }

    @Test
    fun `achievement empty does nothing`() = runTest {
        val stats = mockk<UserStats>()

        val result = mockk<CheckResult>()
        every { result.newlyUnlocked } returns emptyList()

        coEvery { checkAchievements(stats) } returns result

        viewModel.onGameCompleted(stats)

        viewModel.sideEffect.test {
            expectNoEvents()
        }
    }


    @Test
    fun `achievement exception swallowed`() = runTest {
        val stats = mockk<UserStats>()

        coEvery { checkAchievements(stats) } throws RuntimeException()

        viewModel.onGameCompleted(stats)

        viewModel.sideEffect.test {
            expectNoEvents()
        }
    }


    @Test
    fun `null stats does nothing`() = runTest {
        viewModel.onGameCompleted(null)

        viewModel.sideEffect.test {
            expectNoEvents()
        }
    }


    @Test
    fun `when OnExitGame event then delegate called`() {
        viewModel.onEvent(GameContract.Event.OnExitGame)
        verify { delegate.onExitGame() }
    }
}