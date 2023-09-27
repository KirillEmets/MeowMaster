package com.kirillyemets.catapp.ui.screens.search.breedinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import com.kirillyemets.catapp.ui.model.AnimalThumbnailUIState
import com.kirillyemets.catapp.ui.navigation.NavConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedInfoScreenViewModel(
    private val animalRepository: AnimalRepository,
    private val analyticsService: AnalyticsService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId = requireNotNull(savedStateHandle.get<String>(NavConstants.BREED_ID))
    private val breedInfo = MutableStateFlow<Edl<BreedDetails>>(Edl.loading())
    private val referenceImageInfo = MutableStateFlow<Edl<AnimalInfo>>(Edl.loading())

    private val breedImages = MutableStateFlow<Edl<List<UnknownAnimalThumbnail>>>(Edl.loading())
    private var currentPage = 0

    val state = combineStateFlow(
        breedInfo,
        referenceImageInfo,
        breedImages
    ) { breedInfo, referenceImageInfo, breedImages ->
        ViewState(
            breedInfo.mapData {
                it.toUI()
            },
            referenceImageInfo.mapData {
                it.imageInfo.imageUrl
            },
            breedImages = breedImages.mapData { list ->
                list.map {
                    AnimalThumbnailUIState(it)
                }
            }
        )
    }

    init {
        viewModelScope.launch {
            val result = animalRepository.getBreedInfo(breedId = breedId)
            breedInfo.value = result.toEdl()

            if (result.isFailure) {
                return@launch
            }

            val referenceImageId = result.getOrThrow().referenceImageId ?: return@launch
            val infoResult = animalRepository.getAnimalInfo(referenceImageId)
            referenceImageInfo.value = infoResult.toEdl()
        }

        loadNextPage()
    }

    // It can support paging but I didn't really need it
    private fun loadNextPage() {
        currentPage += 1
        viewModelScope.launch {
            breedImages.update {
                it.copy(loading = true)
            }

            val newImages = animalRepository.getUnknownAnimalsForBreed(breedId = breedId)

            newImages.onSuccess { newList ->
                breedImages.update {
                    when (it.data) {
                        null -> Edl.success(newList)
                        else -> it.mapData { oldList -> oldList + newList }.copy(loading = false)
                    }
                }
            }

            newImages.onFailure { throwable ->
                breedImages.update {
                    it.copy(error = throwable, loading = false)
                }
            }
        }
    }

    fun onGalleryCardClicked() {
        analyticsService.logClick(Analytics.ClickType.BreedInfoScreen.GalleryCard)
    }

    data class ViewState(
        val breedInfoEdl: Edl<BreedDetailsUIState>,
        val referenceImageUrl: Edl<String>,
        val breedImages: Edl<List<AnimalThumbnailUIState>>
    )
}

data class BreedDetailsUIState(
    val id: String,
    val name: String,
    val weightInfo: WeightInfoUIState?,
    val description: String?,
    val temperament: String?,
    val origin: String?,
    val lifeSpan: String?,
    val wikipediaUrl: String?,
)

data class WeightInfoUIState(
    val imperial: String,
    val metric: String
)

fun BreedDetails.toUI() = BreedDetailsUIState(
    id = id,
    name = breed,
    weightInfo = weight?.let { WeightInfoUIState(it.imperial, it.metric) },
    description = description,
    temperament = temperament,
    origin = origin,
    lifeSpan = lifeSpan,
    wikipediaUrl = wikipediaUrl,
)

