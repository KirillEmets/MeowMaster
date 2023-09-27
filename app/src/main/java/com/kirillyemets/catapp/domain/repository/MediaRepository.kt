package com.kirillyemets.catapp.domain.repository

import android.graphics.Bitmap

interface MediaRepository {
    suspend fun loadImage(url: String): Result<Bitmap>
}