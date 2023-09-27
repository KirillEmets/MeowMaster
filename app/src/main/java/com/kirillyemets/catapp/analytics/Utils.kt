package com.kirillyemets.catapp.analytics

import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.screens.findapet.TinderCardsDestination
import com.kirillyemets.catapp.ui.screens.home.HomeScreenDestination
import com.kirillyemets.catapp.ui.screens.imageviewer.BatchImageViewerScreenDestination
import com.kirillyemets.catapp.ui.screens.likedanimals.LikedAnimalsDestination
import com.kirillyemets.catapp.ui.screens.search.BreedInfoScreenDestination
import com.kirillyemets.catapp.ui.screens.search.BreedsScreenDestination
import com.kirillyemets.catapp.ui.screens.settings.SettingsScreenDestination

fun Destination.toAnalyticsScreen() = when(this) {
    HomeScreenDestination -> Analytics.Screen.HomeScreen
    TinderCardsDestination -> Analytics.Screen.ExploreScreen
    LikedAnimalsDestination -> Analytics.Screen.LikedAnimalsScreen
    BreedsScreenDestination -> Analytics.Screen.BreedsScreen
    BreedInfoScreenDestination -> Analytics.Screen.BreedInfoScreen
    BatchImageViewerScreenDestination -> Analytics.Screen.FullScreenImageViewer
    SettingsScreenDestination -> Analytics.Screen.SettingsScreen
    else -> null
}

