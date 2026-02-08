package com.example.mycomposeapp.feature.register.presentation

object RegisterContract {

    data class State(
        val fullName: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val fullNameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val termsAccepted: Boolean = false,
        val termsError: String? = null,
        val isLoading: Boolean = false,
        val generalError: String? = null,
        val showSuccessDialog: Boolean = false
    )

    sealed interface Event {
        data class OnFullNameChanged(val fullName: String) : Event
        data class OnEmailChanged(val email: String) : Event
        data class OnPasswordChanged(val password: String) : Event
        data class OnConfirmPasswordChanged(val confirmPassword: String) : Event
        data class OnTermsAcceptedChanged(val termsAccepted: Boolean) : Event
        data object OnRegisterClicked : Event
        data object OnLoginClicked : Event
        data object OnSuccessDialogDismissed : Event
    }

    sealed interface SideEffect {
        data object NavigateToDashboard : SideEffect
        data object NavigateToLogin : SideEffect
    }
}
