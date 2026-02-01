package com.example.mycomposeapp.ui.screen.register.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.ui.screen.register.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute

fun NavGraphBuilder.registerNavGraph(navController: NavController) {
    composable<RegisterRoute> {
        RegisterScreen(navController = navController)
    }
}
