package com.kirillyemets.catapp.ui.screens.imageviewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kirillyemets.catapp.ui.components.BatchImageViewerActionBar
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BatchImageViewerScreen(
    viewModel: BatchImageViewerScreenViewModel = getViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = state.breedName)
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
        val pagerState = rememberPagerState(initialPage = state.initialPage) { state.animals.size }

        HorizontalPager(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            state = pagerState
        ) { page ->
            Column(modifier = Modifier.fillMaxSize()) {
                val animal = state.animals[page]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = animal.imageUrl,
                        contentDescription = "TODO",
                        contentScale = ContentScale.FillWidth
                    )
                }

                val context = LocalContext.current
                BatchImageViewerActionBar(
                    animal.animalId in state.likedAnimalIds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    onDownloadButtonClick = {
                        viewModel.downloadImage(activity = context, animalId = animal.animalId)
                    },
                    onShareButtonClick = {
                        viewModel.shareImage(activity = context, animalId = animal.animalId)
                    },
                    onLikeButtonClick = {
                        viewModel.likeButtonAction(animalId = animal.animalId)
                    }
                )
            }
        }
    }
}

