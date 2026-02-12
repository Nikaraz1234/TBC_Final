package com.example.mycomposeapp.feature.main.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.main.presentation.MainScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

fun NavGraphBuilder.mainNavGraph(
    onNavigateToGame: (gameModeId: String, categoryType: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToArchive: () -> Unit = {}
) {
    composable<MainRoute> {
        MainScreen(
            onNavigateToGame = onNavigateToGame,
            onNavigateToProfile = onNavigateToProfile,
            onLogout = onLogout,
            onNavigateToArchive = onNavigateToArchive
        )
    }
}
