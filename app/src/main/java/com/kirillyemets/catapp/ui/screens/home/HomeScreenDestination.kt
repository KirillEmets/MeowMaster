package com.kirillyemets.catapp.ui.screens.home

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.localNavController
import com.kirillyemets.catapp.ui.screens.findapet.TinderCardsDestination
import com.kirillyemets.catapp.ui.screens.likedanimals.LikedAnimalsDestination
import com.kirillyemets.catapp.ui.screens.search.BreedsScreenDestination

object HomeScreenDestination : Destination("HomeScreen") {
    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(HomeScreenDestination.destination) {

            val navController = localNavController()
            HomeScreen(
                navigateToPetFinder = { animalId: String? ->
                    navController.navigate(TinderCardsDestination.route(animalId = animalId))
                },
                navigateToLikedAnimals = {
                    navController.navigate(LikedAnimalsDestination.route()) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navigateToBreeds = {
                    navController.navigate(BreedsScreenDestination.route()) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}