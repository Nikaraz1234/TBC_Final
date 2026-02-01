package com.example.mycomposeapp.ui.screen.register.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object RegisterRoute

fun NavGraphBuilder.registerNavGraph() {
    composable<RegisterRoute> {
        // TODO: RegisterScreen()
    }
}
