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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
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
    coins: Int = 0,
    canAffordHint: Boolean = false,
    isFromArchive: Boolean = false,
    onUseHint: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (content.isDaily && !isFromArchive) {
            Text(
                text = stringResource(GameR.string.daily_puzzle_header),
                color = colors.goldenYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        } else if (isFromArchive) {
            Text(
                text = stringResource(GameR.string.archive_date_header, content.date),
                color = colors.textMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(GameR.string.guess_emoji_instruction),
                    color = colors.textMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = content.emojiClues,
                    fontSize = 48.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                HeartIndicator(
                    remaining = guessesRemaining,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(
                        GameR.string.guesses_remaining_format,
                        guessesRemaining,
                        stringResource(if (guessesRemaining == 1) GameR.string.guess_singular else GameR.string.guesses_plural)
                    ),
                    color = colors.textMuted,
                    fontSize = 13.sp
                )

                // Hint section
                if (isHintUsed && hintText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(GameR.string.lead_actor_label),
                        color = colors.textMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = hintText,
                        color = colors.goldenYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                } else if (hintText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonLarge(
                        text = stringResource(GameR.string.hint_cost_format, GameConstants.EMOJI_HINT_COST),
                        onClick = onUseHint,
                        style = ButtonStyle.Outlined,
                        enabled = canAffordHint,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    if (!canAffordHint && coins < GameConstants.EMOJI_HINT_COST) {
                        Text(
                            text = stringResource(GameR.string.not_enough_coins),
                            color = colors.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
