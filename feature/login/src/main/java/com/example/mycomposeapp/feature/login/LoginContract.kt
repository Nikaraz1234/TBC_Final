package com.example.mycomposeapp.feature.login

object LoginContract {

    data class State(
        val email: String = "",
        val password: String = "",
        val emailError: String? = null,
        val passwordError: String? = null,
        val rememberMe: Boolean = false,
        val isLoading: Boolean = false,
        val generalError: String? = null
    )

    sealed interface Event {
        data class OnEmailChanged(val email: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data class OnRememberMeChanged(val rememberMe: Boolean) : Event
        data object OnLoginClicked : Event
        data object OnForgotPasswordClicked : Event
        data object OnRegisterClicked : Event
    }

    sealed interface SideEffect {
        data object NavigateToDashboard : SideEffect
        data object NavigateToRegister : SideEffect
        data class ShowSnackbar(val message: String) : SideEffect
    }
}
