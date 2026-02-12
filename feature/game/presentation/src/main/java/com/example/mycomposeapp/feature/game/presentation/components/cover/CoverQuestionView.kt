package com.example.mycomposeapp.feature.game.presentation.components.cover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.components.buttons.ButtonSmall
import com.example.mycomposeapp.feature.game.presentation.R as GameR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
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
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        val hiddenCount = GameConstants.COVER_GRID_CELLS - revealedCells.size
        if (hiddenCount > 0) {
            ButtonSmall(
                text = stringResource(GameR.string.reveal_cost_format, revealCost),
                onClick = onRevealMore,
                style = ButtonStyle.Outlined,
                enabled = canAffordReveal,
                modifier = Modifier
                    .padding(horizontal = 80.dp)
                    .alpha(if (canAffordReveal) 1f else 0.5f)
            )
        }
    }
}
