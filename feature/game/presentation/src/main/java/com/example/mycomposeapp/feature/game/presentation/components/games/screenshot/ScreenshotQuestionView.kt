package com.example.mycomposeapp.feature.game.presentation.components.games.screenshot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.LoaderImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.game.domain.constants.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.components.common.HintRow
import com.example.mycomposeapp.feature.game.presentation.components.common.HintsCard
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun ScreenshotQuestionView(
    modifier: Modifier = Modifier,
    content: QuestionContent.Screenshot,
    guessesRemaining: Int = 3,
    isHintUsed: Boolean = false,
    coins: Int = 0,
    canAffordHint: Boolean = false,
    hintStep: Int = 0,
    hintCost: Int = GameConstants.EMOJI_HINT_COST,
    onUseHint: () -> Unit = {},
    studioHint: String = "",
    genreHint: String = "",
    yearHint: String = "",
    showInsufficientFunds: Boolean = false,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val typography = AppTheme.typography

    val canShowHintButton = hintStep < 3
    val canBuyHint = coins >= hintCost

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LoaderImage(
            imageUrl = content.imageUrl,
            contentDescription = "Screenshot",
            modifier = Modifier
                .padding(horizontal = spacing.spacing16)
        )

        Spacer(modifier = Modifier.height(spacing.spacing16))

        HintsCard(
            rows = buildList {
                if (hintStep >= 1) add(HintRow("Studio", studioHint))
                if (hintStep >= 2) add(HintRow("Genres", genreHint))
                if (hintStep >= 3) add(HintRow("Release Year", yearHint))
            }
        )

        if (canShowHintButton) {
            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonLarge(
                text = stringResource(GameR.string.hint_cost_format, hintCost),
                onClick = onUseHint,
                style = ButtonStyle.Outlined,
                enabled = canBuyHint,
                modifier = Modifier.padding(horizontal = spacing.spacing16)
            )

            if (showInsufficientFunds) {
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

@Preview(showBackground = true)
@Composable
private fun ScreenshotQuestionViewPreview() {
    MyComposeAppTheme {
        ScreenshotQuestionView(
            content = QuestionContent.Screenshot(
                imageUrl = "https://images.igdb.com/igdb/image/upload/t_1080p/co1r7f.jpg",
            ),
            guessesRemaining = 2,
            isHintUsed = false,
            coins = 5,
            canAffordHint = true,
            onUseHint = {}
        )
    }
}
