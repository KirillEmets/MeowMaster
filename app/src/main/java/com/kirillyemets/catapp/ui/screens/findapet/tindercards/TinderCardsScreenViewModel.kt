package com.kirillyemets.catapp.ui.screens.findapet.tindercards

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.domain.repository.MediaRepository
import com.kirillyemets.catapp.domain.usecase.MediaInteractor
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.RequestState
import com.kirillyemets.catapp.mylibs.any
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import com.kirillyemets.catapp.mylibs.toRequestState
import com.kirillyemets.catapp.ui.navigation.NavConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TinderCardsScreenViewModel(
    private val animalRepository: AnimalRepository,
    private val mediaRepository: MediaRepository,
    private val mediaInteractor: MediaInteractor,
    private val analyticsService: AnalyticsService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val animals = MutableStateFlow(Edl.loading<List<UnknownAnimalThumbnail>>())
    private val animalsPreloadState = MutableStateFlow(RequestState.Nothing)
    private val currentCardIndex = MutableStateFlow(0)

    private val startAnimalId: String? = savedStateHandle[NavConstants.ANIMAL_ID]

    private val preloadedBitmaps = MutableStateFlow<Map<String, Bitmap?>>(mapOf())

    val uiState = combineStateFlow(
        animals,
        animalsPreloadState,
        currentCardIndex,
        preloadedBitmaps
    ) { animals,
        animalsPreloadState,
        currentCardIndex,
        preloadedBitmaps ->
        val reachedLastItem = animals.data?.let { currentCardIndex >= it.size } ?: true
        val loading = any(
            animals.loading,
            reachedLastItem && animalsPreloadState == RequestState.Loading
        )
        val failedToLoad = reachedLastItem && animalsPreloadState == RequestState.Failure

        ViewState(
            cards = animals.mapData { list ->
                list.map {
                    AnimalCardUIState(
                        animalId = it.animalId,
                        imageUrl = it.imageUrl,
                        bitmap = preloadedBitmaps[it.imageUrl]
                    )
                }
            }.data,
            currentCardIndex = currentCardIndex,
            showLoading = loading,
            showFinishMessage = failedToLoad,
            actionButtonsEnabled = !(reachedLastItem || loading)
        )
    }

    init {
        viewModelScope.launch {
            startAnimalId?.let {
                val thumbnail = animalRepository.getCachedThumbnail(startAnimalId) ?: return@let

                animals.value = Edl.success(listOf(thumbnail))
                preloadImages(true)
                return@launch
            }


            val loadedAnimals = animalRepository.getUnknownAnimals(10).toEdl()

            if (loadedAnimals.data != null) {
                val urls = loadedAnimals.data.map { it.imageUrl }
                preloadBitmaps(urls.drop(1))

                val firstBitmap = mediaRepository.loadImage(urls[0]).getOrNull()
                preloadedBitmaps.update {
                    it + (urls[0] to firstBitmap)
                }
            }

            animals.value = loadedAnimals
        }
    }

    private fun preloadBitmaps(urls: List<String>) {
        viewModelScope.launch {
            urls.forEach { url ->
                launch {
                    val bitmap = mediaRepository.loadImage(url).getOrNull()
                    preloadedBitmaps.update {
                        it + (url to bitmap)
                    }
                }
            }
        }
    }

    fun actionLike(animalId: String) {
        analyticsService.logClick(Analytics.ClickType.ExploreScreen.Like)
        viewModelScope.launch {
            animalRepository.saveAnimalAsLikedAsync(animalId).await()
        }
        showNextCard()
    }

    fun actionDislike() {
        analyticsService.logClick(Analytics.ClickType.ExploreScreen.Dislike)
        showNextCard()
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

    fun onShowInfoClick() {
        analyticsService.logClick(Analytics.ClickType.ExploreScreen.ShowInfo)
    }

    fun onBackClick() {
        analyticsService.logClick(Analytics.ClickType.ExploreScreen.BackArrow)
    }

    private fun showNextCard() {
        currentCardIndex.update { it + 1 }

        val shouldPreload = currentCardIndex.value >= (animals.value.data?.size?.minus(5) ?: 0)
        if (shouldPreload) {
            preloadImages(false)
        }
    }

    private fun preloadImages(first: Boolean) {
        viewModelScope.launch {
            animalsPreloadState.value = RequestState.Loading
            val newAnimals = animalRepository.getUnknownAnimals(if (first) 5 else 20)
            animalsPreloadState.value = newAnimals.toRequestState()
            newAnimals.onSuccess { newList ->
                val n = currentCardIndex.value - currentCardIndex.value % 2

                val bitmapsToRemove = animals.value.data!!.subList(0, n)
                preloadedBitmaps.update { map ->
                    map - bitmapsToRemove.map { it.imageUrl }.toSet()
                }

                animals.update {
                    it.mapData { oldList ->
                        val new = (oldList + newList).drop(n)
                        new
                    }
                }
                currentCardIndex.update { it - n }

                preloadBitmaps(newList.map { it.imageUrl })
            }
        }
    }

    data class ViewState(
        val cards: List<AnimalCardUIState>?,
        val currentCardIndex: Int,
        val showLoading: Boolean,
        val showFinishMessage: Boolean,
        val actionButtonsEnabled: Boolean
    )
}

data class AnimalCardUIState(
    val animalId: String,
    val imageUrl: String,
    val bitmap: Bitmap? = null
)