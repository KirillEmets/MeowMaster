package com.kirillyemets.catapp.ui.screens.likedanimals

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.domain.usecase.MediaInteractor
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.collectToEdlFlow
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import com.kirillyemets.catapp.ui.model.AnimalThumbnailUIState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LikedAnimalsScreenViewModel(
    private val animalRepository: AnimalRepository,
    private val mediaInteractor: MediaInteractor,
    private val analyticsService: AnalyticsService
) : ViewModel() {
    private val likedAnimals = MutableStateFlow<Edl<List<AnimalInfo>>>(Edl())
    private val exploreThumbnail = MutableStateFlow<Edl<UnknownAnimalThumbnail>>(Edl())

    val state = combineStateFlow(
        likedAnimals,
        exploreThumbnail
    ) { likedAnimals, exploreThumbnail ->
        ViewState(
            likedAnimals = likedAnimals.mapData { list ->
                list.map {
                    LikedAnimalUIState(it.id, it.imageInfo.imageUrl)
                }
            },
            exploreAnimalThumbnailUIState = exploreThumbnail.mapData { AnimalThumbnailUIState(it) }
        )
    }

    init {
        viewModelScope.launch {
            likedAnimals.value = Edl.loading()
            animalRepository.loadLikedAnimals().onEach {
                if (it.isEmpty()) {
                   updateExploreThumbnail()
                }
            }.collectToEdlFlow(likedAnimals)
        }
    }

    private var lastDeletedAnimalId = ""
    fun deleteFromLiked(animalId: String) {
        analyticsService.logClick(Analytics.ClickType.AnimalInfo.Remove)
        viewModelScope.launch {
            animalRepository.deleteFromLiked(animalId)
            lastDeletedAnimalId = animalId
        }
    }

    fun undoDeleteFromLiked() {
        analyticsService.logClick(Analytics.ClickType.LikedAnimalsScreen.UndoRemove)
        viewModelScope.launch {
            animalRepository.undoDeletion(lastDeletedAnimalId)
        }
    }

    fun downloadImage(activity: Context, animalId: String) {
        analyticsService.logClick(Analytics.ClickType.AnimalInfo.Share)
        viewModelScope.launch {
            mediaInteractor.downloadImageForAnimal(activity, animalId)
        }
    }

    fun shareImage(activity: Context, animalId: String) {
        analyticsService.logClick(Analytics.ClickType.AnimalInfo.Share)
        viewModelScope.launch {
            mediaInteractor.shareImageForAnimal(activity, animalId)
        }
    }

    private fun updateExploreThumbnail() {
        viewModelScope.launch {
            exploreThumbnail.value = Edl.loading()
            exploreThumbnail.value = animalRepository.getExploreCardThumbnail().toEdl()
        }
    }

    fun onExploreCardClick() {
        analyticsService.logClick(Analytics.ClickType.LikedAnimalsScreen.ExploreCard)
        viewModelScope.launch {
            delay(3000)
            updateExploreThumbnail()
        }
    }

    fun onExploreButtonClick() {
        analyticsService.logClick(Analytics.ClickType.LikedAnimalsScreen.ExploreButton)
    }

    fun onGalleryCardClick() {
        analyticsService.logClick(Analytics.ClickType.LikedAnimalsScreen.GalleryCard)
    }

    class ViewState(
        val likedAnimals: Edl<List<LikedAnimalUIState>>,
        val exploreAnimalThumbnailUIState: Edl<AnimalThumbnailUIState>
    )
}

class LikedAnimalUIState(
    val animalId: String,
    val imageUrl: String
)