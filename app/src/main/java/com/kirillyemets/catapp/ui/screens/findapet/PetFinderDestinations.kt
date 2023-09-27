package com.kirillyemets.catapp.ui.screens.findapet

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.NavConstants
import com.kirillyemets.catapp.ui.navigation.localNavController
import com.kirillyemets.catapp.ui.screens.findapet.tindercards.TinderCardsScreen

object TinderCardsDestination : Destination(
    name = "TinderCardsDestination",
    args = listOf(
        navArgument(NavConstants.ANIMAL_ID) {
            type = NavType.StringType
            nullable = true
        }
    )
) {
    fun route(animalId: String? = null) = routeWithArgs(NavConstants.ANIMAL_ID to animalId)

    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(TinderCardsDestination.destination) {
            val navController = localNavController()

            TinderCardsScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}