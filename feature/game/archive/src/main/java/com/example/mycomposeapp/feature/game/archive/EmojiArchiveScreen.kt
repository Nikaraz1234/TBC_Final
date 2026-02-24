package com.example.mycomposeapp.feature.game.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycomposeapp.core.ui.components.Loader
import com.example.mycomposeapp.core.ui.components.cards.GlassCard
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.feature.game.domain.model.DailyPuzzle
import com.example.mycomposeapp.feature.game.domain.model.QuestionContent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.example.mycomposeapp.feature.game.archive.R as ArchiveR
import com.example.mycomposeapp.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiArchiveScreen(
    onNavigateBack: () -> Unit,
    onPuzzleSelected: (date: String) -> Unit,
    viewModel: EmojiArchiveViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors

    var showDatePicker by remember { mutableStateOf(false) }

    val todayUtcMillis = remember {
        Clock.System.now().toEpochMilliseconds()
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis < todayUtcMillis
            }
        }
    )
    val timeZone = remember { TimeZone.currentSystemDefault() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(ArchiveR.string.btn_back_desc),
                        tint = colors.textLight
                    )
                }

                Text(
                    text = stringResource(ArchiveR.string.emoji_archive_title),
                    color = colors.textLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = CoreUiR.drawable.ic_calendar),
                        contentDescription = "Pick date",
                        tint = colors.textLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    val dateString = Instant
                                        .fromEpochMilliseconds(millis)
                                        .toLocalDateTime(timeZone)
                                        .date
                                        .toString()

                                    onPuzzleSelected(dateString)
                                    showDatePicker = false
                                }
                            }
                        ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            when {
                state.isLoading -> {
                    Loader()
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: stringResource(ArchiveR.string.something_went_wrong),
                            color = colors.textMuted,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }

                state.puzzles.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(ArchiveR.string.no_past_puzzles),
                            color = colors.textMuted,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(4.dp)) }

                        items(state.puzzles, key = { it.date }) { puzzle ->
                            ArchivePuzzleCard(
                                puzzle = puzzle,
                                onClick = { onPuzzleSelected(puzzle.date) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchivePuzzleCard(
    puzzle: DailyPuzzle,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = puzzle.date,
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                when (val c = puzzle.question.content) {
                    is QuestionContent.Emoji -> {
                        Text(
                            text = c.emojiClues,
                            fontSize = 32.sp
                        )
                    }

                    is QuestionContent.BookByOrder -> {
                        Text(
                            text = c.bookTitle,
                            color = colors.textLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${c.genre} • ${c.mainCharacter}",
                            color = colors.textMuted,
                            fontSize = 12.sp
                        )
                    }

                    else -> {
                        Text(
                            text = "Unsupported puzzle type",
                            color = colors.textMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (puzzle.isCompleted) {
                Text(text = "\u2705", fontSize = 24.sp)
            } else {
                Text(
                    text = stringResource(ArchiveR.string.btn_play),
                    color = colors.goldenYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}