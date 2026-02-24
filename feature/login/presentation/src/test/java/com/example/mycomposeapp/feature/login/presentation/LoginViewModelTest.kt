@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.login.presentation

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.model.ValidationResult
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import com.example.mycomposeapp.core.domain.usecase.auth.LoginUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase
import com.example.mycomposeapp.core.presentation.common.GoogleSignInHandler
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.*

class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var googleSignInHandler: GoogleSignInHandler
    private lateinit var validateEmail: ValidateEmailUseCase
    private lateinit var validatePassword: ValidatePasswordUseCase
    private lateinit var dataStore: DataStoreManager

    private fun buildViewModel(): LoginViewModel =
        LoginViewModel(
            loginUseCase = loginUseCase,
            googleSignInHandler = googleSignInHandler,
            validateEmailUseCase = validateEmail,
            validatePasswordUseCase = validatePassword,
            dataStoreManager = dataStore
        )

    private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
    private val KEY_SAVED_EMAIL = stringPreferencesKey("saved_email")

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        loginUseCase = mockk()
        googleSignInHandler = mockk()
        validateEmail = mockk()
        validatePassword = mockk()
        dataStore = mockk()

        every { validateEmail(any()) } returns ValidationResult(true, null)
        every { validatePassword(any()) } returns ValidationResult(true, null)

        every { dataStore.getPreference(KEY_REMEMBER_ME, false) } returns flowOf(false)
        every { dataStore.getPreference(KEY_SAVED_EMAIL, "") } returns flowOf("")

        coEvery { dataStore.setPreference(any<Preferences.Key<String>>(), any<String>()) } just Runs
        coEvery { dataStore.setPreference(any<Preferences.Key<Boolean>>(), any<Boolean>()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }


    @Test
    fun `Given rememberMe false When init Then does not prefill email`() = runTest {
        // Given
        every { dataStore.getPreference(KEY_REMEMBER_ME, false) } returns flowOf(false)

        // When
        val vm = buildViewModel()
        advanceUntilIdle()

        // Then
        assertFalse(vm.uiState.value.rememberMe)
        assertTrue(vm.uiState.value.email.isEmpty())
    }

    @Test
    fun `Given rememberMe true When init Then loads saved email`() = runTest {
        // Given
        every { dataStore.getPreference(KEY_REMEMBER_ME, false) } returns flowOf(true)
        every { dataStore.getPreference(KEY_SAVED_EMAIL, "") } returns flowOf("saved@mail.com")

        // When
        val vm = buildViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(vm.uiState.value.rememberMe)
        assertEquals("saved@mail.com", vm.uiState.value.email)
    }


    @Test
    fun `Given previous errors When OnEmailChanged Then clears emailError and generalError`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnGoogleSignInFailed("old"))
        assertEquals("old", vm.uiState.value.generalError)

        // When
        vm.onEvent(LoginContract.Event.OnEmailChanged("a@b.com"))

        // Then
        assertEquals("a@b.com", vm.uiState.value.email)
        assertNull(vm.uiState.value.emailError)
        assertNull(vm.uiState.value.generalError)
    }

    @Test
    fun `Given previous errors When OnPasswordChanged Then clears passwordError and generalError`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnGoogleSignInFailed("old"))
        assertEquals("old", vm.uiState.value.generalError)

        // When
        vm.onEvent(LoginContract.Event.OnPasswordChanged("pass"))

        // Then
        assertEquals("pass", vm.uiState.value.password)
        assertNull(vm.uiState.value.passwordError)
        assertNull(vm.uiState.value.generalError)
    }

    @Test
    fun `Given rememberMe toggled When OnRememberMeChanged Then updates state`() = runTest {
        // Given
        val vm = buildViewModel()
        assertFalse(vm.uiState.value.rememberMe)

        // When
        vm.onEvent(LoginContract.Event.OnRememberMeChanged(true))

        // Then
        assertTrue(vm.uiState.value.rememberMe)
    }


    @Test
    fun `Given forgot password clicked When OnForgotPasswordClicked Then snackbar emitted`() = runTest {
        // Given
        val vm = buildViewModel()

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnForgotPasswordClicked)

            // Then
            assertTrue(awaitItem() is LoginContract.SideEffect.ShowSnackbar)
        }
    }

    @Test
    fun `Given register clicked When OnRegisterClicked Then NavigateToRegister emitted`() = runTest {
        // Given
        val vm = buildViewModel()

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnRegisterClicked)

            // Then
            assertEquals(LoginContract.SideEffect.NavigateToRegister, awaitItem())
        }
    }

    @Test
    fun `Given google sign in clicked When OnGoogleSignInClicked Then LaunchGoogleSignIn emitted`() = runTest {
        // Given
        val vm = buildViewModel()

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnGoogleSignInClicked)

            // Then
            assertEquals(LoginContract.SideEffect.LaunchGoogleSignIn, awaitItem())
        }
    }


    @Test
    fun `Given null idToken When OnGoogleSignInResult Then google loading false`() = runTest {
        // Given
        val vm = buildViewModel()

        // When
        vm.onEvent(LoginContract.Event.OnGoogleSignInResult(null))

        // Then
        assertFalse(vm.uiState.value.isGoogleLoading)
    }

    @Test
    fun `Given blank idToken When OnGoogleSignInResult Then error set`() = runTest {
        // Given
        val vm = buildViewModel()

        // When
        vm.onEvent(LoginContract.Event.OnGoogleSignInResult("  "))

        // Then
        assertFalse(vm.uiState.value.isGoogleLoading)
        assertEquals("Google sign-in failed", vm.uiState.value.generalError)
    }

    @Test
    fun `Given handler Success When OnGoogleSignInResult Then navigate dashboard`() = runTest {
        // Given
        val vm = buildViewModel()
        coEvery { googleSignInHandler.handle("token") } returns Resource.Success("ok")

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnGoogleSignInResult("token"))
            advanceUntilIdle()

            // Then
            assertFalse(vm.uiState.value.isGoogleLoading)
            assertEquals(LoginContract.SideEffect.NavigateToDashboard, awaitItem())
        }

        coVerify(exactly = 1) { googleSignInHandler.handle("token") }
    }

    @Test
    fun `Given handler Error When OnGoogleSignInResult Then error set`() = runTest {
        // Given
        val vm = buildViewModel()
        coEvery { googleSignInHandler.handle("token") } returns Resource.Error("bad")

        // When
        vm.onEvent(LoginContract.Event.OnGoogleSignInResult("token"))
        advanceUntilIdle()

        // Then
        assertFalse(vm.uiState.value.isGoogleLoading)
        assertEquals("bad", vm.uiState.value.generalError)
    }

    @Test
    fun `Given handler Loading When OnGoogleSignInResult Then remains loading`() = runTest {
        // Given
        val vm = buildViewModel()
        coEvery { googleSignInHandler.handle("token") } returns Resource.Loading

        // When
        vm.onEvent(LoginContract.Event.OnGoogleSignInResult("token"))
        advanceUntilIdle()

        // Then
        assertTrue(vm.uiState.value.isGoogleLoading)
        assertNull(vm.uiState.value.generalError)
    }

    @Test
    fun `Given google sign in failed When OnGoogleSignInFailed Then state updated`() = runTest {
        // Given
        val vm = buildViewModel()

        // When
        vm.onEvent(LoginContract.Event.OnGoogleSignInFailed("fail"))

        // Then
        assertFalse(vm.uiState.value.isGoogleLoading)
        assertEquals("fail", vm.uiState.value.generalError)
    }

    // ---------------- Login validation fail ----------------

    @Test
    fun `Given invalid email and password When OnLoginClicked Then sets field errors and does not call login`() = runTest {
        // Given
        val vm = buildViewModel()
        every { validateEmail(any()) } returns ValidationResult(false, "bad email")
        every { validatePassword(any()) } returns ValidationResult(false, "bad pass")

        // When
        vm.onEvent(LoginContract.Event.OnLoginClicked)
        dispatcher.scheduler.runCurrent()

        // Then
        assertEquals("bad email", vm.uiState.value.emailError)
        assertEquals("bad pass", vm.uiState.value.passwordError)

        coVerify(exactly = 0) { loginUseCase(any(), any()) }
    }

    // ---------------- Login success rememberMe true ----------------

    @Test
    fun `Given rememberMe true When login success Then saves creds and navigates`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnEmailChanged("a@b.com"))
        vm.onEvent(LoginContract.Event.OnPasswordChanged("pass"))
        vm.onEvent(LoginContract.Event.OnRememberMeChanged(true))

        coEvery { loginUseCase("a@b.com", "pass") } returns Resource.Success("token123")

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnLoginClicked)
            advanceUntilIdle()

            // Then
            assertFalse(vm.uiState.value.isLoading)
            assertEquals(LoginContract.SideEffect.NavigateToDashboard, awaitItem())
        }

        coVerify(exactly = 1) { loginUseCase("a@b.com", "pass") }
        coVerify(exactly = 1) { dataStore.setPreference(PreferenceKeys.TOKEN, "token123") }
        coVerify(exactly = 1) { dataStore.setPreference(KEY_REMEMBER_ME, true) }
        coVerify(exactly = 1) { dataStore.setPreference(KEY_SAVED_EMAIL, "a@b.com") }
    }

    // ---------------- Login success rememberMe false ----------------

    @Test
    fun `Given rememberMe false When login success Then clears saved creds and navigates`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnEmailChanged("a@b.com"))
        vm.onEvent(LoginContract.Event.OnPasswordChanged("pass"))
        vm.onEvent(LoginContract.Event.OnRememberMeChanged(false))

        coEvery { loginUseCase("a@b.com", "pass") } returns Resource.Success("token123")

        vm.sideEffect.test {
            // When
            vm.onEvent(LoginContract.Event.OnLoginClicked)
            advanceUntilIdle()

            // Then
            assertFalse(vm.uiState.value.isLoading)
            assertEquals(LoginContract.SideEffect.NavigateToDashboard, awaitItem())
        }

        coVerify(exactly = 1) { dataStore.setPreference(KEY_REMEMBER_ME, false) }
        coVerify(exactly = 1) { dataStore.setPreference(KEY_SAVED_EMAIL, "") }
        coVerify(exactly = 0) { dataStore.setPreference(PreferenceKeys.TOKEN, any()) }
    }

    // ---------------- Login error ----------------

    @Test
    fun `Given login Error When OnLoginClicked Then sets generalError`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnEmailChanged("a@b.com"))
        vm.onEvent(LoginContract.Event.OnPasswordChanged("pass"))

        coEvery { loginUseCase("a@b.com", "pass") } returns Resource.Error("bad")

        // When
        vm.onEvent(LoginContract.Event.OnLoginClicked)
        advanceUntilIdle()

        // Then
        assertFalse(vm.uiState.value.isLoading)
        assertEquals("bad", vm.uiState.value.generalError)
    }


    @Test
    fun `Given login Loading When OnLoginClicked Then remains loading`() = runTest {
        // Given
        val vm = buildViewModel()
        vm.onEvent(LoginContract.Event.OnEmailChanged("a@b.com"))
        vm.onEvent(LoginContract.Event.OnPasswordChanged("pass"))

        coEvery { loginUseCase("a@b.com", "pass") } returns Resource.Loading

        // When
        vm.onEvent(LoginContract.Event.OnLoginClicked)
        advanceUntilIdle()

        // Then
        assertTrue(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.generalError)
    }
}