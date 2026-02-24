package com.example.mycomposeapp.feature.splash.presentation

object SplashContract {

    data class State(
        val progress: Float = 0f,
        val isLoading: Boolean = true,
    )

    sealed interface Event {
        data object OnEnter : Event
        data class OnProgressChanged(val progress: Float) : Event
        data object OnLoadingFinished : Event
    }

    sealed interface SideEffect {
        data object GoDashboard : SideEffect
        data object GoWelcome : SideEffect

    }
}
