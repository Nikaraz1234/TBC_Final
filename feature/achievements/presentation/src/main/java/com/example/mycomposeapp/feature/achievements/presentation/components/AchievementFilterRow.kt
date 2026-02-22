package com.example.mycomposeapp.feature.achievements.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.achievements.domain.model.AchievementCategory
import com.example.mycomposeapp.feature.achievements.presentation.R

@Composable
fun AchievementFilterRow(
    selectedCategory: AchievementCategory?,
    onCategorySelected: (AchievementCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val spacing = AppTheme.spacing

    val allCategories: List<AchievementCategory?> =
        listOf(null) + AchievementCategory.entries.toList()

    val allLabel = stringResource(R.string.achievements_filter_all)

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing.spacing8),
        contentPadding = PaddingValues(horizontal = spacing.spacing16)
    ) {
        items(allCategories) { category ->
            val isSelected = category == selectedCategory
            val label = category?.displayName() ?: allLabel

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = label,
                        style = typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.goldenYellow,
                    selectedLabelColor = colors.black,
                    containerColor = colors.glassWhite,
                    labelColor = colors.textMuted
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = colors.glassBorder,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}
