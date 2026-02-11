package com.example.mycomposeapp.feature.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginNavGraph(
    onNavigateToDashboard: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    composable<LoginRoute> {
        LoginScreen(
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToRegister = onNavigateToRegister
        )
    }
}
