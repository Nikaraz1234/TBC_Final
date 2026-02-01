package com.example.mycomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.mycomposeapp.ui.screen.dashboard.navigation.dashboardNavGraph
import com.example.mycomposeapp.ui.screen.login.navigation.LoginRoute
import com.example.mycomposeapp.ui.screen.login.navigation.loginNavGraph
import com.example.mycomposeapp.ui.screen.register.navigation.RegisterRoute
import com.example.mycomposeapp.ui.screen.register.navigation.registerNavGraph
import com.example.mycomposeapp.ui.screen.splash.navigation.SplashRoute
import com.example.mycomposeapp.ui.screen.splash.navigation.splashNavGraph
import com.example.mycomposeapp.ui.screen.welcome.navigation.WelcomeRoute
import com.example.mycomposeapp.ui.screen.welcome.navigation.welcomeNavGraph

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        splashNavGraph(navController = navController)
        welcomeNavGraph(
            onLoginClick = { navController.navigate(LoginRoute) },
            onRegisterClick = { navController.navigate(RegisterRoute) },
            onGoogleSignInClick = { /* TODO */ },
            onTermsClick = { /* TODO */ }
        )
        loginNavGraph()
        registerNavGraph()
        dashboardNavGraph()
    }
}
