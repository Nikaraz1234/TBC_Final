package com.example.mycomposeapp.ui.screen.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.mycomposeapp.ui.screen.splash.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashNavGraph(
    navController: NavHostController
) {
    composable<SplashRoute> {
        SplashScreen(navController = navController)
    }
}
