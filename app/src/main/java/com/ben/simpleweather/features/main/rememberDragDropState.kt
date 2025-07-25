package com.ben.simpleweather.features.main

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit,
    draggableItemsNum: Int
): DragDropState {
    val state = remember(lazyListState) {
        DragDropState(
            stateList = lazyListState,
            onMove = onMove
        )
    }
    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            launch {
                lazyListState.scrollBy(diff)
            }
        }
    }
    return state
}
