package com.example.mycomposeapp.ui.screen.splash

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.domain.keys.PreferenceKeys
import com.example.mycomposeapp.domain.usecase.datastore.GetPreferenceUseCase
import com.example.mycomposeapp.ui.common.BaseViewModel
import com.example.mycomposeapp.ui.screen.dashboard.navigation.DashboardRoute
import com.example.mycomposeapp.ui.screen.welcome.navigation.WelcomeRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getPreferenceUseCase: GetPreferenceUseCase
) : BaseViewModel<
        SplashContract.State,
        SplashContract.SideEffect,
        SplashContract.Event
        >(
    initialState = SplashContract.State()
) {

    private var splashJob: Job? = null

    fun onEvent(event: SplashContract.Event) {
        when (event) {
            SplashContract.Event.OnEnter -> startRealInitialization()
            is SplashContract.Event.OnProgressChanged -> updateProgress(event.progress)
            SplashContract.Event.OnLoadingFinished -> finishSplash()
        }
    }

    private fun startRealInitialization() {
        viewModelScope.launch {
            var progress = 0f
            setState { copy(isLoading = true, progress = 0f) }

            val token = getPreferenceUseCase(
                key = PreferenceKeys.TOKEN,
                defaultValue = ""
            ).first()

            progress += 0.2f
            setState { copy(progress = progress) }

            val isLoggedIn = token.isNotBlank()

            getPreferenceUseCase(
                key = PreferenceKeys.DARK_MODE,
                defaultValue = false
            ).first()

            delay(300)

            progress += 0.2f
            setState { copy(progress = progress) }


            delay(300)
            progress += 0.3f
            setState { copy(progress = progress) }


            delay(300)
            progress += 0.3f
            setState { copy(progress = progress, isLoading = false) }

            val destination = if (isLoggedIn) {
                DashboardRoute
            } else {
                WelcomeRoute
            }

            sendSideEffect(
                SplashContract.SideEffect.NavigateTo(destination)
            )
        }
    }


    private fun updateProgress(progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        setState { copy(progress = clamped) }

        if (clamped >= 1f) {
            finishSplash()
        }
    }

    private fun finishSplash() {
        if (!uiState.value.isLoading) return

        splashJob?.cancel()
        splashJob = null

        setState { copy(progress = 1f, isLoading = false) }
        sendSideEffect(SplashContract.SideEffect.NavigateTo(DashboardRoute))
    }
}
