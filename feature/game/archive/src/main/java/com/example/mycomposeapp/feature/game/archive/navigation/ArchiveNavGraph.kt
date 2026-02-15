package com.example.mycomposeapp.feature.game.archive.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mycomposeapp.feature.game.archive.ArchiveHubScreen
import com.example.mycomposeapp.feature.game.archive.EmojiArchiveScreen
import kotlinx.serialization.Serializable

@Serializable
data object ArchiveHubRoute

@Serializable
data class EmojiArchiveRoute(
    val categoryType: String
)

fun NavGraphBuilder.archiveNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToEmojiArchive: (categoryType: String) -> Unit,
    onNavigateToEmojiGame: (categoryType: String, archiveDate: String) -> Unit = { _, _ -> }
) {
    composable<ArchiveHubRoute> {
        ArchiveHubScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToEmojiArchive = { categoryType ->
                onNavigateToEmojiArchive(categoryType)
            }
        )
    }
    composable<EmojiArchiveRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<EmojiArchiveRoute>()

        EmojiArchiveScreen(
            onNavigateBack = onNavigateBack,
            onPuzzleSelected = { date -> onNavigateToEmojiGame(route.categoryType, date) }
        )
    }

}
