package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import com.example.mycomposeapp.core.presentation.common.BaseViewModel

class EditProfileViewModel : BaseViewModel<EditProfileContract.State, EditProfileContract.SideEffect, EditProfileContract.Event >(
    initialState = EditProfileContract.State()
) {
    fun onEvent(event: EditProfileContract.Event){
        when(event) {
            EditProfileContract.Event.OnSaveClicked -> saveChanges()
            EditProfileContract.Event.OnBackClick -> TODO()
            EditProfileContract.Event.OnChangePhotoClicked -> TODO()
        }
    }

    private fun saveChanges(){

    }
}