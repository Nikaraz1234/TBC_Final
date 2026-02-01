package com.example.mycomposeapp.ui.screen.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavGraphBuilder.dashboardNavGraph() {
    composable<DashboardRoute> {
        // TODO: DashboardScreen()
    }
}
