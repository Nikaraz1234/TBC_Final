package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.UploadProfilePhotoUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.ChangePasswordUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.ChangeUsernameUseCase
import com.example.mycomposeapp.feature.profile.edit_profile.domain.usecase.user.GetCurrentUserEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserEmail: GetCurrentUserEmailUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val changeUsernameUseCase: ChangeUsernameUseCase,
    private val getUserUseCase: GetCurrentUserUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
)  : BaseViewModel<EditProfileContract.State, EditProfileContract.SideEffect, EditProfileContract.Event >(
    initialState = EditProfileContract.State()
) {
    fun onEvent(event: EditProfileContract.Event){
        when(event) {
            EditProfileContract.Event.OnChangeUsernameClicked -> changeUsername()
            EditProfileContract.Event.OnChangePasswordClicked -> changePassword()
            EditProfileContract.Event.OnBackClick -> sendSideEffect(EditProfileContract.SideEffect.GoBack)
            EditProfileContract.Event.OnChangePhotoClicked -> TODO()
            is EditProfileContract.Event.OnCurrentPasswordChanged ->
                setState { copy(currentPassword = event.value, error = null) }

            is EditProfileContract.Event.OnNewPasswordChanged ->
                setState { copy(newPassword = event.value, error = null) }

            is EditProfileContract.Event.OnConfirmPasswordChanged ->
                setState { copy(confirmPassword = event.value, error = null) }

            EditProfileContract.Event.OnScreenOpened -> loadUser()
            is EditProfileContract.Event.OnPhotoSelected -> uploadPhoto(event.uriString)
            is EditProfileContract.Event.OnUsernameChanged -> setState { copy(username = event.value) }
        }
    }

    private fun uploadPhoto(uriString: String) {
        val userId = uiState.value.user?.userId ?: run {
            setState { copy(error = "User not loaded") }
            return
        }

        handleResponse(
            apiCall = { uploadProfilePhotoUseCase(userId, uriString) },
            onLoading = {
                setState { copy(isLoading = true, error = null) }
            },
            onSuccess = {
                setState { copy(isLoading = false, error = null) }
            },
            onError = { msg ->
                setState { copy(isLoading = false, error = msg) }
            }
        )
    }


    private fun loadUser() {
        val email = getCurrentUserEmail().orEmpty()
        setState { copy(email = email, isLoading = true) }

        viewModelScope.launch {
            getUserUseCase().collectLatest { user: User? ->
                if (user == null) {
                    setState { copy(isLoading = false, error = "User not found") }
                } else {
                    setState { copy(isLoading = false, user = user, error = null) }
                }
            }
        }
    }
    private fun changePassword() {
        val s = uiState.value

        if (s.currentPassword.isBlank()) {
            setState { copy(error = "Enter current password") }
            return
        }
        if (s.newPassword.isBlank() || s.confirmPassword.isBlank()) {
            setState { copy(error = "Enter new password and confirm it") }
            return
        }
        if (s.newPassword != s.confirmPassword) {
            setState { copy(error = "Passwords do not match") }
            return
        }

        handleResponse(
            apiCall = { changePasswordUseCase(s.currentPassword, s.newPassword) },
            onLoading = { setState { copy(isLoading = true, error = null) } },
            onSuccess = {
                setState {
                    copy(
                        isLoading = false,
                        error = null,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = ""
                    )
                }
            },
            onError = { msg -> setState { copy(isLoading = false, error = msg) } }
        )


    }
    fun changeUsername() {
        val s = uiState.value
        val username = s.user?.username.orEmpty()

        if (username.isBlank()) {
            setState { copy(error = "Username cannot be empty") }
            return
        }

        handleResponse(
            apiCall = { changeUsernameUseCase(s.username) },
            onLoading = {
                setState { copy(isLoading = true, error = null) }
            },
            onSuccess = {
                setState { copy(isLoading = false, error = null) }
            },
            onError = { msg ->
                setState { copy(isLoading = false, error = msg) }
            }

        )
    }
}