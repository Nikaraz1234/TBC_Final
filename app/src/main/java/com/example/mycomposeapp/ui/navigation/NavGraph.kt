package com.example.mycomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.feature.login.presentation.navigation.LoginRoute
import com.example.mycomposeapp.feature.login.presentation.navigation.loginNavGraph
import com.example.mycomposeapp.feature.main.presentation.navigation.MainRoute
import com.example.mycomposeapp.feature.main.presentation.navigation.mainNavGraph
import com.example.mycomposeapp.feature.register.presentation.navigation.RegisterRoute
import com.example.mycomposeapp.feature.register.presentation.navigation.registerNavGraph
import com.example.mycomposeapp.feature.splash.presentation.navigation.SplashRoute
import com.example.mycomposeapp.feature.splash.presentation.navigation.splashNavGraph
import com.example.mycomposeapp.feature.welcome.presentation.navigation.WelcomeRoute
import com.example.mycomposeapp.feature.welcome.presentation.navigation.welcomeNavGraph

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        splashNavGraph(
            onGoDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onGoWelcome = {
                navController.navigate(WelcomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )

        welcomeNavGraph(
            onNavigateToLogin = { navController.navigate(LoginRoute) },
            onNavigateToRegister = { navController.navigate(RegisterRoute) },
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )

        loginNavGraph(
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRoute)
            }
        )

        registerNavGraph(
            onNavigateToDashboard = {
                navController.navigate(MainRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )

        mainNavGraph(
            onNavigateToGame = { gameModeId, categoryType ->
                // TODO: Navigate to game screen
            },
            onNavigateToProfile = {
                // TODO: Navigate to profile screen
            }
        )
    }
}
