package com.kirillyemets.catapp.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import com.kirillyemets.catapp.domain.repository.MediaRepository

class MediaRepositoryImpl(private val context: Context) : MediaRepository {
    private val imageLoader = ImageLoader(context)

    override suspend fun loadImage(url: String): Result<Bitmap> {
        val result = imageLoader.execute(
            ImageRequest.Builder(context)
                .data(url)
                .build()
        )

        return when (result.drawable) {
            null -> Result.failure(Throwable("Couldn't load image"))
            else -> Result.success((result.drawable as BitmapDrawable).bitmap)
        }
    }
}