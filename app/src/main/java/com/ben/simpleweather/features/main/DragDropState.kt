package com.ben.simpleweather.features.main

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import androidx.compose.ui.geometry.Offset

class DragDropState(
    private val draggableItemsNum: Int,
    private val stateList: LazyListState,
    private val onMove: (Int, Int) -> Unit,
) {
    var draggingItemIndex: Int? by mutableStateOf(null)
    var delta by mutableFloatStateOf(0f)
    val scrollChannel = Channel<Float>()

    private var draggingItem: LazyListItemInfo? = null

    internal fun onDragStart(offset: Offset) {
        stateList.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                draggingItem = it
                draggingItemIndex = it.index
            }
    }

    internal fun onDragInterrupted() {
        draggingItem = null
        draggingItemIndex = null
        delta = 0f
    }

    internal fun onDrag(offset: Offset) {
        delta += offset.y

        val currentDraggingItemIndex = draggingItemIndex ?: return
        val currentDraggingItem = draggingItem ?: return

        val startOffset = currentDraggingItem.offset + delta
        val endOffset = currentDraggingItem.offset + currentDraggingItem.size + delta
        val middleOffset = startOffset + (endOffset - startOffset) / 2

        val targetItem = stateList.layoutInfo.visibleItemsInfo.find { item ->
            middleOffset.toInt() in item.offset..(item.offset + item.size) &&
                    currentDraggingItem.index != item.index
        }

        if (targetItem != null) {
            val targetIndex = targetItem.index
            onMove(currentDraggingItemIndex, targetIndex)
            draggingItemIndex = targetIndex
            delta += currentDraggingItem.offset - targetItem.offset
            draggingItem = targetItem
        } else {
            val startOffsetToTop = startOffset - stateList.layoutInfo.viewportStartOffset
            val endOffsetToBottom = endOffset - stateList.layoutInfo.viewportEndOffset
            val scroll = when {
                startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                else -> 0f
            }

            if (scroll != 0f && currentDraggingItemIndex != 0 && currentDraggingItemIndex != draggableItemsNum - 1) {
                scrollChannel.trySend(scroll)
            }
        }
    }
}
