package com.kirillyemets.catapp.ui.model

import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail

data class AnimalThumbnailUIState(
    val animalId: String,
    val imageUrl: String,
    val width: Int,
    val height: Int
) {
    constructor(animalThumbnail: UnknownAnimalThumbnail) : this(
        animalId = animalThumbnail.animalId,
        imageUrl = animalThumbnail.imageUrl,
        width = animalThumbnail.width,
        height = animalThumbnail.height
    )

    fun toAnimalThumbnail(): UnknownAnimalThumbnail = UnknownAnimalThumbnail(
        animalId = animalId,
        imageUrl = imageUrl,
        width = width,
        height = height
    )
}