package com.example.mycomposeapp.ui.screen.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginNavGraph() {
    composable<LoginRoute> {
        // TODO: LoginScreen()
    }
}
