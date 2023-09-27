package com.kirillyemets.catapp.data.database.model

import androidx.room.ColumnInfo
import com.kirillyemets.catapp.domain.model.ImageInfo

data class ImageInfoEmbedded(
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "width") val width: Int,
    @ColumnInfo(name = "height") val height: Int,
) {
    fun toImageInfo() = ImageInfo(
        imageUrl = imageUrl,
        width = width,
        height = height
    )
}
