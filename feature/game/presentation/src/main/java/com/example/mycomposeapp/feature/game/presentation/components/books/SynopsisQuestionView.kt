package com.example.mycomposeapp.feature.game.presentation.components.books

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.GameContract

@Composable
fun SynopsisQuestionView(
    content: QuestionContent.BookSynopsis,
    bookSynopsisState: GameContract.ModeState.BookSynopsis,
    correctAnswer: String,
    isRevealed: Boolean,
    onOptionSelected: (String) -> Unit,
    onUseHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = content.coverImageUrl,
            contentDescription = "Book cover",
            modifier = Modifier
                .size(180.dp, 240.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        Text(
            text = "Which synopsis belongs to this book?",
            style = typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            color = colors.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.spacing12))

        val visibleOptions = content.options.filter {
            it.bookId !in bookSynopsisState.removedOptionIds
        }

        visibleOptions.forEach { option ->
            val isSelected = option.bookId == bookSynopsisState.selectedOptionId
            val isCorrect = option.bookTitle.equals(correctAnswer, ignoreCase = true)
            SynopsisOptionCard(
                option = option,
                isSelected = isSelected,
                isCorrect = isCorrect,
                isRevealed = isRevealed,
                isEnabled = !isRevealed,
                onClick = { onOptionSelected(option.bookTitle) }
            )
            Spacer(modifier = Modifier.height(spacing.spacing8))
        }

        if (!isRevealed) {
            Spacer(modifier = Modifier.height(spacing.spacing8))
            if (bookSynopsisState.showInsufficientFundsWarning) {
                Text(
                    text = "Not enough coins for hint",
                    color = colors.error,
                    style = typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Button(
                onClick = onUseHint,
                enabled = bookSynopsisState.canAffordHint,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black,
                    disabledContainerColor = colors.outline,
                    disabledContentColor = colors.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (bookSynopsisState.isHintUsed) "Hint used" else "Hint (20 coins) — removes 2 wrong options",
                    style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
