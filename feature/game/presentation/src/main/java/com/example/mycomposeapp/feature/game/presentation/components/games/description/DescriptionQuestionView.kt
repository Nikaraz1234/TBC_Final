package com.example.mycomposeapp.feature.game.presentation.components.games.description

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.presentation.components.common.HintRow
import com.example.mycomposeapp.feature.game.presentation.components.common.HintsCard
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent

@Composable
fun DescriptionQuestionView(
    modifier: Modifier = Modifier,
    content: QuestionContent.Description,
    guessesRemaining: Int = 3,
    coins: Int = 0,
    hintStep: Int = 0,
    hintCost: Int = GameConstants.EMOJI_HINT_COST,
    onUseHint: () -> Unit = {},
    studioHint: String = "",
    genreHint: String = "",
    yearHint: String = "",
    showInsufficientFunds: Boolean = false,
) {
    val colors = AppTheme.colors

    val canShowHintButton = hintStep < 3
    val canBuyHint = coins >= hintCost

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = spacing.spacing16, vertical = spacing.spacing16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spacing16),
                verticalArrangement = Arrangement.Center
            ) {

                val textToShow = content.text.takeIf { it.isNotBlank() }
                    ?: ""

                Text(
                    text = textToShow,
                    color = colors.textLight,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 240.dp)
                        .verticalScroll(rememberScrollState())
                        .clip(RoundedCornerShape(16.dp)),
                    overflow = TextOverflow.Clip
                )
            }
        }

        HintsCard(
            rows = buildList {
                if (hintStep >= 1) add(HintRow("Studio", studioHint))
                if (hintStep >= 2) add(HintRow("Genres", genreHint))
                if (hintStep >= 3) add(HintRow("Release Year", yearHint))
            }
        )

        if (canShowHintButton) {
            Spacer(modifier = Modifier.height(16.dp))

            ButtonLarge(
                text = stringResource(GameR.string.hint_cost_format, hintCost),
                onClick = onUseHint,
                style = ButtonStyle.Outlined,
                enabled = canBuyHint,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (showInsufficientFunds) {
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

@Preview(showBackground = true)
@Composable
private fun DescriptionQuestionViewPreview() {
    MyComposeAppTheme {
        DescriptionQuestionView(
            content = QuestionContent.Description(
                text = "A lone hero travels across a cursed land to recover the ancient artifact before it falls into the wrong hands...",
                studio = null,
                genres = emptyList(),
                releaseYear = null
            ),
            coins = 5,
            hintStep = 0,
            hintCost = 2,
            onUseHint = {}
        )
    }
}
