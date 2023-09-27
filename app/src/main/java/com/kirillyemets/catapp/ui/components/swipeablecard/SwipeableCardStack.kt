package com.kirillyemets.catapp.ui.components.swipeablecard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kirillyemets.catapp.ui.screens.findapet.tindercards.SingleValue
import kotlinx.coroutines.launch

@Composable
fun SwipeableCardStack(
    modifier: Modifier = Modifier,
    count: Int,
    currentItemIndex: Int,
    swipeToDirection: MutableState<SingleValue<Direction?>> = mutableStateOf(SingleValue(null)),
    onItemSwipeStart: (i: Int, direction: Direction) -> Unit,
    onItemSwipeFinish: (i: Int, direction: Direction) -> Unit,
    itemContent: @Composable() (ColumnScope.(i: Int) -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()

    val firstCardIndex = currentItemIndex + currentItemIndex % 2
    val secondCardIndex = currentItemIndex + 1 - currentItemIndex % 2

    (0..1).forEach { i ->
        val state = rememberSwipeableCardState()

        val index = if (i == 0) firstCardIndex else secondCardIndex

        if (!(firstCardIndex >= count && i == 0) && !(secondCardIndex >= count && i == 1)) {
            LaunchedEffect(key1 = swipeToDirection.value, block = {
                if ((currentItemIndex + i) % 2 == 0) {
                    when (swipeToDirection.value.value) {
                        Direction.Up -> {
                            onItemSwipeStart(index, Direction.Up)
                            state.swipe(Direction.Up)
                            onItemSwipeFinish(index, Direction.Up)
                            state.offset.snapTo(Offset.Zero)
                        }

                        Direction.Down -> {
                            onItemSwipeStart(index, Direction.Down)
                            state.swipe(Direction.Down)
                            onItemSwipeFinish(index, Direction.Down)
                            state.offset.snapTo(Offset.Zero)
                        }

                        Direction.Left -> {
                            onItemSwipeStart(index, Direction.Left)
                            state.swipe(Direction.Left)
                            onItemSwipeFinish(index, Direction.Left)
                            state.offset.snapTo(Offset.Zero)
                        }

                        Direction.Right -> {
                            onItemSwipeStart(index, Direction.Right)
                            state.swipe(Direction.Right)
                            onItemSwipeFinish(index, Direction.Right)
                            state.offset.snapTo(Offset.Zero)
                        }

                        else -> Unit
                    }
                    swipeToDirection.value = SingleValue(null)
                }
            })


            Card(
                modifier = modifier
                    .zIndex(
                        (if (currentItemIndex % 2 == 0) 1 - i else i).toFloat()
                    ).graphicsLayer()

                    .swipableCard(
//                        key(index),
                        Unit,
                        state,
                        onSwipeStart = { direction ->
                            onItemSwipeStart(index, direction)
                        },
                        onSwipeEnd = { direction ->
                            coroutineScope.launch {
                                onItemSwipeFinish(index, direction)
                                state.offset.snapTo(Offset.Zero)
                            }
                        },
                        blockedDirections = listOf()
                    )
                    .border(1.dp, color = Color.LightGray, MaterialTheme.shapes.small)

            ) {
                itemContent(index)
            }
        }
    }
}