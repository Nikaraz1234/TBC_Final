package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun AnswerFeedbackView(
    isCorrect: Boolean,
    correctAnswer: String,
    userAnswer: String?,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    val feedbackColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
    val feedbackText = if (isCorrect) {
        stringResource(GameR.string.feedback_correct)
    } else {
        stringResource(GameR.string.feedback_wrong)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = feedbackText,
            color = feedbackColor,
            style = typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        if (!isCorrect) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(GameR.string.the_answer_was),
                color = colors.textMuted,
                style = typography.bodyMedium
            )

            Text(
                text = correctAnswer,
                color = colors.goldenYellow,
                style = typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center
            )
        }
    }
}
