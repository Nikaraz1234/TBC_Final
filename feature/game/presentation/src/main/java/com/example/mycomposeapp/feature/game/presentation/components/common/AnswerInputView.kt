 package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.SearchResult
import com.example.mycomposeapp.feature.game.presentation.R as GameR

@Composable
fun AnswerInputView(
    userAnswer: String,
    onAnswerChanged: (String) -> Unit,
    onSubmit: (String) -> Unit,
    searchResults: List<SearchResult>,
    isSearching: Boolean,
    onSuggestionSelected: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        AppTextField(
            value = userAnswer,
            onValueChange = onAnswerChanged,
            label = stringResource(GameR.string.answer_placeholder),
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = { onSubmit(userAnswer) }
            ),
            enabled = enabled
        )

        if (searchResults.isNotEmpty() && enabled) {
            Spacer(modifier = Modifier.height(4.dp))
            SuggestionDropdown(
                suggestions = searchResults,
                onSuggestionClick = onSuggestionSelected
            )
        }

        if (isSearching && enabled) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(GameR.string.searching),
                color = colors.textMuted,
                style = typography.labelSmall
            )
        }
    }
}
