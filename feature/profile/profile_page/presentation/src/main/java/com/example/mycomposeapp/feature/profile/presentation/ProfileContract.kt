package com.example.mycomposeapp.feature.profile.presentation

import com.example.mycomposeapp.core.domain.model.User

object ProfileContract {
    data class State(
        val isLoading: Boolean = true,
        val isDarkTheme: Boolean = false,
        val user: User? = null,
        val error: String? = null,
        val showSettingsSheet: Boolean = false
    )

    sealed interface Event {
        data object OnSettingsClicked : Event
        data object OnSettingsDismissed : Event
        data object OnEditProfileClicked : Event
        data object ToggleDarkTheme : Event
        data object NotificationsClicked : Event
        data object LogoutClicked : Event
        data object Load : Event
        data object DeleteUser: Event
    }

    sealed interface SideEffect {
        data object GoToEditProfile : SideEffect
        data object GoToWelcomeScreen : SideEffect
        data class ShowSnackBar(val msg: String) : SideEffect
    }
}
