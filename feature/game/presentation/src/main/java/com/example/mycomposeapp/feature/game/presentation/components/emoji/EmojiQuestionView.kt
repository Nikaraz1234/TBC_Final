package com.example.mycomposeapp.feature.game.presentation.components.emoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.components.display.HeartIndicator
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent

@Composable
fun EmojiQuestionView(
    content: QuestionContent.Emoji,
    guessesRemaining: Int = 3,
    isHintUsed: Boolean = false,
    hintText: String = "",
    hintLabel: String = "",
    coins: Int = 0,
    canAffordHint: Boolean = false,
    isFromArchive: Boolean = false,
    onUseHint: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (content.isDaily && !isFromArchive) {
            Text(
                text = stringResource(GameR.string.daily_puzzle_header),
                color = colors.goldenYellow,
                style = typography.labelSmall.copy(letterSpacing = 2.sp)
            )
        } else if (isFromArchive) {
            Text(
                text = stringResource(GameR.string.archive_date_header, content.date),
                color = colors.textMuted,
                style = typography.labelSmall.copy(letterSpacing = 2.sp)
            )
        }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = spacing.spacing16)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(GameR.string.guess_emoji_instruction),
                    color = colors.textMuted,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                // Emoji clues: keep big size, but still via typography base
                Text(
                    text = content.emojiClues,
                    style = typography.displayLarge.copy(fontSize = 48.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                HeartIndicator(
                    remaining = guessesRemaining,
                    fontSize = typography.titleMedium.fontSize
                )

                Spacer(modifier = Modifier.height(spacing.spacing8))

                Text(
                    text = stringResource(
                        GameR.string.guesses_remaining_format,
                        guessesRemaining,
                        stringResource(
                            if (guessesRemaining == 1) GameR.string.guess_singular
                            else GameR.string.guesses_plural
                        )
                    ),
                    color = colors.textMuted,
                    style = typography.labelMedium
                )

                // Hint section
                if (isHintUsed && hintText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(spacing.spacing16))

                    Text(
                        text = hintLabel,
                        color = colors.textMuted,
                        style = typography.labelSmall
                    )

                    Text(
                        text = hintText,
                        color = colors.goldenYellow,
                        style = typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                } else if (hintText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(spacing.spacing16))

                    ButtonLarge(
                        text = stringResource(
                            GameR.string.hint_cost_format,
                            GameConstants.EMOJI_HINT_COST
                        ),
                        onClick = onUseHint,
                        style = ButtonStyle.Outlined,
                        enabled = canAffordHint,
                        modifier = Modifier.padding(horizontal = spacing.spacing16)
                    )

                    if (!canAffordHint && coins < GameConstants.EMOJI_HINT_COST) {
                        Text(
                            text = stringResource(GameR.string.not_enough_coins),
                            color = colors.error,
                            style = typography.labelSmall.copy(fontSize = 11.sp),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
