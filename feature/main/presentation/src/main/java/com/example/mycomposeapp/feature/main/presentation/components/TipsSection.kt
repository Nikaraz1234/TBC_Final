package com.example.mycomposeapp.feature.main.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.main.presentation.model.Tip
import com.example.mycomposeapp.feature.main.presentation.model.Tips
import kotlinx.coroutines.delay

@Composable
fun TipsSection(
    startIndex: Int,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val tips = Tips.all
    val pagerState = rememberPagerState(
        initialPage = startIndex % tips.size,
        pageCount = { tips.size }
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % tips.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "TIPS",
            color = colors.textMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            TipCard(tip = tips[page])
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            tips.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) colors.goldenYellow
                            else colors.textMuted.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
private fun TipCard(
    tip: Tip,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val shape = AppTheme.radius.radius12

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush = colors.glassGradient, shape = shape)
            .border(1.dp, colors.glassBorder, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tip.emoji,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.padding(start = 12.dp))

        Text(
            text = tip.text,
            color = colors.textMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}
