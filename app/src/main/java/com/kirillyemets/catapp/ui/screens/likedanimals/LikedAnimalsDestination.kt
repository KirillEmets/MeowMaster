package com.kirillyemets.catapp.ui.screens.likedanimals

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.localNavController
import com.kirillyemets.catapp.ui.screens.findapet.TinderCardsDestination

object LikedAnimalsDestination : Destination("LikedAnimalsScreen") {
    fun route() = destination

    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(LikedAnimalsDestination.destination) {
            val navController = localNavController()

            LikedAnimalsScreen(navigateToExploreScreen = { animalId ->
                navController.navigate(TinderCardsDestination.route(animalId)) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            })
        }
    }
}