package com.example.mycomposeapp.feature.game.presentation.components.manga

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.LoaderImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.comics.MangaItem
import com.example.mycomposeapp.feature.game.presentation.GameContract

private val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun MangaRatingQuestionView(
    mangaRatingState: GameContract.ModeState.MangaRating,
    isRevealed: Boolean,
    onMangaSelected: (Long) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pair = mangaRatingState.currentPair ?: return

    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(spacing.spacing8))

        Text(
            text = "Which manga is rated higher?",
            color = colors.textLight,
            style = typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        MangaCard(
            manga = pair.mangaA,
            isRevealed = isRevealed,
            isSelected = mangaRatingState.selectedId == pair.mangaA.id,
            isWinner = isRevealed && pair.mangaA.rating >= pair.mangaB.rating,
            isLoser = isRevealed && pair.mangaA.rating < pair.mangaB.rating,
            onClick = { if (!isRevealed) onMangaSelected(pair.mangaA.id) }
        )

        Spacer(modifier = Modifier.height(spacing.spacing12))

        Text(
            text = "VS",
            color = colors.goldenYellow,
            style = typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.spacing12))

        MangaCard(
            manga = pair.mangaB,
            isRevealed = isRevealed,
            isSelected = mangaRatingState.selectedId == pair.mangaB.id,
            isWinner = isRevealed && pair.mangaB.rating >= pair.mangaA.rating,
            isLoser = isRevealed && pair.mangaB.rating < pair.mangaA.rating,
            onClick = { if (!isRevealed) onMangaSelected(pair.mangaB.id) }
        )

        if (isRevealed) {
            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = if (mangaRatingState.isAnswerCorrect) "Correct!" else "Wrong!",
                color = if (mangaRatingState.isAnswerCorrect) SuccessGreen else colors.error,
                style = typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonLarge(
                text = if (mangaRatingState.isAnswerCorrect) "Next" else "See Results",
                onClick = onNext,
                style = ButtonStyle.Filled
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MangaCard(
    manga: MangaItem,
    isRevealed: Boolean,
    isSelected: Boolean,
    isWinner: Boolean,
    isLoser: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    val borderColor by animateColorAsState(
        targetValue = when {
            isWinner -> SuccessGreen
            isLoser -> colors.error
            isSelected -> colors.goldenYellow
            else -> Color.White.copy(alpha = 0.2f)
        },
        label = "borderColor"
    )

    val borderWidth = if (isRevealed || isSelected) 3.dp else 1.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isRevealed, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.spacing16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoaderImage(
                imageUrl = manga.imageUrl,
                contentDescription = manga.title,
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(spacing.spacing12))

            Text(
                text = manga.title,
                color = colors.textLight,
                style = typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isRevealed) {
                Text(
                    text = "Rating: ${manga.rating}",
                    color = if (isWinner) SuccessGreen else colors.error,
                    style = typography.headlineSmall.copy(fontSize = 20.sp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Tap to select",
                    color = colors.textMuted,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
