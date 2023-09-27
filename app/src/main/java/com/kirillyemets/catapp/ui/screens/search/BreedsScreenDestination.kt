package com.kirillyemets.catapp.ui.screens.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kirillyemets.catapp.domain.argstore.ImageViewerArgStore
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.NavConstants
import com.kirillyemets.catapp.ui.navigation.localNavController
import com.kirillyemets.catapp.ui.screens.imageviewer.BatchImageViewerScreenDestination
import com.kirillyemets.catapp.ui.screens.search.breedinfo.BreedInfoScreen
import org.koin.compose.rememberKoinInject

object BreedsScreenDestination : Destination("BreedsScreen") {
    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination) {

            val navController = localNavController()
            BreedsScreen(navigateToBreedInfo = { id ->
                navController.navigate(BreedInfoScreenDestination.route(id))
            })
        }
    }

    fun route() = routeWithArgs()
}

object BreedInfoScreenDestination : Destination(
    name = "BreedInfoScreen",
    args = listOf(navArgument(name = NavConstants.BREED_ID) {
        type = NavType.StringType
    })
) {
    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination) {
            val navController = localNavController()
            val imageViewerArgStore = rememberKoinInject<ImageViewerArgStore>()

            BreedInfoScreen(
                navigateToImageViewer = { breedName, id, list ->
                    imageViewerArgStore.putArgs(
                        breedName = breedName,
                        startImageId = id,
                        imageThumbnails = list
                    )

                    navController.navigate(BatchImageViewerScreenDestination.route())
                },
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }

    fun route(breedId: String) = routeWithArgs(NavConstants.BREED_ID to breedId)
}