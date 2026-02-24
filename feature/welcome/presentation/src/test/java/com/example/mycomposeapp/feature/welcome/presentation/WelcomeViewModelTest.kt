@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.welcome.presentation

import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.presentation.common.GoogleSignInHandler
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.*

class WelcomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var googleSignInHandler: GoogleSignInHandler
    private lateinit var viewModel: WelcomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        googleSignInHandler = mockk()
        viewModel = WelcomeViewModel(googleSignInHandler)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `Given login clicked When OnLoginClicked Then NavigateToLogin emitted`() = runTest {
        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(WelcomeContract.Event.OnLoginClicked)

            // Then
            assertEquals(
                WelcomeContract.SideEffect.NavigateToLogin,
                awaitItem()
            )
        }
    }

    @Test
    fun `Given register clicked When OnRegisterClicked Then NavigateToRegister emitted`() = runTest {
        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(WelcomeContract.Event.OnRegisterClicked)

            // Then
            assertEquals(
                WelcomeContract.SideEffect.NavigateToRegister,
                awaitItem()
            )
        }
    }

    @Test
    fun `Given previous error When OnGoogleSignInClicked Then error cleared and LaunchGoogleSignIn emitted`() = runTest {
        // Given
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInFailed("old error")
        )
        assertEquals("old error", viewModel.uiState.value.generalError)

        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(WelcomeContract.Event.OnGoogleSignInClicked)

            // Then
            assertNull(viewModel.uiState.value.generalError)
            assertEquals(
                WelcomeContract.SideEffect.LaunchGoogleSignIn,
                awaitItem()
            )
        }
    }

    @Test
    fun `Given google sign-in failed When OnGoogleSignInFailed Then loading false and error set`() = runTest {
        // When
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInFailed("fail")
        )

        // Then
        assertFalse(viewModel.uiState.value.isGoogleLoading)
        assertEquals("fail", viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given null idToken When OnGoogleSignInResult Then loading stopped`() = runTest {
        // When
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInResult(null)
        )

        // Then
        assertFalse(viewModel.uiState.value.isGoogleLoading)
    }

    @Test
    fun `Given blank idToken When OnGoogleSignInResult Then error set`() = runTest {
        // When
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInResult("   ")
        )

        // Then
        assertFalse(viewModel.uiState.value.isGoogleLoading)
        assertEquals("Google sign-in failed", viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given handler returns Success When OnGoogleSignInResult Then navigate to dashboard`() = runTest {
        // Given
        coEvery {
            googleSignInHandler.handle("token")
        } returns Resource.Success("Ok")

        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(
                WelcomeContract.Event.OnGoogleSignInResult("token")
            )

            advanceUntilIdle()

            // Then
            assertFalse(viewModel.uiState.value.isGoogleLoading)
            assertEquals(
                WelcomeContract.SideEffect.NavigateToDashboard,
                awaitItem()
            )
        }

        coVerify(exactly = 1) {
            googleSignInHandler.handle("token")
        }
    }

    @Test
    fun `Given handler returns Error When OnGoogleSignInResult Then error shown`() = runTest {
        // Given
        coEvery {
            googleSignInHandler.handle("token")
        } returns Resource.Error("bad")

        // When
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInResult("token")
        )
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isGoogleLoading)
        assertEquals("bad", viewModel.uiState.value.generalError)

        coVerify(exactly = 1) {
            googleSignInHandler.handle("token")
        }
    }

    @Test
    fun `Given handler returns Loading When OnGoogleSignInResult Then loading remains true`() = runTest {
        // Given
        coEvery {
            googleSignInHandler.handle("token")
        } returns Resource.Loading

        // When
        viewModel.onEvent(
            WelcomeContract.Event.OnGoogleSignInResult("token")
        )
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isGoogleLoading)
        assertNull(viewModel.uiState.value.generalError)

        coVerify(exactly = 1) {
            googleSignInHandler.handle("token")
        }
    }
}