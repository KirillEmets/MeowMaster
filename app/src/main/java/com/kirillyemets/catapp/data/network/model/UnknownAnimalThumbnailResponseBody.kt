package com.kirillyemets.catapp.data.network.model

import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail

data class UnknownAnimalThumbnailResponseBody(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
) {
    fun toUnknownAnimalThumbnail() = UnknownAnimalThumbnail(
        animalId = id,
        imageUrl = url,
        width = width,
        height = height
    )
}