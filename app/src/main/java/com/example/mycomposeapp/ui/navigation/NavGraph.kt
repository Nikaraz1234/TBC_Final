package com.example.mycomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.feature.login.navigation.LoginRoute
import com.example.mycomposeapp.feature.login.navigation.loginNavGraph
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation.EditProfileRoute
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation.editProfileNavGraph
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.navigation.profileNavGraph
import com.example.mycomposeapp.feature.profile.presentation.navigation.profileNavGraph
import com.example.mycomposeapp.feature.register.navigation.RegisterRoute
import com.example.mycomposeapp.feature.register.navigation.registerNavGraph
import com.example.mycomposeapp.feature.splash.presentation.navigation.SplashRoute
import com.example.mycomposeapp.feature.splash.presentation.navigation.splashNavGraph
import com.example.mycomposeapp.feature.welcome.navigation.WelcomeRoute
import com.example.mycomposeapp.feature.welcome.navigation.welcomeNavGraph
import com.example.mycomposeapp.ui.screen.dashboard.navigation.DashboardRoute
import com.example.mycomposeapp.ui.screen.dashboard.navigation.dashboardNavGraph

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        splashNavGraph(
            onGoDashboard = {
                navController.navigate(DashboardRoute) {
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
            onLoginClick = { navController.navigate(LoginRoute) },
            onRegisterClick = { navController.navigate(RegisterRoute) },
            onGoogleSignInClick = { /* TODO */ },
            onTermsClick = { /* TODO */ }
        )

        loginNavGraph(
            onNavigateToDashboard = {
                navController.navigate(DashboardRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(RegisterRoute)
            }
        )

        registerNavGraph(
            onNavigateToDashboard = {
                navController.navigate(DashboardRoute) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            }
        )

        dashboardNavGraph()

        profileNavGraph(
            onNavigateToEdit = { navController.navigate(EditProfileRoute) },
        )
        editProfileNavGraph(
            onBack = {navController.popBackStack()}
        )

    }
}
