package com.kirillyemets.catapp.ui.screens.imageviewer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.argstore.ImageViewerArgStore
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.domain.usecase.MediaInteractor
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.ui.model.AnimalThumbnailUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BatchImageViewerScreenViewModel(
    imageViewerArgStore: ImageViewerArgStore,
    private val animalRepository: AnimalRepository,
    private val mediaInteractor: MediaInteractor,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val imageThumbnails = imageViewerArgStore.imageThumbnails.orEmpty()
    private val initialPage = imageThumbnails.indexOfFirst {
        it.animalId == imageViewerArgStore.startImageId
    }.takeUnless { it == -1 } ?: 0
    private val breedName = imageViewerArgStore.breedName.orEmpty()

    private val likedAnimalIds = animalRepository.loadLikedAnimals()
        .map {
            val s1 = it.map { info -> info.id }.toSet()
            val s2 = imageThumbnails.map { uat -> uat.animalId }.toSet()
            s1.intersect(s2).toList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val state = combineStateFlow(
        likedAnimalIds
    ) { likedAnimals ->
        ViewState(
            breedName = breedName,
            initialPage = initialPage,
            animals = imageThumbnails.map { AnimalThumbnailUIState(it) },
            likedAnimalIds = likedAnimals
        )
    }

    fun likeButtonAction(animalId: String) {
        viewModelScope.launch {
            when (animalId) {
                in likedAnimalIds.value -> {
                    analyticsService.logClick(Analytics.ClickType.FullScreenImageViewer.Dislike)
                    animalRepository.deleteFromLiked(animalId)
                }
                else -> {
                    analyticsService.logClick(Analytics.ClickType.FullScreenImageViewer.Like)
                    animalRepository.saveAnimalAsLikedAsync(animalId).await()
                }
            }
        }
    }

    fun downloadImage(activity: Context, animalId: String) {
        analyticsService.logClick(Analytics.ClickType.AnimalInfo.Download)

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

    class ViewState(
        val breedName: String,
        val initialPage: Int,
        val animals: List<AnimalThumbnailUIState>,
        val likedAnimalIds: List<String>
    ) {
    }
}