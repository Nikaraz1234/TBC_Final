@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.mycomposeapp.feature.register.presentation

import app.cash.turbine.test
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.domain.model.ValidationResult
import com.example.mycomposeapp.core.domain.usecase.auth.RegisterUseCase
import com.example.mycomposeapp.core.domain.usecase.user.RefreshUserUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateConfirmPasswordUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateNameUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import kotlin.test.*

class RegisterViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var refreshUserUseCase: RefreshUserUseCase
    private lateinit var validateName: ValidateNameUseCase
    private lateinit var validateEmail: ValidateEmailUseCase
    private lateinit var validatePassword: ValidatePasswordUseCase
    private lateinit var validateConfirmPassword: ValidateConfirmPasswordUseCase

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        registerUseCase = mockk()
        refreshUserUseCase = mockk()
        validateName = mockk()
        validateEmail = mockk()
        validatePassword = mockk()
        validateConfirmPassword = mockk()

        // Default: all validations pass
        every { validateName(any()) } returns ValidationResult(true, null)
        every { validateEmail(any()) } returns ValidationResult(true, null)
        every { validatePassword(any()) } returns ValidationResult(true, null)
        every { validateConfirmPassword(any(), any()) } returns ValidationResult(true, null)

        viewModel = RegisterViewModel(
            registerUseCase = registerUseCase,
            refreshUserUseCase = refreshUserUseCase,
            validateNameUseCase = validateName,
            validateEmailUseCase = validateEmail,
            validatePasswordUseCase = validatePassword,
            validateConfirmPasswordUseCase = validateConfirmPassword
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }


    @Test
    fun `Given previous errors When OnFullNameChanged Then updates name and clears errors`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()

        // When
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))

        // Then
        assertEquals("Nika", viewModel.uiState.value.fullName)
        assertNull(viewModel.uiState.value.fullNameError)
        assertNull(viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given previous errors When OnEmailChanged Then updates email and clears errors`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()

        // When
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))

        // Then
        assertEquals("a@b.com", viewModel.uiState.value.email)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given previous errors When OnPasswordChanged Then updates password and clears errors`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()

        // When
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))

        // Then
        assertEquals("pass", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
        assertNull(viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given previous errors When OnConfirmPasswordChanged Then updates confirm and clears errors`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()

        // When
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))

        // Then
        assertEquals("pass", viewModel.uiState.value.confirmPassword)
        assertNull(viewModel.uiState.value.confirmPasswordError)
        assertNull(viewModel.uiState.value.generalError)
    }

    @Test
    fun `Given previous terms error When OnTermsAcceptedChanged Then updates and clears termsError`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()
        assertNotNull(viewModel.uiState.value.termsError)

        // When
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        // Then
        assertTrue(viewModel.uiState.value.termsAccepted)
        assertNull(viewModel.uiState.value.termsError)
    }

    // ---------------- NAV EVENTS ----------------

    @Test
    fun `Given login clicked When OnLoginClicked Then NavigateToLogin emitted`() = runTest {
        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(RegisterContract.Event.OnLoginClicked)

            // Then
            assertEquals(RegisterContract.SideEffect.NavigateToLogin, awaitItem())
        }
    }

    @Test
    fun `Given registration success When OnSuccessDialogDismissed Then hides dialog and navigates dashboard`() = runTest {
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        coEvery { registerUseCase(email = any(), password = any(), displayName = any()) } returns
                Resource.Success("ok")
        coEvery { refreshUserUseCase() } just Runs

        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showSuccessDialog)

        viewModel.sideEffect.test {
            // When
            viewModel.onEvent(RegisterContract.Event.OnSuccessDialogDismissed)

            // Then
            assertFalse(viewModel.uiState.value.showSuccessDialog)
            assertEquals(RegisterContract.SideEffect.NavigateToDashboard, awaitItem())
        }
    }

    // ---------------- REGISTER VALIDATION FAIL ----------------

    @Test
    fun `Given invalid inputs When OnRegisterClicked Then sets field errors and does not call register`() = runTest {
        // Given
        every { validateName(any()) } returns ValidationResult(false, "bad name")
        every { validateEmail(any()) } returns ValidationResult(false, "bad email")
        every { validatePassword(any()) } returns ValidationResult(false, "bad pass")
        every { validateConfirmPassword(any(), any()) } returns ValidationResult(false, "bad confirm")

        // When
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        dispatcher.scheduler.runCurrent()

        // Then
        assertEquals("bad name", viewModel.uiState.value.fullNameError)
        assertEquals("bad email", viewModel.uiState.value.emailError)
        assertEquals("bad pass", viewModel.uiState.value.passwordError)
        assertEquals("bad confirm", viewModel.uiState.value.confirmPasswordError)
        assertNotNull(viewModel.uiState.value.termsError)

        coVerify(exactly = 0) { registerUseCase(any(), any(), any()) }
    }


    @Test
    fun `Given valid inputs When register success Then shows success dialog`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        coEvery { registerUseCase(email = any(), password = any(), displayName = any()) } returns
                Resource.Success("ok")

        coEvery { refreshUserUseCase() } just Runs

        // When
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showSuccessDialog)

        coVerify(exactly = 1) {
            registerUseCase(
                email = "a@b.com",
                password = "pass",
                displayName = "Nika"
            )
        }
        coVerify(exactly = 1) { refreshUserUseCase() }
    }


    @Test
    fun `Given valid inputs When register success and refresh throws Then still shows success dialog`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        coEvery { registerUseCase(email = any(), password = any(), displayName = any()) } returns
                Resource.Success("ok")

        coEvery { refreshUserUseCase() } throws RuntimeException("fail")

        // When
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.showSuccessDialog)

        coVerify(exactly = 1) { refreshUserUseCase() }
    }


    @Test
    fun `Given valid inputs When register error Then sets generalError`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        coEvery { registerUseCase(email = any(), password = any(), displayName = any()) } returns
                Resource.Error("bad")

        // When
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("bad", viewModel.uiState.value.generalError)
        assertFalse(viewModel.uiState.value.showSuccessDialog)
    }


    @Test
    fun `Given valid inputs When register returns Loading Then remains loading`() = runTest {
        // Given
        viewModel.onEvent(RegisterContract.Event.OnFullNameChanged("Nika"))
        viewModel.onEvent(RegisterContract.Event.OnEmailChanged("a@b.com"))
        viewModel.onEvent(RegisterContract.Event.OnPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnConfirmPasswordChanged("pass"))
        viewModel.onEvent(RegisterContract.Event.OnTermsAcceptedChanged(true))

        coEvery { registerUseCase(email = any(), password = any(), displayName = any()) } returns
                Resource.Loading

        // When
        viewModel.onEvent(RegisterContract.Event.OnRegisterClicked)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.generalError)
    }
}