package com.example.mycomposeapp.feature.splash.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.splash.presentation.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashNavGraph(
    onGoDashboard: () -> Unit,
    onGoWelcome: () -> Unit
) {
    composable<SplashRoute> {
        SplashScreen(
            onGoDashboard = onGoDashboard,
            onGoWelcome = onGoWelcome
        )
    }
}
