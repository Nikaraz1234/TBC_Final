package com.example.mycomposeapp.feature.splash.presentation

import androidx.lifecycle.viewModelScope
import com.example.mycomposeapp.core.domain.keys.PreferenceKeys
import com.example.mycomposeapp.core.domain.usecase.datastore.GetPreferenceUseCase
import com.example.mycomposeapp.core.presentation.common.BaseViewModel
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
            setState { SplashContract.State(isLoading = true, progress = 0f) }

            val token = getPreferenceUseCase(
                key = PreferenceKeys.TOKEN,
                defaultValue = ""
            ).first()

            progress += PROGRESS_STEPS[0]
            setState { SplashContract.State(progress = progress) }

            val isLoggedIn = token.isNotBlank()

            getPreferenceUseCase(
                key = PreferenceKeys.DARK_MODE,
                defaultValue = false
            ).first()

            delay(STEP_DELAY_MS)

            progress += PROGRESS_STEPS[1]
            setState { SplashContract.State(progress = progress) }

            delay(STEP_DELAY_MS)
            progress += PROGRESS_STEPS[2]
            setState { SplashContract.State(progress = progress) }

            delay(STEP_DELAY_MS)
            progress += PROGRESS_STEPS[3]
            setState { SplashContract.State(progress = progress, isLoading = false) }

            sendSideEffect(
                if (isLoggedIn) SplashContract.SideEffect.GoDashboard
                else SplashContract.SideEffect.GoWelcome
            )
        }
    }


    private fun updateProgress(progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        setState { SplashContract.State(progress = clamped) }

        if (clamped >= 1f) {
            finishSplash()
        }
    }

    private fun finishSplash() {
        if (!uiState.value.isLoading) return

        splashJob?.cancel()
        splashJob = null

        setState { SplashContract.State(progress = 1f, isLoading = false) }
    }

    private companion object {
        const val STEP_DELAY_MS = 300L
        val PROGRESS_STEPS = listOf(0.2f, 0.2f, 0.3f, 0.3f)
    }
}
