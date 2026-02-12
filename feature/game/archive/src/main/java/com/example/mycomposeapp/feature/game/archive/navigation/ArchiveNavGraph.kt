package com.example.mycomposeapp.feature.game.archive.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mycomposeapp.feature.game.archive.ArchiveHubScreen
import com.example.mycomposeapp.feature.game.archive.EmojiArchiveScreen
import kotlinx.serialization.Serializable

@Serializable
data object ArchiveHubRoute

@Serializable
data object EmojiArchiveRoute

fun NavGraphBuilder.archiveNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToEmojiArchive: () -> Unit = {},
    onNavigateToEmojiGame: (archiveDate: String) -> Unit = {}
) {
    composable<ArchiveHubRoute> {
        ArchiveHubScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToEmojiArchive = onNavigateToEmojiArchive
        )
    }
    composable<EmojiArchiveRoute> {
        EmojiArchiveScreen(
            onNavigateBack = onNavigateBack,
            onPuzzleSelected = { date -> onNavigateToEmojiGame(date) }
        )
    }
}
