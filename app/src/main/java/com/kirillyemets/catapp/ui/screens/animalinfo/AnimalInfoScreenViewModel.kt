package com.kirillyemets.catapp.ui.screens.animalinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.mylibs.Edl
import com.kirillyemets.catapp.mylibs.combineStateFlow
import com.kirillyemets.catapp.mylibs.toEdl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AnimalInfoScreenViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {
    val animalId: String = ""//requireNotNull(savedStateHandle[NavConstants.ANIMAL_ID])

    private val animalInfo = MutableStateFlow(Edl<AnimalInfo>())
    val state = combineStateFlow(animalInfo) { animalInfo ->
        ViewState(animalInfo)
    }

    fun loadAnimalInfo(id: String) {
        animalInfo.value = Edl.loading()
        viewModelScope.launch {
            animalInfo.value = animalRepository.getAnimalInfo(id).toEdl()
        }
    }

    data class ViewState(
        val animalInfoEdl: Edl<AnimalInfo> // TODO use UI State model
    )
}