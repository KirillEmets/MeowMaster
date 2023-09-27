package com.kirillyemets.catapp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimalsGalleryGrid(
    animals: List<AnimalGalleryItem>,
    modifier: Modifier = Modifier,
    onAnimalCardClick: (id: String) -> Unit
) {
    LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(3), content = {
        items(animals, key = { animal -> animal.animalId }) { animal ->
            AnimalGalleryCard(
                modifier = Modifier
                    .animateItemPlacement()
                    .fillMaxWidth()
                    .padding(4.dp)
                    .aspectRatio(1f),
                url = animal.imageUrl,
                onClick = {
                    onAnimalCardClick(animal.animalId)
                }
            )
        }
    })
}

fun LazyGridScope.animalsGalleryGridCells(
    animals: List<AnimalGalleryItem>,
    onAnimalCardClick: (id: String) -> Unit
) {
    items(
        items = animals,
        key = { item -> item.animalId }
    ) { animal ->
        AnimalGalleryCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            url = animal.imageUrl,
            onClick = {
                onAnimalCardClick(animal.animalId)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalGalleryCard(
    modifier: Modifier = Modifier,
    url: String,
    onClick: () -> Unit
) {
    Card(modifier = modifier, onClick = onClick) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = url,
            contentDescription = "Liked Animal",
            loading = {
                FullScreenLoadingOverlay(Color.Transparent)
            },
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

class AnimalGalleryItem(
    val animalId: String,
    val imageUrl: String
)