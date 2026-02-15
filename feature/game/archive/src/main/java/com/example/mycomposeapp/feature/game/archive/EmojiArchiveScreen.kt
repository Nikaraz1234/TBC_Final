package com.example.mycomposeapp.feature.game.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycomposeapp.feature.game.archive.R as ArchiveR
import com.example.mycomposeapp.core.ui.components.Loader
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent

@Composable
fun EmojiArchiveScreen(
    onNavigateBack: () -> Unit,
    onPuzzleSelected: (date: String) -> Unit,
    viewModel: EmojiArchiveViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = colors.backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(ArchiveR.string.btn_back_desc),
                        tint = colors.textLight
                    )
                }
                Text(
                    text = stringResource(ArchiveR.string.emoji_archive_title),
                    color = colors.textLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            when {
                state.isLoading -> {
                    Loader()
                }
                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: stringResource(ArchiveR.string.something_went_wrong),
                            color = colors.textMuted,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
                state.puzzles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(ArchiveR.string.no_past_puzzles),
                            color = colors.textMuted,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }
                        items(state.puzzles, key = { it.date }) { puzzle ->
                            ArchivePuzzleCard(
                                puzzle = puzzle,
                                onClick = { onPuzzleSelected(puzzle.date) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivePuzzleCard(
    puzzle: DailyPuzzle,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    val emojiContent = puzzle.question.content as? QuestionContent.Emoji

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = puzzle.date,
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = emojiContent?.emojiClues ?: "",
                    fontSize = 32.sp
                )
            }

            if (puzzle.isCompleted) {
                Text(
                    text = "\u2705",
                    fontSize = 24.sp
                )
            } else {
                Text(
                    text = stringResource(ArchiveR.string.btn_play),
                    color = colors.goldenYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
