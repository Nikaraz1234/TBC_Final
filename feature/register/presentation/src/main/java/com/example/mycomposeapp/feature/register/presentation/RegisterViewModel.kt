package com.example.mycomposeapp.feature.register.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.usecase.auth.RegisterUseCase
import com.example.mycomposeapp.core.domain.usecase.user.RefreshUserUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateConfirmPasswordUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateNameUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.Event
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.SideEffect
import com.example.mycomposeapp.feature.register.presentation.RegisterContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val refreshUserUseCase: RefreshUserUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase
) : BaseViewModel<State, SideEffect, Event>(State()) {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnFullNameChanged -> {
                setState { copy(fullName = event.fullName, fullNameError = null, generalError = null) }
            }
            is Event.OnEmailChanged -> {
                setState { copy(email = event.email, emailError = null, generalError = null) }
            }
            is Event.OnPasswordChanged -> {
                setState { copy(password = event.password, passwordError = null, generalError = null) }
            }
            is Event.OnConfirmPasswordChanged -> {
                setState { copy(confirmPassword = event.confirmPassword, confirmPasswordError = null, generalError = null) }
            }
            is Event.OnTermsAcceptedChanged -> {
                setState { copy(termsAccepted = event.termsAccepted, termsError = null) }
            }
            is Event.OnRegisterClicked -> {
                performRegistration()
            }
            is Event.OnLoginClicked -> {
                sendSideEffect(SideEffect.NavigateToLogin)
            }
            is Event.OnSuccessDialogDismissed -> {
                setState { copy(showSuccessDialog = false) }
                sendSideEffect(SideEffect.NavigateToDashboard)
            }
        }
    }

    private fun performRegistration() {
        val currentState = uiState.value

        val nameResult = validateNameUseCase(currentState.fullName)
        val emailResult = validateEmailUseCase(currentState.email)
        val passwordResult = validatePasswordUseCase(currentState.password)
        val confirmPasswordResult = validateConfirmPasswordUseCase(
            currentState.password,
            currentState.confirmPassword
        )

        val termsError = if (!currentState.termsAccepted) {
            "You must accept the Terms & Conditions"
        } else null

        val hasErrors = listOf(nameResult, emailResult, passwordResult, confirmPasswordResult)
            .any { !it.isValid } || termsError != null

        if (hasErrors) {
            setState {
                copy(
                    fullNameError = nameResult.errorMessage,
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage,
                    confirmPasswordError = confirmPasswordResult.errorMessage,
                    termsError = termsError
                )
            }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null) }

            when (val result = registerUseCase(
                email = currentState.email,
                password = currentState.password,
                displayName = currentState.fullName
            )) {
                is Resource.Success -> {
                    try {
                        refreshUserUseCase()
                    } catch (_: Exception) { }
                    setState { copy(isLoading = false, showSuccessDialog = true) }
                }
                is Resource.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            generalError = result.message
                        )
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
