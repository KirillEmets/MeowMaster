package com.kirillyemets.catapp.data.analytics

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService

class AnalyticsServiceImpl(
    private val fa: FirebaseAnalytics
) : AnalyticsService() {
    override fun logEvent(event: Analytics.Event) {
        Log.d("Analytics", "logEvent: $event")

        val firebaseEvent: FirebaseEvent = when (event) {
            is Analytics.Event.NetworkCall -> {
                FirebaseEvent("NetworkCall", bundleOf("type" to event.type))
            }

            is Analytics.Event.Click -> {
                val clickType = when (event.clickType) {
                    Analytics.ClickType.HomeScreen.BreedsCard -> "HomeScreen.Breeds"
                    Analytics.ClickType.HomeScreen.ExploreCard -> "HomeScreen.ExploreCard"
                    Analytics.ClickType.HomeScreen.LikedAnimalsCard -> "HomeScreen.LikedAnimalsCard"

                    Analytics.ClickType.ExploreScreen.BackArrow -> "ExploreScreen.BackArrow"
                    Analytics.ClickType.ExploreScreen.Like -> "ExploreScreen.Like"
                    Analytics.ClickType.ExploreScreen.ShowInfo -> "ExploreScreen.ShowInfo"
                    Analytics.ClickType.ExploreScreen.Dislike -> "ExploreScreen.Dislike"

                    Analytics.ClickType.AnimalInfo.BreedClicked -> "AnimalInfo.BreedClicked"
                    Analytics.ClickType.AnimalInfo.Remove -> "AnimalInfo.Remove"
                    Analytics.ClickType.AnimalInfo.Share -> "AnimalInfo.Share"
                    Analytics.ClickType.AnimalInfo.Download -> "AnimalInfo.Download"

                    is Analytics.ClickType.SettingsScreen.ChangeTheme -> "SettingsScreen.ChangeTheme"

                    Analytics.ClickType.LikedAnimalsScreen.ExploreButton -> "LikedAnimalsScreen.ExploreButton"
                    Analytics.ClickType.LikedAnimalsScreen.ExploreCard -> "LikedAnimalsScreen.ExploreCard"
                    Analytics.ClickType.LikedAnimalsScreen.GalleryCard -> "LikedAnimalsScreen.GalleryCard"
                    Analytics.ClickType.LikedAnimalsScreen.UndoRemove -> "LikedAnimalsScreen.UndoRemove"

                    Analytics.ClickType.BreedInfoScreen.GalleryCard -> "BreedInfoScreen.GalleryCard"

                    is Analytics.ClickType.BreedsScreen.BreedCard -> "BreedsScreen.BreedCard"
                    Analytics.ClickType.BreedsScreen.Reload -> "BreedsScreen.Reload"

                    Analytics.ClickType.FullScreenImageViewer.Dislike -> "FullScreenImageViewer.Dislike"
                    Analytics.ClickType.FullScreenImageViewer.Like -> "FullScreenImageViewer.Like"
                }

                val extra = when(event.clickType) {
                    is Analytics.ClickType.SettingsScreen.ChangeTheme -> bundleOf("themeProfile" to event.clickType.themeProfile.label)
                    is Analytics.ClickType.BreedsScreen.BreedCard -> bundleOf("breedId" to event.clickType.breedId)
                    else -> Bundle()
                }

                FirebaseEvent("Click", bundleOf("clickType" to clickType).apply { putAll(extra) })
            }

            is Analytics.Event.ViewScreen -> {
                val screenName = when (event.screen) {
                    Analytics.Screen.BreedInfoScreen -> "BreedInfoScreen"
                    Analytics.Screen.BreedsScreen -> "BreedsScreen"
                    Analytics.Screen.ExploreScreen -> "ExploreScreen"
                    Analytics.Screen.FullScreenImageViewer -> "FullScreenImageViewer"
                    Analytics.Screen.HomeScreen -> "HomeScreen"
                    Analytics.Screen.LikedAnimalsScreen -> "LikedAnimalsScreen"
                    Analytics.Screen.SettingsScreen -> "SettingsScreen"
                }

                FirebaseEvent(
                    FirebaseAnalytics.Event.SCREEN_VIEW,
                    bundleOf(Param.SCREEN_NAME to screenName)
                )
            }
        }

        fa.logEvent(firebaseEvent.name, firebaseEvent.params)
    }
}

data class FirebaseEvent(
    val name: String,
    val params: Bundle
)