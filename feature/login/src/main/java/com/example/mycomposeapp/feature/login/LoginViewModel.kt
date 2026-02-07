package com.example.mycomposeapp.feature.login

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.model.AuthResult
import com.example.mycomposeapp.core.domain.repository.DataStoreManager
import com.example.mycomposeapp.core.domain.usecase.auth.LoginUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidateEmailUseCase
import com.example.mycomposeapp.core.domain.usecase.validation.ValidatePasswordUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.login.LoginContract.Event
import com.example.mycomposeapp.feature.login.LoginContract.SideEffect
import com.example.mycomposeapp.feature.login.LoginContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel<State, SideEffect, Event>(State()) {

    companion object {
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val KEY_SAVED_EMAIL = stringPreferencesKey("saved_email")
    }

    init {
        loadSavedCredentials()
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            val rememberMe = dataStoreManager.getPreference(KEY_REMEMBER_ME, false).first()
            if (rememberMe) {
                val savedEmail = dataStoreManager.getPreference(KEY_SAVED_EMAIL, "").first()
                setState {
                    copy(
                        email = savedEmail,
                        rememberMe = true
                    )
                }
            }
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnEmailChanged -> {
                setState { copy(email = event.email, emailError = null, generalError = null) }
            }
            is Event.OnPasswordChanged -> {
                setState { copy(password = event.password, passwordError = null, generalError = null) }
            }
            is Event.OnRememberMeChanged -> {
                setState { copy(rememberMe = event.rememberMe) }
            }
            is Event.OnLoginClicked -> {
                performLogin()
            }
            is Event.OnForgotPasswordClicked -> {
                sendSideEffect(SideEffect.ShowSnackbar("Password reset coming soon"))
            }
            is Event.OnRegisterClicked -> {
                sendSideEffect(SideEffect.NavigateToRegister)
            }
        }
    }

    private fun performLogin() {
        val currentState = uiState.value

        val emailResult = validateEmailUseCase(currentState.email)
        val passwordResult = validatePasswordUseCase(currentState.password)

        val hasErrors = listOf(emailResult, passwordResult).any { !it.isValid }

        if (hasErrors) {
            setState {
                copy(
                    emailError = emailResult.errorMessage,
                    passwordError = passwordResult.errorMessage
                )
            }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, generalError = null) }

            when (val result = loginUseCase(currentState.email, currentState.password)) {
                is AuthResult.Success -> {
                    if (currentState.rememberMe) {
                        dataStoreManager.setPreference(KEY_REMEMBER_ME, true)
                        dataStoreManager.setPreference(KEY_SAVED_EMAIL, currentState.email)
                    } else {
                        dataStoreManager.setPreference(KEY_REMEMBER_ME, false)
                        dataStoreManager.setPreference(KEY_SAVED_EMAIL, "")
                    }
                    setState { copy(isLoading = false) }
                    sendSideEffect(SideEffect.NavigateToDashboard)
                }
                is AuthResult.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            generalError = result.message
                        )
                    }
                }
            }
        }
    }
}
