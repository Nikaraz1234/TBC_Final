package com.example.mycomposeapp.feature.login.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.login.presentation.LoginScreen
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
