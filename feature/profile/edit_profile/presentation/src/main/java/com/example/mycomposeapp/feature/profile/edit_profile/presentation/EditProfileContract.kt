package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import com.example.mycomposeapp.core.domain.model.User

object EditProfileContract {
    data class State(
        val isLoading: Boolean = true,
        val user: User? = null,
        val username: String = "",
        val email: String = "",
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val error: String? = null

    )

    sealed interface Event {
        data object OnScreenOpened : Event
        data object OnBackClick: Event
        data object OnChangePhotoClicked : Event
        data class OnUsernameChanged(val value: String) : Event
        data class OnCurrentPasswordChanged(val value: String) : Event
        data class OnNewPasswordChanged(val value: String) : Event
        data class OnConfirmPasswordChanged(val value: String) : Event
        data object OnChangeUsernameClicked : Event
        data object OnChangePasswordClicked: Event
        data class OnPhotoSelected(val uriString: String) : Event


    }

    sealed interface SideEffect {
        data object GoBack: SideEffect
    }
}