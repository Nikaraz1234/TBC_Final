package com.example.mycomposeapp.feature.game.presentation.components.books

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.GameContract

@Composable
fun OddOneOutQuestionView(
    content: QuestionContent.BookOddOneOut,
    bookOddOneOutState: GameContract.ModeState.BookOddOneOut,
    correctAnswer: String,
    isRevealed: Boolean,
    onBookSelected: (String) -> Unit,
    onUseHint: () -> Unit,
    onUseCategoryHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Tap the odd one out!",
            style = typography.titleSmall.copy(fontWeight = FontWeight.Medium),
            color = colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.spacing12))

        val visibleBooks = content.books.filter { it.id !in bookOddOneOutState.removedBookIds }
        val rows = visibleBooks.chunked(2)

        rows.forEach { rowBooks ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowBooks.forEach { book ->
                    val isSelected = book.id == bookOddOneOutState.selectedBookId
                    val isCorrect = book.title.equals(correctAnswer, ignoreCase = true)
                    OddOneOutBookCard(
                        book = book,
                        isSelected = isSelected,
                        isCorrect = isCorrect,
                        isRevealed = isRevealed,
                        isEnabled = !isRevealed,
                        onClick = { onBookSelected(book.title) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                // Fill empty slot if odd number
                if (rowBooks.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isRevealed) {
            Spacer(modifier = Modifier.height(spacing.spacing8))
            OddOneOutRevealCard(
                traitDisplayName = content.traitDisplayName,
                sharedValue = content.sharedValue,
                isCorrect = bookOddOneOutState.selectedBookId?.let { selId ->
                    content.books.find { it.id == selId }?.title?.equals(correctAnswer, ignoreCase = true)
                } ?: false
            )
        } else {
            Spacer(modifier = Modifier.height(spacing.spacing8))
            if (bookOddOneOutState.showInsufficientFundsWarning) {
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
                enabled = bookOddOneOutState.canAffordHint,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color.Black,
                    disabledContainerColor = colors.outline,
                    disabledContentColor = colors.onSurface.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (bookOddOneOutState.isHintUsed) "Hint used" else "Hint (20 coins) — removes 2 shared books",
                    style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (bookOddOneOutState.isCategoryHintUsed) {
                Text(
                    text = "Category: ${content.traitDisplayName} — ${content.sharedValue}",
                    color = Color(0xFFFFD700),
                    style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Button(
                    onClick = onUseCategoryHint,
                    enabled = bookOddOneOutState.canAffordCategoryHint,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black,
                        disabledContainerColor = colors.outline,
                        disabledContentColor = colors.onSurface.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Category hint (15 coins) — reveals shared trait",
                        style = typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
