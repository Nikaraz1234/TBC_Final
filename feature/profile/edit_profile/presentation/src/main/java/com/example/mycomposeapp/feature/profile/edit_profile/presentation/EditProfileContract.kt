package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import com.example.mycomposeapp.feature.profile.edit_profile.domain.model.User

object EditProfileContract {
    data class State(
        val isLoading: Boolean = true,
        val user: User = User(
            userId = "",
            username = "",
            photoUrl = "",
            stats = TODO()
        )
    )

    sealed interface Event {
        data object OnSaveClicked: Event
        data object OnBackClick: Event
        data object OnChangePhotoClicked : Event
    }

    sealed interface SideEffect {
        data object GoBack: SideEffect
    }
}