package com.example.mycomposeapp.feature.game.presentation.components.games.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.components.display.HeartIndicator
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun ScreenshotQuestionView(
    modifier: Modifier = Modifier,
    content: QuestionContent.Screenshot,
    guessesRemaining: Int = 3,
    isHintUsed: Boolean = false,
    hintText: String = "",
    coins: Int = 0,
    canAffordHint: Boolean = false,
    isFromArchive: Boolean = false,
    onUseHint: () -> Unit = {}
) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                AsyncImage(
                    model = content.imageUrl,
                    contentDescription = "Screen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

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
            hintText = "Rockstar Games",
            coins = 5,
            canAffordHint = true,
            isFromArchive = false,
            onUseHint = {}
        )
    }
}