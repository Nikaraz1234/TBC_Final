package com.example.mycomposeapp.feature.welcome.presentation

object WelcomeContract {

    data class State(
        val isGoogleLoading: Boolean = false,
        val generalError: String? = null
    )

    sealed interface Event {
        data object OnLoginClicked : Event
        data object OnRegisterClicked : Event
        data object OnGoogleSignInClicked : Event
        data class OnGoogleSignInResult(val idToken: String?) : Event
        data class OnGoogleSignInFailed(val errorMessage: String) : Event
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect
        data object NavigateToRegister : SideEffect
        data object NavigateToDashboard : SideEffect
        data object LaunchGoogleSignIn : SideEffect
    }
}
