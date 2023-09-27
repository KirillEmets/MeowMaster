package com.kirillyemets.catapp.domain.usecase

import android.content.Context

interface MediaInteractor {
    suspend fun downloadImageForAnimal(activity: Context, animalId: String): Result<Unit>
    suspend fun shareImageForAnimal(activity: Context, animalId: String): Result<Unit>
}

