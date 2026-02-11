package com.example.mycomposeapp.feature.profile.presentation.screen.profile

object ProfileContract {
    data class State(
        val isLoading: Boolean = true,
        val lvlProgress: Float = 0f,
        val isDarkTheme: Boolean = false
    )

    sealed interface Event {
        data object OnBackButtonClicked: Event
        data object OnSettingsClicked : Event
        data object OnEditProfileClicked: Event
        data object ToggleDarkTheme : Event
        data object EditProfileClicked : Event
        data object NotificationsClicked : Event
        data object LogoutClicked : Event
    }

    sealed interface SideEffect {
        data object ShowSettings: SideEffect
        data object GoBack: SideEffect
        data object GoToEditProfile: SideEffect
    }
}