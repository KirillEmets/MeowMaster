package com.kirillyemets.catapp.domain.argstore

import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail

data class ImageViewerArgStore(
    var breedName: String? = null,
    var startImageId: String? = null,
    var imageThumbnails: List<UnknownAnimalThumbnail>? = null
) {
    fun putArgs(
        breedName: String,
        startImageId: String,
        imageThumbnails: List<UnknownAnimalThumbnail>
    ) {
        this.breedName = breedName
        this.startImageId = startImageId
        this.imageThumbnails = imageThumbnails
    }
}