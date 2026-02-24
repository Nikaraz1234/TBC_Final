package com.example.mycomposeapp.feature.welcome.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.core.domain.common.Resource
import com.example.mycomposeapp.core.presentation.common.GoogleSignInHandler
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.Event
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.SideEffect
import com.example.mycomposeapp.feature.welcome.presentation.WelcomeContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val googleSignInHandler: GoogleSignInHandler
) : BaseViewModel<State, SideEffect, Event>(State()) {

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnLoginClicked -> {
                sendSideEffect(SideEffect.NavigateToLogin)
            }
            is Event.OnRegisterClicked -> {
                sendSideEffect(SideEffect.NavigateToRegister)
            }
            is Event.OnGoogleSignInClicked -> {
                setState { copy(generalError = null) }
                sendSideEffect(SideEffect.LaunchGoogleSignIn)
            }
            is Event.OnGoogleSignInResult -> {
                handleGoogleSignInResult(event.idToken)
            }
            is Event.OnGoogleSignInFailed -> {
                setState { copy(isGoogleLoading = false, generalError = event.errorMessage) }
            }
        }
    }

    private fun handleGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            // User cancelled sign-in
            setState { copy(isGoogleLoading = false) }
            return
        }
        if (idToken.isBlank()) {
            setState { copy(isGoogleLoading = false, generalError = "Google sign-in failed") }
            return
        }
        viewModelScope.launch {
            setState { copy(isGoogleLoading = true, generalError = null) }

            when (val result = googleSignInHandler.handle(idToken)) {
                is Resource.Success -> {
                    setState { copy(isGoogleLoading = false) }
                    sendSideEffect(SideEffect.NavigateToDashboard)
                }
                is Resource.Error -> {
                    setState { copy(isGoogleLoading = false, generalError = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
