package com.kirillyemets.catapp.ui.components.swipeablecard

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Enables Tinder like swiping gestures.
 *
 * @param state The current state of the swipeable card. Use [rememberSwipeableCardState] to create.
 * @param onSwipeEnd will be called once a swipe gesture is completed. The given [Direction] will indicate which side the gesture was performed on.
 * @param onSwipeCancel will be called when the gesture is stopped before reaching the minimum threshold to be treated as a full swipe
 * @param blockedDirections the directions which will not trigger a swipe. By default only horizontal swipes are allowed.
 */

fun Modifier.swipableCard(
    key: Any,
    state: SwipeableCardState,
    onSwipeStart: (Direction) -> Unit,
    onSwipeEnd: (Direction) -> Unit,
    onSwipeCancel: () -> Unit = {},
    blockedDirections: List<Direction> = listOf(Direction.Up, Direction.Down),
): Modifier {
    return composed {
        val coroutineScope = rememberCoroutineScope()

        fun a() = coroutineScope.launch {
            val coercedOffset = state.offset.targetValue
                .coerceIn(blockedDirections,
                    maxHeight = state.maxHeight,
                    maxWidth = state.maxWidth)

            if (hasNotTravelledEnough(state, coercedOffset)) {
                state.reset()
                onSwipeCancel()
            } else {
                val horizontalTravel = abs(state.offset.targetValue.x)
                val verticalTravel = abs(state.offset.targetValue.y)

                if (horizontalTravel > verticalTravel) {
                    if (state.offset.targetValue.x > 0) {
                        state.swipe(Direction.Right)
                        onSwipeEnd(Direction.Right)
                    } else {
                        state.swipe(Direction.Left)
                        onSwipeEnd(Direction.Left)
                    }
                } else {
                    if (state.offset.targetValue.y < 0) {
                        onSwipeStart(Direction.Up)
                        state.swipe(Direction.Up)
                        onSwipeEnd(Direction.Up)
                    } else {
                        onSwipeStart(Direction.Down)
                        state.swipe(Direction.Down)
                        onSwipeEnd(Direction.Down)
                    }
                }
            }
        }

        val pi = pointerInput(key) {
            coroutineScope {
                if (!state.isAnimating)
                    detectDragGestures(
                        onDragCancel = {
                            launch {
                                state.reset()
                                onSwipeCancel()
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (!state.isAnimating)
                                launch {
                                    val original = state.offset.targetValue
                                    val summed = original + dragAmount
                                    val newValue = Offset(
                                        x = summed.x.coerceIn(-state.maxWidth, state.maxWidth),
                                        y = summed.y.coerceIn(-state.maxHeight, state.maxHeight)
                                    )
                                    if (change.positionChange() != Offset.Zero) change.consume()
                                    state.drag(newValue.x, newValue.y)
                                }
                        },
                        onDragEnd = {
                            a()
                        }
                    )
            }
        }

        val base = if (state.isAnimating) Modifier else pi
        base.then(
            graphicsLayer {
                translationX = state.offset.value.x
                translationY = state.offset.value.y
                rotationZ = (state.offset.value.x / 60).coerceIn(-40f, 40f)
            }
        )
    }
}

private fun Offset.coerceIn(
    blockedDirections: List<Direction>,
    maxHeight: Float,
    maxWidth: Float,
): Offset {
    return copy(
        x = x.coerceIn(
            if (blockedDirections.contains(Direction.Left)) {
                0f
            } else {
                -maxWidth
            },
            if (blockedDirections.contains(Direction.Right)) {
                0f
            } else {
                maxWidth
            }
        ),
        y = y.coerceIn(if (blockedDirections.contains(Direction.Up)) {
            0f
        } else {
            -maxHeight
        },
            if (blockedDirections.contains(Direction.Down)) {
                0f
            } else {
                maxHeight
            }
        )
    )
}

private fun hasNotTravelledEnough(
    state: SwipeableCardState,
    offset: Offset,
): Boolean {
    return abs(offset.x) < state.maxWidth / 8 &&
        abs(offset.y) < state.maxHeight / 8
}