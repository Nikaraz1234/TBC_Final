package com.example.mycomposeapp.feature.leaderboard.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.leaderboard.presentation.LeaderboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object LeaderboardRoute

fun NavGraphBuilder.leaderboardNavGraph(
    showSnackBar: (String) -> Unit
) {
    composable<LeaderboardRoute> {
        LeaderboardScreen(
            showSnackBar = showSnackBar
        )
    }
}