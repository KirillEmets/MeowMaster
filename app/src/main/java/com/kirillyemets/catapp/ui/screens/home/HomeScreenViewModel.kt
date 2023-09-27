package com.kirillyemets.catapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import com.kirillyemets.catapp.ui.model.AnimalThumbnailUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val animalRepository: AnimalRepository,
    private val analytics: AnalyticsService
) : ViewModel() {
    private val exploreThumbnail: MutableStateFlow<Edl<UnknownAnimalThumbnail>> =
        MutableStateFlow(Edl())

    private val lastLikedAnimal = animalRepository.loadLikedAnimals().map { it.lastOrNull() }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val state = combineStateFlow(
        exploreThumbnail,
        lastLikedAnimal
    ) { exploreThumbnail, lastLikedAnimal ->
        ViewState(
            exploreImageThumbnail = exploreThumbnail.mapData { AnimalThumbnailUIState(it) },
            breedsImageUrl = "https://cdn2.thecatapi.com/images/dN6eoeLjY.jpg",
            likedImageUrl = lastLikedAnimal?.imageInfo?.imageUrl
        )
    }

    init {
        updateExploreThumbnail()
    }

    private fun updateExploreThumbnail() {
        viewModelScope.launch {
            exploreThumbnail.value = Edl.loading()
            exploreThumbnail.value = animalRepository.getExploreCardThumbnail().toEdl()
        }
    }

    fun onExploreCardClick() {
        analytics.logClick(Analytics.ClickType.HomeScreen.ExploreCard)
        viewModelScope.launch {
            delay(3000)
            updateExploreThumbnail()
        }
    }

    fun onLikedCardClick() {
        analytics.logClick(Analytics.ClickType.HomeScreen.LikedAnimalsCard)
    }

    fun onBreedsCardClick() {
        analytics.logClick(Analytics.ClickType.HomeScreen.BreedsCard)
    }



    data class ViewState(
        val exploreImageThumbnail: Edl<AnimalThumbnailUIState>,
        val breedsImageUrl: String,
        val likedImageUrl: String?
    )
}