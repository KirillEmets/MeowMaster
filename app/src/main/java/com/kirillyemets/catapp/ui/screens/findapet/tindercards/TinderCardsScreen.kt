package com.kirillyemets.catapp.ui.screens.findapet.tindercards

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kirillyemets.catapp.R
import com.kirillyemets.catapp.ui.components.ExploreAnimalInfoActionBar
import com.kirillyemets.catapp.ui.components.FullScreenLoadingOverlay
import com.kirillyemets.catapp.ui.components.swipeablecard.Direction
import com.kirillyemets.catapp.ui.components.swipeablecard.SwipeableCardStack
import com.kirillyemets.catapp.ui.screens.animalinfo.AnimalInfoModalBottomSheet
import com.kirillyemets.catapp.ui.theme.CoralPink
import org.koin.androidx.compose.getViewModel

@Composable
fun TinderCardsScreen(
    viewModel: TinderCardsScreenViewModel = getViewModel(),
    navigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {

        }
    ) { paddingValues ->
        val cards = state.cards ?: emptyList()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

            TinderCardsScreenContent(
                loading = state.showLoading,
                cards = cards,
                currentCardIndex = state.currentCardIndex,
                actionButtonsEnabled = state.actionButtonsEnabled,
                showFinishMessage = state.showFinishMessage,
                onBackClick = {
                    navigateBack()
                    viewModel.onBackClick()
                },
                onDownloadClick = { animalId ->
                    viewModel.downloadImage(context, animalId)
                },
                onShareClick = { animalId ->
                    viewModel.shareImage(context, animalId)
                },
                onShowInfoClick = {
                    viewModel.onShowInfoClick()
                },
                onItemSwipe = { i, direction ->
                    if (cards.isEmpty()) return@TinderCardsScreenContent

                    when (direction) {
                        Direction.Up, Direction.Right -> viewModel.actionLike(animalId = cards[i].animalId)
                        else -> viewModel.actionDislike()
                    }
                },
            )
        }
    }
}

@Composable
fun TinderCardsScreenContent(
    loading: Boolean,
    cards: List<AnimalCardUIState>,
    currentCardIndex: Int,
    actionButtonsEnabled: Boolean,
    showFinishMessage: Boolean,
    onBackClick: () -> Unit,
    onDownloadClick: (animalId: String) -> Unit,
    onShareClick: (animalId: String) -> Unit,
    onShowInfoClick: () -> Unit,
    onItemSwipe: (i: Int, direction: Direction) -> Unit,
) {
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    val swipeDirectionState: MutableState<SingleValue<Direction?>> =
        remember { mutableStateOf(SingleValue(null)) }

    TwoItemLayout(
        Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AnimatedContent(targetState = showFinishMessage, transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(),
                    initialContentExit = fadeOut(),
                    sizeTransform = null
                )
            }) { showFinishMessage ->
                val modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(3 / 4f, true)

                when (showFinishMessage) {
                    false -> {
                        SwipeableCardStack(
                            modifier = modifier,
                            count = cards.size,
                            currentItemIndex = currentCardIndex,
                            swipeToDirection = swipeDirectionState,
                            onItemSwipeStart = { i, d ->

                            },
                            onItemSwipeFinish = { i, d ->
                                onItemSwipe(i, d)
                            }
                        ) { it ->
                            val card = cards[it]
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = if (card.bitmap == null && it == 0) card.imageUrl else card.bitmap,
                                    contentDescription = "Cat image"
                                )
                            }
                        }

                        if (loading) {
                            Box(modifier = modifier) {
                                FullScreenLoadingOverlay(Color.Transparent)
                            }
                        }
                    }

                    true -> Box(modifier = modifier.background(Color.Cyan)) {

                    }
                }
            }
        }

        val alpha by animateFloatAsState(targetValue = if (showFinishMessage) 0f else 1f)
        Row(
            modifier = Modifier
                .alpha(alpha)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedIconButton(
                onClick = onBackClick,
                content = {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedIconButton(
                modifier = Modifier.size(64.dp),
                enabled = actionButtonsEnabled,
                onClick = {
                    if (swipeDirectionState.value.value == null)
                        swipeDirectionState.value = SingleValue(Direction.Down)
                },
                content = {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.round_close_24),
                        contentDescription = "Dislike"
                    )
                },
                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = CoralPink)
            )

            Spacer(modifier = Modifier.width(32.dp))

            OutlinedIconButton(
                modifier = Modifier.size(64.dp),
                enabled = actionButtonsEnabled,
                onClick = {
                    if (swipeDirectionState.value.value == null)
                        swipeDirectionState.value = SingleValue(Direction.Up)
                },
                content = {
                    Icon(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = R.drawable.outline_favorite_24),
                        contentDescription = "Like"
                    )
                },
                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = Color.Green)
            )

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedIconButton(
                enabled = actionButtonsEnabled,
                onClick = {
                    onShowInfoClick()
                    showBottomSheet = true
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_info_24),
                        contentDescription = "Info"
                    )
                }
            )
        }
    }

    if (currentCardIndex < cards.size && showBottomSheet) {
        val animalId = cards[currentCardIndex].animalId
        AnimalInfoModalBottomSheet(
            animalId = animalId,
            actionBar = {
                ExploreAnimalInfoActionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    onDownloadButtonClick = {
                        onDownloadClick(animalId)
                    },
                    onShareButtonClick = {
                        onShareClick(animalId)
                    },
                    onLikeButtonClick = {
                        showBottomSheet = false
                        swipeDirectionState.value = SingleValue(Direction.Up)
                    }
                )
            },
            onDismiss = {
                showBottomSheet = false
            }
        )
    }
}

@Composable
@Suppress("unused")
private fun TwoItemLayout(
    modifier: Modifier,
    padding: Dp = 32.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val paddingInPx = with(density) { padding.roundToPx() }
    paddingInPx.hashCode()

    Layout(modifier = modifier, content = content) { measurables, constraints ->

        require(measurables.size == 2)

        val bottomPlaceable = measurables.last().measure(constraints.copy(minHeight = 0, minWidth = 0))
        val maxHeight = constraints.maxHeight - bottomPlaceable.height * 2
        val topPlaceable = measurables.first().measure(constraints.copy(minWidth = 0, minHeight = 0, maxHeight = maxHeight))
        val height = topPlaceable.height

        val totalHeight = constraints.maxHeight
        val verticalCenter = totalHeight / 2

        val lolPadding = if (topPlaceable.height == maxHeight) bottomPlaceable.height else 0

        layout(constraints.minWidth, totalHeight) {
            topPlaceable.placeRelative(
                0,
                verticalCenter - height / 2 - lolPadding
            )
            bottomPlaceable.placeRelative(
                0,
                verticalCenter + height / 2 + (totalHeight - height) / 4 - bottomPlaceable.height / 2 - lolPadding / 2
            )
        }
    }
}

class SingleValue<T>(val value: T)