package com.example.mycomposeapp.feature.profile.presentation

import com.example.mycomposeapp.core.presentation.common.BaseViewModel
class ProfileViewModel : BaseViewModel<ProfileContract.State, ProfileContract.SideEffect, ProfileContract.Event>(
    initialState = ProfileContract.State()
) {


    fun onEvent(event: ProfileContract.Event){
        when(event) {
            ProfileContract.Event.OnSettingsClicked -> TODO()
            ProfileContract.Event.OnBackButtonClicked -> TODO()
            ProfileContract.Event.OnEditProfileClicked -> TODO()
            ProfileContract.Event.EditProfileClicked -> TODO()
            ProfileContract.Event.LogoutClicked -> TODO()
            ProfileContract.Event.NotificationsClicked -> TODO()
            ProfileContract.Event.ToggleDarkTheme -> TODO()
        }
    }
}