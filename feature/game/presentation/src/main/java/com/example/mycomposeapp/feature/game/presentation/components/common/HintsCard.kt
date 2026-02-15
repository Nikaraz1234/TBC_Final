package com.example.mycomposeapp.feature.game.presentation.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing

@Immutable
data class HintRow(val label: String, val value: String)

@Composable
fun HintsCard(
    modifier: Modifier = Modifier,
    rows: List<HintRow>
) {
    val colors = AppTheme.colors

    if (rows.isEmpty()) return

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing16,)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(spacing.spacing16)) {
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = row.label,
                        color = colors.textMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(0.38f)
                    )
                    Text(
                        text = row.value,
                        color = colors.goldenYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.62f)
                    )
                }

                if (index != rows.lastIndex) {
                    Spacer(Modifier.height(spacing.spacing12))
                    HorizontalDivider(color = colors.glassWhite.copy(alpha = 0.25f))
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}
