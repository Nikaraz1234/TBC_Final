package com.example.mycomposeapp.feature.game.presentation.components.books

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.feature.game.domain.model.BookEvent
import com.example.mycomposeapp.feature.game.presentation.GameContract

@Composable
fun BookByOrderQuestionView(
    state: GameContract.ModeState.BookByOrder,
    onMove: (eventId: Int, slotIndex: Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val typo = AppTheme.typography

    val slotBounds = remember { Array(6) { Rect.Zero } }

    var draggingId by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var dragStartCenter by remember { mutableStateOf<Offset?>(null) }

    val byId = remember(state.allEvents) { state.allEvents.associateBy { it.eventId } }
    val canSubmit = state.slots.all { it != null }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.spacing16),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.bookTitle,
            style = typo.headlineMedium,
            color = colors.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(spacing.spacing12))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(6) { slotIndex ->
                val id = state.slots[slotIndex]
                val txt = id?.let { byId[it]?.text }.orEmpty()

                SlotCard(
                    slotIndex = slotIndex,
                    text = txt,
                    isFilled = id != null,
                    onBounds = { rect -> slotBounds[slotIndex] = rect },

                    draggableEventId = id,
                    draggingId = draggingId,
                    dragOffset = dragOffset,
                    onDragStartCenter = { center ->
                        draggingId = id
                        dragOffset = Offset.Zero
                        dragStartCenter = center
                    },
                    onDrag = { amt -> dragOffset += amt },
                    onDragEnd = {
                        val start = dragStartCenter
                        val dId = draggingId
                        if (start != null && dId != null) {
                            val dropPoint = start + dragOffset
                            val target = slotBounds.indexOfFirst { it.contains(dropPoint) }
                            if (target >= 0) onMove(dId, target)
                        }
                        draggingId = null
                        dragOffset = Offset.Zero
                        dragStartCenter = null
                    },
                    onDragCancel = {
                        draggingId = null
                        dragOffset = Offset.Zero
                        dragStartCenter = null
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Events",
            style = typo.titleMedium,
            color = colors.onBackground
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.pool.forEach { ev ->
                PoolCard(
                    event = ev,
                    draggingId = draggingId,
                    dragOffset = dragOffset,
                    onDragStartCenter = { center ->
                        draggingId = ev.eventId
                        dragOffset = Offset.Zero
                        dragStartCenter = center
                    },
                    onDrag = { amt -> dragOffset += amt },
                    onDragEnd = {
                        val start = dragStartCenter
                        val dId = draggingId
                        if (start != null && dId != null) {
                            val dropPoint = start + dragOffset
                            val target = slotBounds.indexOfFirst { it.contains(dropPoint) }
                            if (target >= 0) onMove(dId, target)
                        }
                        draggingId = null
                        dragOffset = Offset.Zero
                        dragStartCenter = null
                    },
                    onDragCancel = {
                        draggingId = null
                        dragOffset = Offset.Zero
                        dragStartCenter = null
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.goldenYellow,
                contentColor = colors.onPrimary,
                disabledContainerColor = colors.goldenYellowDark.copy(alpha = 0.45f),
                disabledContentColor = colors.onPrimary.copy(alpha = 0.55f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Submit",
                style = typo.titleMedium
            )
        }
    }
}

@Composable
private fun SlotCard(
    slotIndex: Int,
    text: String,
    isFilled: Boolean,
    onBounds: (Rect) -> Unit,

    draggableEventId: Int?,
    draggingId: Int?,
    dragOffset: Offset,
    onDragStartCenter: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    val colors = AppTheme.colors
    val typo = AppTheme.typography

    var centerInRoot by remember { mutableStateOf<Offset?>(null) }

    val shape = RoundedCornerShape(12.dp)

    val slotBg =
        if (isFilled) colors.glassWhiteLight else colors.glassWhiteDark

    val borderColor =
        if (draggableEventId != null) colors.goldenYellowDark.copy(alpha = 0.55f)
        else colors.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(1.dp, borderColor, shape)
            .background(slotBg, shape)
            .onGloballyPositioned { coords ->
                val b = coords.boundsInRoot()
                onBounds(b)
                centerInRoot = Offset(b.center.x, b.center.y)
            }
            .pointerInput(draggableEventId) {
                if (draggableEventId == null) return@pointerInput
                detectDragGestures(
                    onDragStart = {
                        val c = centerInRoot ?: return@detectDragGestures
                        onDragStartCenter(c)
                    },
                    onDrag = { change, amt ->
                        change.consume()
                        onDrag(amt)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Slot ${slotIndex + 1}",
                style = typo.labelMedium,
                color = colors.textMuted
            )

            Text(
                text = if (!isFilled) "Drop here" else text,
                style = typo.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (!isFilled) colors.textMuted else colors.onBackground
            )
        }
    }
}

@Composable
private fun PoolCard(
    event: BookEvent,
    draggingId: Int?,
    dragOffset: Offset,
    onDragStartCenter: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit
) {
    val colors = AppTheme.colors
    val typo = AppTheme.typography

    var centerInRoot by remember { mutableStateOf<Offset?>(null) }

    val isDragging = draggingId == event.eventId
    val shape = RoundedCornerShape(14.dp)

    val bg = if (isDragging) colors.glassWhite else colors.glassWhiteLight
    val border = if (isDragging) colors.goldenYellow else colors.outline

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                val b = coords.boundsInRoot()
                centerInRoot = Offset(b.center.x, b.center.y)
            }
            .offset {
                if (isDragging) {
                    IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt())
                } else IntOffset.Zero
            }
            .pointerInput(event.eventId) {
                detectDragGestures(
                    onDragStart = {
                        val c = centerInRoot ?: return@detectDragGestures
                        onDragStartCenter(c)
                    },
                    onDrag = { change, amt ->
                        change.consume()
                        onDrag(amt)
                    },
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragCancel
                )
            }
            .border(1.dp, border, shape)
            .background(bg, shape)
            .padding(12.dp)
    ) {
        Text(
            text = event.text,
            style = typo.bodyMedium,
            color = colors.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}