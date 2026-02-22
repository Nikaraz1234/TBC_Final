package com.example.mycomposeapp.feature.profile.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.usecase.auth.LogoutUseCase
import com.example.mycomposeapp.core.domain.usecase.datastore.RemovePreferenceUseCase
import com.example.mycomposeapp.core.domain.usecase.user.GetCurrentUserUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
import com.example.mycomposeapp.feature.profile.presentation.ProfileContract.Event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val removePreferenceUseCase: RemovePreferenceUseCase
) : BaseViewModel<ProfileContract.State, ProfileContract.SideEffect, ProfileContract.Event>(
    initialState = ProfileContract.State()
) {

    init {
        observeCurrentUser()
    }

    fun onEvent(event: ProfileContract.Event) {
        when (event) {
            OnSettingsClicked   -> setState { copy(showSettingsSheet = true) }
            OnSettingsDismissed -> setState { copy(showSettingsSheet = false) }
            OnEditProfileClicked -> sendSideEffect(ProfileContract.SideEffect.GoToEditProfile)
            LogoutClicked        -> logout()
            NotificationsClicked -> TODO()
            ToggleDarkTheme      -> TODO()
        }
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .collect { user ->
                    setState {
                        copy(user = user)
                    }
                }
        }
    }

    private fun logout() {
        handleResponse(
            apiCall = { logoutUseCase() },
            onSuccess = {
                sendSideEffect(ProfileContract.SideEffect.GoToWelcomeScreen)
            },
            onError = { message ->
                setState { copy(error = message) }
            },
            onLoading = {
                setState { copy(isLoading = true) }
            }
        )
    }
}
