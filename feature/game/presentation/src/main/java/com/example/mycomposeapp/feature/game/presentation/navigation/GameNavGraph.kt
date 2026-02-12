package com.example.mycomposeapp.feature.game.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.game.presentation.GameplayScreen
import kotlinx.serialization.Serializable

@Serializable
data class GameRoute(
    val gameModeId: String,
    val categoryType: String,
    val archiveDate: String? = null
)

fun NavGraphBuilder.gameNavGraph(
    onNavigateBack: () -> Unit
) {
    composable<GameRoute> {
        GameplayScreen(onNavigateBack = onNavigateBack)
    }
}
