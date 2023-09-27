package com.kirillyemets.catapp.ui.screens.search

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kirillyemets.catapp.ui.components.FullScreenLoadingOverlay
import com.kirillyemets.catapp.ui.components.dashedBorder
import com.kirillyemets.catapp.ui.screens.home.CardWithBottomText
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedsScreen(
    viewModel: BreedsScreenViewModel = getViewModel(),
    navigateToBreedInfo: (breedId: String) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val breedsEdl = state.breedsEdl

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Breeds")
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->


        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                breedsEdl.data != null -> {
                    val breeds = breedsEdl.data
                    when {
                        breeds.isEmpty() -> {
                            BreedsScreenEmptyState {
                                viewModel.reloadBreeds()
                            }
                        }

                        else -> {
                            LazyLabeledCardsGrid(
                                modifier = Modifier
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                                    .fillMaxSize(),
                                cardModifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                size = breeds.size,
                                onCardClick = {
                                    val id = breeds[it].breedId
                                    navigateToBreedInfo(id)
                                    viewModel.onBreedClicked(id)
                                },
                                content = {
                                    val breed = breeds[it]
                                    val imageEdl = breed.referenceImageInfo

                                    Box(modifier = Modifier.animateContentSize()) {
                                        when {
                                            imageEdl.loading -> {
                                                Box(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1.25f)
                                                ) {
                                                    FullScreenLoadingOverlay(Color.Transparent)
                                                }
                                            }

                                            imageEdl.data != null -> {
                                                AsyncImage(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(imageEdl.data.width.toFloat() / imageEdl.data.height.toFloat())
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    model = imageEdl.data.url,
                                                    contentDescription = breed.name,
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }

                                            else -> Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1f)
                                                    .background(Color.Cyan)
                                            ) {
                                                LaunchedEffect(key1 = Unit, block = {
                                                    Log.d(
                                                        "kek",
                                                        "imageEdl: $imageEdl, error: ${imageEdl.error?.message}"
                                                    )
                                                })
                                            }
                                        }
                                    }
                                },
                                bottomLabel = {
                                    Text(text = breeds[it].name)
                                }
                            )
                        }
                    }
                }

                breedsEdl.loading -> {
                    FullScreenLoadingOverlay(Color.Transparent)
                }

                breedsEdl.isError -> {
                    BreedsScreenEmptyState {
                        viewModel.reloadBreeds()
                    }
                }
            }
        }
    }
}

@Composable
fun BreedsScreenEmptyState(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .dashedBorder(
                2.dp,
                color = MaterialTheme.colorScheme.error,
                cornerRadiusDp = 12.dp
            ),
    ) {
        Column(modifier = Modifier) {
            Text(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                text = "Couldn't load breeds. Please check your internet connection and reload.",
                style = MaterialTheme.typography.titleLarge
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                OutlinedButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { onClick() }
                ) {
                    Text(text = "Reload", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun LazyLabeledCardsGrid(
    size: Int,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    onCardClick: (i: Int) -> Unit,
    content: @Composable (i: Int) -> Unit,
    bottomLabel: @Composable (i: Int) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        content = {
            items(size) { i ->
                CardWithBottomText(
                    modifier = cardModifier,
                    label = { bottomLabel(i) },
                    content = {
                        content(i)
                    },
                    onClick = { onCardClick(i) }
                )
            }
        }
    )
}

