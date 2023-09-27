package com.kirillyemets.catapp.ui.screens.imageviewer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.localNavController

object BatchImageViewerScreenDestination : Destination(
    name = "BatchImageViewerScreenDestination",
) {
    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination) {
            val navController = localNavController()
            BatchImageViewerScreen(navigateBack = {
                navController.navigateUp()
            })
        }
    }

    fun route() = routeWithArgs()
}