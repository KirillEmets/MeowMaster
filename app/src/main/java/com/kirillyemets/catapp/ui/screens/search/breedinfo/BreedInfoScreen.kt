package com.kirillyemets.catapp.ui.screens.search.breedinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.ui.components.AnimalGalleryItem
import com.kirillyemets.catapp.ui.components.animalsGalleryGridCells
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedInfoScreen(
    viewModel: BreedInfoScreenViewModel = getViewModel(),
    navigateToImageViewer: (breedName: String, id: String, list: List<UnknownAnimalThumbnail>) -> Unit,
    navigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val breedInfoEdl = state.breedInfoEdl
    val imageUrlEdl = state.referenceImageUrl

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = breedInfoEdl.data?.name.orEmpty())
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateBack()
                        },
                        content = {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when {
            breedInfoEdl.data != null -> {
                val data = breedInfoEdl.data
                val fullRow: LazyGridItemSpanScope.() -> GridItemSpan = { GridItemSpan(3) }

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    columns = GridCells.Fixed(3),
                    content = {
                        item(span = fullRow) {
                            Column {
                                when {
                                    imageUrlEdl.data != null -> {
                                        AsyncImage(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            model = imageUrlEdl.data,
                                            contentDescription = "TODO",
                                            contentScale = ContentScale.FillWidth
                                        )
                                    }

                                    imageUrlEdl.loading -> CircularProgressIndicator()
                                    else -> Unit
                                }

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )
                            }
                        }

                        data.description?.let { description ->
                            item(span = fullRow) {
                                Column {
                                    Text(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        text = description
                                    )

                                    Divider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }

                        item(span = fullRow) {
                            BreedDetails(
                                modifier = Modifier.padding(vertical = 8.dp),
                                origin = data.origin,
                                lifeSpan = data.lifeSpan,
                                temperament = data.temperament,
                                weightInfo = data.weightInfo
                            )
                        }

                        val animals = state.breedImages.data.orEmpty().map {
                            AnimalGalleryItem(it.animalId, it.imageUrl)
                        }

                        if (animals.isNotEmpty()) {
                            item(span = fullRow) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                        text = "All ${data.name} images: "
                                    )
                                }
                            }

                            animalsGalleryGridCells(
                                animals = animals,
                                onAnimalCardClick = { id ->
                                    navigateToImageViewer(
                                        state.breedInfoEdl.data?.name.orEmpty(),
                                        id,
                                        state.breedImages.data.orEmpty()
                                            .map { it.toAnimalThumbnail() })

                                    viewModel.onGalleryCardClicked()
                                }
                            )
                        }

                        if (state.breedImages.loading) {
                            item(span = fullRow) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BreedDetails(
    modifier: Modifier = Modifier,
    origin: String?,
    lifeSpan: String?,
    temperament: String?,
    weightInfo: WeightInfoUIState?
) {
    Column(modifier = modifier) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge.copy(lineHeight = 20.sp)) {
            origin?.let { origin ->
                Text(text = "Origin: $origin")
            }

            lifeSpan?.let { lifeSpan ->
                Text(text = "Life span: $lifeSpan")
            }

            weightInfo?.let { weightInfo ->
                Text(text = "Weight: ${weightInfo.imperial} lbs, ${weightInfo.metric} kg")
            }
            temperament?.let { temperament ->
                Text(text = "Temperament: $temperament")
            }
        }
    }
}