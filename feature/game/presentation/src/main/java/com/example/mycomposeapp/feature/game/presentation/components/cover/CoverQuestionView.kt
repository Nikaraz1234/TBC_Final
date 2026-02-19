package com.example.mycomposeapp.feature.game.presentation.components.cover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonSmall
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import com.example.mycomposeapp.feature.game.domain.model.GameConstants
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent

@Composable
fun CoverQuestionView(
    content: QuestionContent.Cover,
    revealedCells: Set<Int>,
    coins: Int,
    revealCost: Int,
    canAffordReveal: Boolean,
    onRevealMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spacing32)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = content.imageUrl,
                contentDescription = stringResource(GameR.string.movie_poster_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            CoverGridOverlay(
                revealedCells = revealedCells,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.height(spacing.spacing12))

        val hiddenCount = GameConstants.COVER_GRID_CELLS - revealedCells.size

        if (hiddenCount > 0) {
            ButtonSmall(
                text = stringResource(
                    GameR.string.reveal_cost_format,
                    revealCost
                ),
                onClick = onRevealMore,
                style = ButtonStyle.Outlined,
                enabled = canAffordReveal,
                modifier = Modifier.padding(horizontal = 80.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoverQuestionViewPreview() {

    val fakeContent = QuestionContent.Cover(
        imageUrl = "https://image.tmdb.org/t/p/w500/8UlWHLMpgZm9bx6QYh0NFoq67TZ.jpg"
    )

    MyComposeAppTheme {
        Surface {
            CoverQuestionView(
                content = fakeContent,
                revealedCells = setOf(1, 3, 5, 7),
                coins = 100,
                revealCost = 10,
                canAffordReveal = true,
                onRevealMore = {}
            )
        }
    }
}
