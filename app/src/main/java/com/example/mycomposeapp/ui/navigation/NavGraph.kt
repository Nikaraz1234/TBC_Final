package com.example.mycomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.feature.splash.presentation.navigation.SplashRoute
import com.example.mycomposeapp.feature.splash.presentation.navigation.splashNavGraph
import com.example.mycomposeapp.feature.welcome.navigation.WelcomeRoute
import com.example.mycomposeapp.feature.welcome.navigation.welcomeNavGraph
import com.example.mycomposeapp.ui.screen.dashboard.navigation.DashboardRoute
import com.example.mycomposeapp.ui.screen.dashboard.navigation.dashboardNavGraph
import com.example.mycomposeapp.ui.screen.login.navigation.LoginRoute
import com.example.mycomposeapp.ui.screen.login.navigation.loginNavGraph
import com.example.mycomposeapp.ui.screen.register.navigation.RegisterRoute
import com.example.mycomposeapp.ui.screen.register.navigation.registerNavGraph

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
        loginNavGraph(navController = navController)
        registerNavGraph(navController = navController)
        dashboardNavGraph()
    }
}
