package com.ben.simpleweather.features.main

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

@OptIn(ExperimentalFoundationApi::class)
inline fun <T : Any> LazyListScope.draggableItems(
    items: List<T>,
    dragDropState: DragDropState,
    crossinline content: @Composable (Modifier, T) -> Unit,
) {
    itemsIndexed(
        items = items,
        contentType = { index, _ -> DraggableItem() }
    ) { index, item ->
        val modifier = if (dragDropState.draggingItemIndex == index) {
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationY = dragDropState.delta  // 누적값 아닌 절대 이동 거리만 반영
                }
        } else {
            Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = tween(
                durationMillis = 500,
                easing = LinearOutSlowInEasing
            )
            )
        }
        Box(modifier = modifier) {
            content(modifier, item)
        }
    }
}

class DraggableItem()
