package com.kirillyemets.catapp.ui.navigation

import com.kirillyemets.catapp.ui.screens.findapet.TinderCardsDestination
import com.kirillyemets.catapp.ui.screens.home.HomeScreenDestination
import com.kirillyemets.catapp.ui.screens.imageviewer.BatchImageViewerScreenDestination
import com.kirillyemets.catapp.ui.screens.likedanimals.LikedAnimalsDestination
import com.kirillyemets.catapp.ui.screens.search.BreedInfoScreenDestination
import com.kirillyemets.catapp.ui.screens.search.BreedsScreenDestination
import com.kirillyemets.catapp.ui.screens.settings.SettingsScreenDestination

val allDestinations = listOf(
    HomeScreenDestination,
    LikedAnimalsDestination,
    TinderCardsDestination,
    BreedsScreenDestination,
    BreedInfoScreenDestination,
    BatchImageViewerScreenDestination,
    SettingsScreenDestination
)