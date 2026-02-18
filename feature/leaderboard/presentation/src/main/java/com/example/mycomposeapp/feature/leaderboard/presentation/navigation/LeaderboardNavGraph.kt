package com.example.mycomposeapp.feature.leaderboard.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.leaderboard.presentation.LeaderboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object LeaderboardRoute

fun NavGraphBuilder.leaderboardNavGraph() {
    composable<LeaderboardRoute> {
        LeaderboardScreen()
    }
}