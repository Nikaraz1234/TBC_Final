package com.example.mycomposeapp.feature.game.presentation.components.plot

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonSmall
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun PlotQuestionView(
    content: QuestionContent.Plot,
    plotState: GameContract.ModeState.Plot,
    isRevealed: Boolean,
    showInsufficientFunds: Boolean,
    onUseHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isRevealed && content.imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(width = 140.dp, height = 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.backgroundDark),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = content.imageUrl,
                    contentDescription = stringResource(GameR.string.movie_poster_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!isRevealed) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                ButtonSmall(
                    text = stringResource(
                        GameR.string.hint_cost_format,
                        GameConstants.PLOT_HINT_COST
                    ),
                    onClick = onUseHint,
                    style = ButtonStyle.Outlined,
                    enabled = !plotState.allHintsRevealed
                )

                if (showInsufficientFunds) {
                    Text(
                        text = stringResource(GameR.string.not_enough_coins),
                        color = colors.error,
                        style = typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(GameR.string.guess_plot_instruction),
                    color = colors.textMuted,
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = content.plotSummary,
                    color = colors.textLight,
                    style = typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (content.hints.isNotEmpty()) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    content.hints.forEachIndexed { index, hint ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = colors.glassWhite,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                        HintRow(
                            label = hint.label,
                            value = hint.value,
                            isVisible = isRevealed || index in plotState.revealedHintIndices
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HintRow(
    label: String,
    value: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = colors.textMuted,
            style = typography.labelMedium,
            modifier = Modifier.width(100.dp)
        )

        Text(
            text = if (isVisible) value else "\u2022 \u2022 \u2022 \u2022 \u2022",
            color = if (isVisible) colors.goldenYellow else colors.textMuted,
            style = if (isVisible) typography.labelLarge else typography.labelMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
