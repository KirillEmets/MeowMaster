package com.kirillyemets.catapp.analytics

import com.kirillyemets.catapp.domain.model.ThemeProfile

abstract class AnalyticsService {
    protected abstract fun logEvent(event: Analytics.Event)

    fun logScreenView(screen: Analytics.Screen) = logEvent(Analytics.Event.ViewScreen(screen))
    fun logClick(clickType: Analytics.ClickType) = logEvent(Analytics.Event.Click(clickType))
    fun logNetworkCall(type: String) = logEvent(Analytics.Event.NetworkCall(type))
}


object Analytics {
    sealed class Event {
        data class ViewScreen(val screen: Screen): Event()
        data class Click(val clickType: ClickType): Event()
        data class NetworkCall(val type: String): Event()
    }

    sealed class ClickType {
        object HomeScreen {
            data object ExploreCard: ClickType()
            data object BreedsCard: ClickType()
            data object LikedAnimalsCard: ClickType()
        }

        object ExploreScreen {
            data object Dislike: ClickType()
            data object Like: ClickType()
            data object ShowInfo: ClickType()
            data object BackArrow: ClickType()
        }

        object AnimalInfo {
            data object Download: ClickType()
            data object Share: ClickType()
            data object Remove: ClickType()
            data object BreedClicked: ClickType()
        }

        object LikedAnimalsScreen {
            data object GalleryCard: ClickType()
            data object ExploreButton: ClickType()
            data object ExploreCard: ClickType()
            data object UndoRemove: ClickType()
        }

        object BreedsScreen {
            data class BreedCard(val breedId: String): ClickType()
            data object Reload: ClickType()
        }

        object BreedInfoScreen {
            data object GalleryCard: ClickType()
        }

        object SettingsScreen {
            data class ChangeTheme(val themeProfile: ThemeProfile): ClickType()
        }

        object FullScreenImageViewer {
            data object Like: ClickType()
            data object Dislike: ClickType()
        }
    }

    sealed class Screen {
        data object HomeScreen : Screen()
        data object ExploreScreen: Screen()
        data object LikedAnimalsScreen: Screen()
        data object BreedsScreen: Screen()
        data object BreedInfoScreen: Screen()
        data object FullScreenImageViewer: Screen()
        data object SettingsScreen: Screen()
    }
}

