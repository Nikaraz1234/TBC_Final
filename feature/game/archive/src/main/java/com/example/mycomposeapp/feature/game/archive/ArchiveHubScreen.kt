package com.example.mycomposeapp.feature.game.archive

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.feature.game.archive.R as ArchiveR
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing

@Composable
fun ArchiveHubScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEmojiArchive: (categoryType: String) -> Unit
) {
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
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
                text = stringResource(ArchiveR.string.archive_title),
                color = colors.textLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(spacing.spacing8))

        ArchiveCategoryCard(
            emoji = "\uD83C\uDFAC",
            title = "Movie",
            description = stringResource(ArchiveR.string.emoji_puzzles_desc),
            onClick = { onNavigateToEmojiArchive("MOVIES") },
            modifier = Modifier.padding(horizontal = spacing.spacing16)
        )
        Spacer(modifier = Modifier.height(spacing.spacing16))
        ArchiveCategoryCard(
            emoji = "\uD83C\uDFAC",
            title = "Manga",
            description = stringResource(ArchiveR.string.emoji_puzzles_desc_manga),
            onClick = { onNavigateToEmojiArchive("COMICS") },
            modifier = Modifier.padding(horizontal = spacing.spacing16)
        )
        Spacer(modifier = Modifier.height(spacing.spacing16))
        ArchiveCategoryCard(
            emoji = "\uD83C\uDFAC",
            title = "Books",
            description = stringResource(ArchiveR.string.order_puzzles_desc_books),
            onClick = { onNavigateToEmojiArchive("BOOKS") },
            modifier = Modifier.padding(horizontal = spacing.spacing16)
        )
    }
}

@Composable
private fun ArchiveCategoryCard(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = colors.textLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = colors.textMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}
