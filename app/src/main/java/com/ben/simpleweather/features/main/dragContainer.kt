package com.ben.simpleweather.features.main

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.dragContainer(dragDropState: DragDropState): Modifier = this.then(
    Modifier.pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                dragDropState.onDragStart(offset)
            },
            onDrag = { change, offset ->
                change.consume()
                dragDropState.onDrag(offset)
            },
            onDragEnd = {
                dragDropState.onDragInterrupted()
            },
            onDragCancel = {
                dragDropState.onDragInterrupted()
            }
        )
    }
)
