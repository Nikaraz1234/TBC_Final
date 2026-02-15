package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.components.buttons.ButtonLarge
import com.example.mycomposeapp.feature.game.presentation.GameContract
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun NextButton(
    phase: GameContract.GamePhase = GameContract.GamePhase.Playing,
    isLastQuestion: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String? = null
) {
    ButtonLarge(
        text = buttonText ?: if (isLastQuestion) stringResource(GameR.string.btn_see_results) else stringResource(GameR.string.btn_next_question),
        onClick = onClick,
        modifier = modifier.padding(horizontal = 16.dp)
    )
}
