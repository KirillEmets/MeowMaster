package com.kirillyemets.catapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.kirillyemets.catapp.R
import com.kirillyemets.catapp.ui.components.FullScreenLoadingOverlay
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = getViewModel(),
    navigateToPetFinder: (id: String?) -> Unit,
    navigateToLikedAnimals: () -> Unit,
    navigateToBreeds: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val exploreThumbnailEdl = state.exploreImageThumbnail

    Scaffold(
        containerColor = Color.Transparent
    ) {
        BoxWithConstraints() {
            val maxHeight = this.maxHeight
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(0.dp, maxHeight / 2)
                        .fillMaxWidth(0.9f)
                        .aspectRatio(4 / 3f, true)
                ) {
                    Text(
                        text = "Explore",
                        style = MaterialTheme.typography.titleMedium
                    )

                    CardWithBottomText(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .card3dEffect()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                        label = {
                            Text(
                                text = "Find new cats",
                                style = MaterialTheme.typography.headlineMedium,
                                color = LocalTextStyle.current.color
                            )
                        },
                        onClick = {
                            navigateToPetFinder(exploreThumbnailEdl.data?.animalId)
                            viewModel.onExploreCardClick()
                        }
                    ) {
                        when {
                            exploreThumbnailEdl.data != null -> {
                                SubcomposeAsyncImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    model = exploreThumbnailEdl.data.imageUrl,
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center,
                                    contentDescription = "TODO",
                                    loading = {
                                        FullScreenLoadingOverlay(Color.Transparent)
                                    }
                                )
                            }

                            exploreThumbnailEdl.loading -> {
                                FullScreenLoadingOverlay(Color.Transparent)
                            }
                        }
                    }
                }

                Column(Modifier.fillMaxWidth(0.9f)) {
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        val cardModifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .aspectRatio(1f, true)

                        CardWithBottomText(
                            modifier = cardModifier
                                .padding(end = 8.dp)
                                .card3dEffect()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),

                            onClick = {
                                navigateToBreeds()
                                viewModel.onBreedsCardClick()

                            },
                            label = {
                                Text(text = "Breeds")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                model = state.breedsImageUrl,
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                contentDescription = "Breeds"
                            )
                        }

                        CardWithBottomText(
                            modifier = cardModifier
                                .padding(start = 8.dp)
                                .card3dEffect()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            onClick = {
                                navigateToLikedAnimals()
                                viewModel.onLikedCardClick()
                            },
                            label = {
                                Text(text = "Liked")
                            }
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                model = state.likedImageUrl,
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                contentDescription = "Liked",
                                error = painterResource(id = R.drawable.ic_liked_cat_placeholder),
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardWithBottomText(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit) = {},
    label: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier, onClick = onClick) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(0.7f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                ProvideTextStyle(value = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onPrimary)) {
                    label()
                }
            }
        }
    }
}

fun Modifier.card3dEffect() = drawBehind {
    drawRoundRect(
        color = Color.Gray,
        topLeft = Offset(24f, -8f),
        size = Size(size.width - 48f, size.height),
        cornerRadius = CornerRadius(16f, 16f)
    )
}