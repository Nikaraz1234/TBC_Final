package com.example.mycomposeapp.ui.screen.splash

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
        data class NavigateTo(val route: Any) : SideEffect
    }
}
