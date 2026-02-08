package com.example.mycomposeapp.feature.register.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.register.presentation.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute

fun NavGraphBuilder.registerNavGraph(
    onNavigateToDashboard: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    composable<RegisterRoute> {
        RegisterScreen(
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToLogin = onNavigateToLogin
        )
    }
}
