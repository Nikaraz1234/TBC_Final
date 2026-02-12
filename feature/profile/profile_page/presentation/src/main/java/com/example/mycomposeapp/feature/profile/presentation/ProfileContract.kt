package com.example.mycomposeapp.feature.profile.presentation

import com.example.mycomposeapp.core.domain.model.User

object ProfileContract {
    data class State(
        val isLoading: Boolean = true,
        val lvlProgress: Float = 0f,
        val isDarkTheme: Boolean = false,
        val user: User? = null,
        val error: String? = null
    )

    sealed interface Event {
        data object OnBackButtonClicked: Event
        data object OnSettingsClicked : Event
        data object OnEditProfileClicked: Event
        data object ToggleDarkTheme : Event
        data object EditProfileClicked : Event
        data object NotificationsClicked : Event
        data object LogoutClicked : Event
        data object Load : Event
    }

    sealed interface SideEffect {
        data object ShowSettings: SideEffect
        data object GoBack: SideEffect
        data object GoToEditProfile: SideEffect
        data object GoToWelcomeScreen : SideEffect
    }
}