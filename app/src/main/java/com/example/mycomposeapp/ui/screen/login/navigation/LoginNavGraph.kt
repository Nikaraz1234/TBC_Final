package com.example.mycomposeapp.ui.screen.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.ui.screen.login.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginNavGraph(navController: NavController) {
    composable<LoginRoute> {
        LoginScreen(navController = navController)
    }
}
