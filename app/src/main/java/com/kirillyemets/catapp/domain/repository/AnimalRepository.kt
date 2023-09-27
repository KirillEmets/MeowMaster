package com.kirillyemets.catapp.domain.repository

import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

interface AnimalRepository {
    suspend fun getExploreCardThumbnail(): Result<UnknownAnimalThumbnail>
    suspend fun getUnknownAnimals(amount: Int): Result<List<UnknownAnimalThumbnail>>
    suspend fun getAnimalInfo(animalId: String): Result<AnimalInfo>
    suspend fun getBreedInfo(breedId: String): Result<BreedDetails>
    suspend fun getUnknownAnimalsForBreed(breedId: String): Result<List<UnknownAnimalThumbnail>>
    fun saveAnimalAsLikedAsync(animalId: String): Deferred<Result<Unit>>

    suspend fun deleteFromLiked(animalId: String)
    suspend fun undoDeletion(animalId: String)
    fun loadLikedAnimals(): Flow<List<AnimalInfo>>
    fun loadBreeds(): Flow<List<BreedDetails>>


    fun getCachedThumbnail(animalId: String): UnknownAnimalThumbnail?
    suspend fun reloadBreeds(): Result<List<BreedDetails>>
}