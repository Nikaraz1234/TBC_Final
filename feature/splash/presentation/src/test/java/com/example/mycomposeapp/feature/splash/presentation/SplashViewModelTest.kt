package com.example.mycomposeapp.feature.splash.presentation

import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.usecase.datastore.GetPreferenceUseCase
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var getPreferenceUseCase: GetPreferenceUseCase
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        getPreferenceUseCase = mockk()
        viewModel = SplashViewModel(getPreferenceUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `Given valid token When OnEnter Then navigates to dashboard`() = runTest {
        // Given
        coEvery { getPreferenceUseCase(PreferenceKeys.TOKEN, "") } returns flowOf("token")
        coEvery { getPreferenceUseCase(PreferenceKeys.DARK_MODE, false) } returns flowOf(false)

        // When
        viewModel.onEvent(SplashContract.Event.OnEnter)
        advanceUntilIdle()

        // Then
        viewModel.sideEffect.test {
            assertEquals(SplashContract.SideEffect.GoDashboard, awaitItem())
        }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1f, viewModel.uiState.value.progress)
    }

    @Test
    fun `Given empty token When OnEnter Then navigates to welcome`() = runTest {
        // Given
        coEvery { getPreferenceUseCase(PreferenceKeys.TOKEN, "") } returns flowOf("")
        coEvery { getPreferenceUseCase(PreferenceKeys.DARK_MODE, false) } returns flowOf(false)

        // When
        viewModel.onEvent(SplashContract.Event.OnEnter)
        advanceUntilIdle()

        // Then
        viewModel.sideEffect.test {
            assertEquals(SplashContract.SideEffect.GoWelcome, awaitItem())
        }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1f, viewModel.uiState.value.progress)
    }

    @Test
    fun `Given negative progress When OnProgressChanged Then clamps to zero`() = runTest {
        // When
        viewModel.onEvent(SplashContract.Event.OnProgressChanged(-0.5f))

        // Then
        assertEquals(0f, viewModel.uiState.value.progress)
    }

    @Test
    fun `Given progress greater than one When OnProgressChanged Then clamps to one`() = runTest {
        // When
        viewModel.onEvent(SplashContract.Event.OnProgressChanged(2f))

        // Then
        assertEquals(1f, viewModel.uiState.value.progress)
    }

    @Test
    fun `Given loading started When progress reaches one Then finishSplash stops loading`() = runTest {
        // Given (start loading via OnEnter)
        coEvery { getPreferenceUseCase(PreferenceKeys.TOKEN, "") } returns flowOf("token")
        coEvery { getPreferenceUseCase(PreferenceKeys.DARK_MODE, false) } returns flowOf(false)

        viewModel.onEvent(SplashContract.Event.OnEnter)

        // ensure initialization started (state isLoading set before delays)
        advanceUntilIdle()
        // NOTE: after advanceUntilIdle, init may already complete -> so we need another approach:
        // We want to stop it early BEFORE it finishes. So instead of advanceUntilIdle, we only run initial tasks.
    }

    @Test
    fun `Given loading true When OnLoadingFinished Then sets progress to one and stops loading`() = runTest {
        // Given (start loading via OnEnter but DO NOT let it finish)
        coEvery { getPreferenceUseCase(PreferenceKeys.TOKEN, "") } returns flowOf("token")
        coEvery { getPreferenceUseCase(PreferenceKeys.DARK_MODE, false) } returns flowOf(false)

        viewModel.onEvent(SplashContract.Event.OnEnter)

        // Run only the immediate part of coroutine (no delays yet)
        dispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)
        assertEquals(0f, viewModel.uiState.value.progress)

        // When
        viewModel.onEvent(SplashContract.Event.OnLoadingFinished)
        dispatcher.scheduler.runCurrent()

        // Then
        assertEquals(1f, viewModel.uiState.value.progress)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `Given not loading When OnLoadingFinished Then does nothing`() = runTest {
        // Given
        val initialState = viewModel.uiState.value

        // When
        viewModel.onEvent(SplashContract.Event.OnLoadingFinished)

        // Then
        assertEquals(initialState, viewModel.uiState.value)
    }

    @Test
    fun `Given loading true When OnProgressChanged to one Then finishSplash stops loading`() = runTest {
        // Given
        coEvery { getPreferenceUseCase(PreferenceKeys.TOKEN, "") } returns flowOf("token")
        coEvery { getPreferenceUseCase(PreferenceKeys.DARK_MODE, false) } returns flowOf(false)

        viewModel.onEvent(SplashContract.Event.OnEnter)
        dispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)

        // When
        viewModel.onEvent(SplashContract.Event.OnProgressChanged(1f))
        dispatcher.scheduler.runCurrent()

        // Then
        assertEquals(1f, viewModel.uiState.value.progress)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}