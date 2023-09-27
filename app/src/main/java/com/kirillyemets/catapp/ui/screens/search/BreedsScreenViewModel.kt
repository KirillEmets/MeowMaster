package com.kirillyemets.catapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.collectToEdlFlow
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedsScreenViewModel(
    private val animalRepository: AnimalRepository,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    val breeds = MutableStateFlow<Edl<List<BreedDetails>>>(Edl.loading())
    private val imageIdToAnimalInfoMap = MutableStateFlow<Map<String, Edl<AnimalInfo>>>(emptyMap())

    val state = combineStateFlow(breeds, imageIdToAnimalInfoMap) { breeds, imageIdToAnimalInfoMap ->
        val breedsThumbnails = breeds.mapData { breedList ->
            breedList.filter { it.referenceImageId != null }.map { breedDetails ->
                val refImageEdl =
                    imageIdToAnimalInfoMap[breedDetails.referenceImageId] ?: Edl.loading()

                BreedThumbnailUIState(
                    breedId = breedDetails.id,
                    name = breedDetails.breed,
                    referenceImageInfo = refImageEdl.mapData {
                        with(it.imageInfo) {
                            ReferenceImageUIState(imageUrl, width, height)
                        }
                    }
                )
            }
        }

        ViewState(
            breedsThumbnails
        )
    }

    fun reloadBreeds() {
        analyticsService.logClick(Analytics.ClickType.BreedsScreen.Reload)
        viewModelScope.launch {
            breeds.value = Edl.loading()
            breeds.value = animalRepository.reloadBreeds().toEdl()
        }
    }

    fun onBreedClicked(breedId: String) {
        analyticsService.logClick(Analytics.ClickType.BreedsScreen.BreedCard(breedId))
    }

    init {
        viewModelScope.launch {
            animalRepository.loadBreeds().collectToEdlFlow(breeds)
        }

        viewModelScope.launch {
            breeds.collectLatest {
                coroutineScope {
                    it.data?.forEach { breedDetails ->
                        val imageId = breedDetails.referenceImageId ?: return@forEach

                        imageIdToAnimalInfoMap.update { map ->
                            map.toMutableMap().apply {
                                this[imageId] = Edl.loading()
                            }
                        }

                        launch {
                            val referenceInfo = animalRepository.getAnimalInfo(animalId = imageId)

                            imageIdToAnimalInfoMap.update { map ->
                                map.toMutableMap().apply {
                                    this[imageId] = referenceInfo.toEdl()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data class ViewState(
        val breedsEdl: Edl<List<BreedThumbnailUIState>>
    )
}

data class BreedThumbnailUIState(
    val breedId: String,
    val name: String,
    val referenceImageInfo: Edl<ReferenceImageUIState>
)

data class ReferenceImageUIState(
    val url: String,
    val width: Int,
    val height: Int
)