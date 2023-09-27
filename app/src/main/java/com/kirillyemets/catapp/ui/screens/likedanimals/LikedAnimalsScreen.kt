package com.kirillyemets.catapp.ui.screens.likedanimals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.kirillyemets.catapp.ui.components.AnimalGalleryItem
import com.kirillyemets.catapp.ui.components.AnimalsGalleryGrid
import com.kirillyemets.catapp.ui.components.FullScreenLoadingOverlay
import com.kirillyemets.catapp.ui.components.LikedAnimalInfoActionBar
import com.kirillyemets.catapp.ui.components.dashedBorder
import com.kirillyemets.catapp.ui.screens.animalinfo.AnimalInfoModalBottomSheet
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedAnimalsScreen(
    viewModel: LikedAnimalsScreenViewModel = getViewModel(),
    navigateToExploreScreen: (String?) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val likedAnimalsEdl = state.likedAnimals

    var modalBottomSheetAnimalId by remember { mutableStateOf<String?>(null) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Liked cats")
                },
                scrollBehavior = scrollBehavior
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                likedAnimalsEdl.data != null -> {
                    when (likedAnimalsEdl.data.size) {
                        0 -> {
                            val thumbnail = state.exploreAnimalThumbnailUIState.data
                            LikedAnimalsScreenEmptyState(
                                exploreThumbnailUrl = thumbnail?.imageUrl,
                                onClick = {
                                    navigateToExploreScreen(null)
                                    viewModel.onExploreButtonClick()
                                },
                                onImageClick = {
                                    navigateToExploreScreen(thumbnail?.animalId)
                                    viewModel.onExploreCardClick()
                                }
                            )
                        }

                        else -> {
                            AnimalsGalleryGrid(
                                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                                animals = likedAnimalsEdl.data.map {
                                    AnimalGalleryItem(
                                        it.animalId,
                                        it.imageUrl
                                    )
                                },
                                onAnimalCardClick = {
                                    modalBottomSheetAnimalId = it
                                    viewModel.onGalleryCardClick()
                                }
                            )
                        }
                    }
                }

                likedAnimalsEdl.loading -> FullScreenLoadingOverlay()
                likedAnimalsEdl.isError -> Unit
            }
        }
    }

    val context = LocalContext.current
    var showUndoSnackbar by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = showUndoSnackbar, block = {
        if (showUndoSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Deleted from liked",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDeleteFromLiked()
            }
            showUndoSnackbar = false
        }
    })

    if (modalBottomSheetAnimalId != null)
        AnimalInfoModalBottomSheet(
            animalId = modalBottomSheetAnimalId!!,
            actionBar = {
                LikedAnimalInfoActionBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    onDownloadButtonClick = {
                        viewModel.downloadImage(context, modalBottomSheetAnimalId!!)
                    },
                    onShareButtonClick = {
                        viewModel.shareImage(context, modalBottomSheetAnimalId!!)
                    },
                    onRemoveButtonClick = {
                        viewModel.deleteFromLiked(modalBottomSheetAnimalId!!)
                        modalBottomSheetAnimalId = null
                        showUndoSnackbar = true
                    }
                )
            },
            onDismiss = {
                modalBottomSheetAnimalId = null
            }
        )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedAnimalsScreenEmptyState(
    exploreThumbnailUrl: String?,
    onClick: () -> Unit,
    onImageClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .dashedBorder(
                    2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    cornerRadiusDp = 12.dp
                ),
        ) {
            Column(modifier = Modifier) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    text = "Ooops, you don't have any cat's saved right now. Go to the Explore page to find some!",
                    style = MaterialTheme.typography.titleLarge
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    OutlinedButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = { onClick() }
                    ) {
                        Text(text = "Explore", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            text = "Don't worry, there's a cat just for you:"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.9f)
                    .aspectRatio(4 / 3f),
                onClick = onImageClick
            ) {

                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = exploreThumbnailUrl,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    contentDescription = "TODO",
                    loading = {
                        FullScreenLoadingOverlay(Color.Transparent)
                    }
                )
            }
        }

    }
}