package com.example.mycomposeapp.feature.achievements.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.achievements.presentation.AchievementsScreen
import kotlinx.serialization.Serializable

@Serializable
data object AchievementsRoute

fun NavGraphBuilder.achievementsNavGraph() {
    composable<AchievementsRoute> {
        AchievementsScreen()
    }
}
