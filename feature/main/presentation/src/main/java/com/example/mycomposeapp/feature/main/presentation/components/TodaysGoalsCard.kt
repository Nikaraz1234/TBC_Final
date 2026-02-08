package com.example.mycomposeapp.feature.main.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycomposeapp.core.domain.model.DailyGoal
import com.example.mycomposeapp.core.domain.model.DailyGoalsProgress
import com.example.mycomposeapp.core.ui.theme.AppTheme

@Composable
fun TodaysGoalsCard(
    goalsProgress: DailyGoalsProgress,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val shape = AppTheme.radius.radius16

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(brush = colors.glassGradient, shape = shape)
            .border(1.dp, colors.glassBorder, shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TODAY'S GOALS",
                color = colors.textMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${goalsProgress.completedCount}/${goalsProgress.totalCount}",
                color = colors.goldenYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val progress = if (goalsProgress.totalCount > 0)
            goalsProgress.completedCount.toFloat() / goalsProgress.totalCount
        else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(AppTheme.radius.radius8),
            color = colors.goldenYellow,
            trackColor = colors.glassWhite,
        )

        Spacer(modifier = Modifier.height(12.dp))

        goalsProgress.goals.forEach { goal ->
            GoalItem(goal = goal)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GoalItem(
    goal: DailyGoal,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (goal.isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(1.5.dp, colors.textMuted, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = goal.title,
            color = if (goal.isCompleted) colors.textMuted else colors.textLight,
            fontSize = 14.sp,
            textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        if (goal.targetProgress > 1) {
            Text(
                text = "${goal.currentProgress}/${goal.targetProgress}",
                color = if (goal.isCompleted) Color(0xFF4CAF50) else colors.textMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
