package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.SearchResult

@Composable
fun SuggestionDropdown(
    suggestions: List<SearchResult>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .verticalScroll(rememberScrollState())
            .clip(shape)
            .background(colors.glassWhiteLight, shape)
            .border(1.dp, colors.glassBorder, shape)
    ) {
        suggestions.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(item.title) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.imageUrl != null) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = colors.textLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.subtitle.isNotBlank()) {
                        Text(
                            text = item.subtitle,
                            color = colors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
