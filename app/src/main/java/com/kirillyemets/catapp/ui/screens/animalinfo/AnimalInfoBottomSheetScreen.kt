package com.kirillyemets.catapp.ui.screens.animalinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kirillyemets.catapp.ui.components.FullScreenLoadingOverlay
import com.kirillyemets.catapp.ui.screens.search.breedinfo.BreedDetails
import com.kirillyemets.catapp.ui.screens.search.breedinfo.WeightInfoUIState
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalInfoBottomSheetScreen(
    animalId: String,
    viewModel: AnimalInfoScreenViewModel = getViewModel(),
    navigateToBreedInfo: (breedId: String) -> Unit,
    actionBar: @Composable () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val animalInfoEdl = state.animalInfoEdl

    LaunchedEffect(key1 = animalId, block = {
        animalId.let { viewModel.loadAnimalInfo(it) }
    })

    Scaffold(
        containerColor = Color.Transparent,
    ) { padding ->
        when {
            animalInfoEdl.data != null -> {
                val info = animalInfoEdl.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = {
                        item {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                model = info.imageInfo.imageUrl,
                                contentDescription = "TODO",
                                contentScale = ContentScale.FillWidth
                            )
                        }

                        item {
                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val breedText = when (info.breeds.size) {
                                        0 -> "Breed: Unknown"
                                        1 -> "Breed: "
                                        else -> "Breeds: "
                                    }

                                    Text(text = breedText)
                                    if (info.breeds.isNotEmpty()) {
                                        info.breeds.forEach {
                                            Row {
                                                AssistChip(
                                                    onClick = {
                                                        navigateToBreedInfo(it.id)
                                                    },
                                                    label = { Text(it.breed) }
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (info.categories.isNotEmpty()) {
                                        val categoriesText = when (info.categories.size) {
                                            1 -> "Category: "
                                            else -> "Categories: "
                                        }

                                        Text(text = categoriesText)
                                        info.categories.forEach {
                                            Row {
                                                AssistChip(
                                                    onClick = { },
                                                    label = { Text(it.name) }
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            if (info.breeds.size == 1) {
                                val breed = info.breeds.single()

                                BreedDetails(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    origin = breed.origin,
                                    lifeSpan = breed.lifeSpan,
                                    temperament = breed.temperament,
                                    weightInfo = breed.weight?.let {
                                        WeightInfoUIState(
                                            it.imperial,
                                            it.metric
                                        )
                                    }
                                )
                            }
                        }

                        item {
                            actionBar()
                        }

                        item {
                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }
                )
            }

            // TODO
            animalInfoEdl.isError -> {
                AlertDialog(
                    onDismissRequest = { },
                    confirmButton = {},
                    dismissButton = {},
                    text = {},
                    title = {}
                )
            }

            animalInfoEdl.loading -> {
                FullScreenLoadingOverlay(Color.Transparent)
            }
        }
    }
}