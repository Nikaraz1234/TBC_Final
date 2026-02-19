package com.example.mycomposeapp.feature.game.presentation.components.rankle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.ArrowDirection
import com.example.mycomposeapp.feature.game.domain.model.FeedbackColor
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.domain.model.RankleGuess
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.R

@Composable
fun RankleQuestionView(
    content: QuestionContent.Rankle,
    rankleState: GameContract.ModeState.Rankle,
    isRevealed: Boolean,
    onAnswerSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    var showHelpDialog by remember { mutableStateOf(false) }
    var inputText by remember(rankleState.mangaTitle) { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    fun validateAndSubmit() {
        val value = inputText.trim().toDoubleOrNull()
        when {
            inputText.isBlank() -> inputError = "Please enter a rating"
            value == null -> inputError = "Enter a valid number"
            value !in 0.0..10.0 -> inputError = "Must be between 0 and 10"
            else -> {
                inputError = null
                focusManager.clearFocus()
                onAnswerSubmit(inputText.trim())
                inputText = ""
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cover
        Box(
            modifier = Modifier
                .size(width = 140.dp, height = 200.dp)
                .clip(AppTheme.radius.radius16)
                .background(colors.backgroundDark),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = rankleState.mangaImageUrl,
                contentDescription = stringResource(R.string.Manga),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = rankleState.mangaTitle,
            color = colors.textLight,
            style = typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input + Submit (only when round is active)
        if (!rankleState.isRoundOver) {
            AppTextField(
                value = inputText,
                onValueChange = { new ->
                    val filtered = new.filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) inputText = filtered
                    inputError = null
                },
                label = "Enter rating (0–10)",
                error = inputError,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { validateAndSubmit() },
                enabled = inputText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.goldenYellow,
                    contentColor = Color.Black,
                    disabledContainerColor = colors.goldenYellow.copy(alpha = 0.3f),
                    disabledContentColor = Color.Black.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Submit Guess",
                    color = Color.Black,
                    style = typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Help button
        TextButton(onClick = { showHelpDialog = true }) {
            Text(
                text = "?",
                color = colors.textMuted,
                style = typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Guess history
        if (rankleState.guesses.isNotEmpty()) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    rankleState.guesses.forEach { guess -> GuessRow(guess) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Result card
        if (rankleState.isRoundOver && rankleState.actualRating != null) {
            val won = rankleState.guesses.lastOrNull()?.color == FeedbackColor.GREEN

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (won) {
                        val coinsEarned = 6 - rankleState.guesses.size

                        Text(
                            text = "🎉 Correct!",
                            color = Color(0xFF4CAF50),
                            style = typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "+$coinsEarned coins!",
                            color = colors.goldenYellow,
                            style = typography.titleSmall
                        )
                    } else {
                        Text(
                            text = "Out of guesses!",
                            color = Color(0xFFE53935),
                            style = typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.the_answer_was),
                        color = colors.textMuted,
                        style = typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "%.2f".format(rankleState.actualRating),
                        color = colors.goldenYellow,
                        style = typography.headlineLarge
                    )
                }
            }
        }
    }

    if (showHelpDialog) {
        RankleHelpDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
private fun GuessRow(guess: RankleGuess, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val bg = guess.color.toComposeColor()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg.copy(alpha = 0.2f))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "%.2f".format(guess.input),
            color = colors.textLight,
            style = typography.titleSmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                if (guess.color == FeedbackColor.GREEN) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        style = typography.labelLarge
                    )
                }
            }

            when (guess.direction) {
                ArrowDirection.UP -> Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Higher",
                    tint = colors.textLight,
                    modifier = Modifier.size(20.dp)
                )

                ArrowDirection.DOWN -> Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Lower",
                    tint = colors.textLight,
                    modifier = Modifier.size(20.dp)
                )

                ArrowDirection.NONE -> Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun RankleHelpDialog(onDismiss: () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "How to Play Rankle",
                color = colors.textLight,
                style = typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "Guess the anime's rating! You have 5 tries.",
                    color = colors.textLight,
                    style = typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Color Feedback:",
                    color = colors.textLight,
                    style = typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))
                HelpColorRow("Green", "Within 0.02 — You Win!", Color(0xFF4CAF50))
                HelpColorRow("Red", "Within 0.1", Color(0xFFE53935))
                HelpColorRow("Orange", "Within 0.2", Color(0xFFFF9800))
                HelpColorRow("Yellow", "Within 0.5", Color(0xFFFDD835))
                HelpColorRow("White", "Within 1.0", Color(0xFFE0E0E0))
                HelpColorRow("Gray", "More than 1.0 away", Color(0xFF757575))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Arrows show if the actual rating is higher ⬆ or lower ⬇.",
                    color = colors.textLight,
                    style = typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Coins: 5 for 1st try, 4 for 2nd, 3 for 3rd, 2 for 4th, 1 for 5th.",
                    color = colors.goldenYellow,
                    style = typography.labelLarge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got it!",
                    color = colors.goldenYellow,
                    style = typography.labelLarge
                )
            }
        },
        containerColor = colors.backgroundDark,
        tonalElevation = 8.dp
    )
}

@Composable
private fun HelpColorRow(label: String, description: String, color: Color) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: $description",
            color = colors.textLight,
            style = typography.labelMedium
        )
    }
}

private fun FeedbackColor.toComposeColor(): Color = when (this) {
    FeedbackColor.GREEN -> Color(0xFF4CAF50)
    FeedbackColor.RED -> Color(0xFFE53935)
    FeedbackColor.ORANGE -> Color(0xFFFF9800)
    FeedbackColor.YELLOW -> Color(0xFFFDD835)
    FeedbackColor.WHITE -> Color(0xFFE0E0E0)
    FeedbackColor.GRAY -> Color(0xFF757575)
}
