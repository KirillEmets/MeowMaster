package com.kirillyemets.catapp.data.cache

import com.kirillyemets.catapp.data.database.LikedAnimalsDao
import com.kirillyemets.catapp.data.database.model.AnimalBreedDetailsCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalCategoryCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalEntity
import com.kirillyemets.catapp.data.database.model.BreedDetailsEntity
import com.kirillyemets.catapp.data.database.model.CategoryEntity
import com.kirillyemets.catapp.data.database.model.ImageInfoEmbedded
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform


interface AnimalCache {
    suspend fun getAnimalInfo(animalId: String): AnimalInfo?
    suspend fun saveAnimalInfo(animalInfo: AnimalInfo)

    suspend fun getBreedDetails(breedId: String): BreedDetails?
    suspend fun saveBreedDetails(breedDetails: BreedDetails)

    suspend fun updateAllBreeds(list: Set<BreedDetails>)
    fun loadAllBreeds(): Flow<Set<BreedDetails>>
}

class AnimalCacheImpl(
    private val animalsDao: LikedAnimalsDao
) : AnimalCache {
    override suspend fun getAnimalInfo(animalId: String): AnimalInfo? {
        return animalsDao.getAnimal(animalId = animalId)?.toAnimalInfo()
    }

    override suspend fun saveAnimalInfo(animalInfo: AnimalInfo) {
        val animalId = animalInfo.id
        val breeds = animalInfo.breeds.map {
            BreedDetailsEntity(
                breedId = it.id,
                weightImperial = it.weight?.imperial,
                weightMetric = it.weight?.metric,
                breed = it.breed,
                description = it.description,
                temperament = it.temperament,
                origin = it.origin,
                lifeSpan = it.lifeSpan,
                wikipediaUrl = it.wikipediaUrl,
                referenceImageId = it.referenceImageId
            )
        }


        val breedRefs = breeds.map {
            AnimalBreedDetailsCrossRef(
                animalId = animalId,
                breedId = it.breedId
            )
        }

        val categories = animalInfo.categories.map {
            CategoryEntity(
                categoryId = it.id,
                name = it.name
            )
        }

        val categoriesRefs = categories.map {
            AnimalCategoryCrossRef(
                animalId = animalId,
                categoryId = it.categoryId
            )
        }

        val animalEntity = AnimalEntity(
            animalId,
            ImageInfoEmbedded(
                imageUrl = animalInfo.imageInfo.imageUrl,
                width = animalInfo.imageInfo.width,
                height = animalInfo.imageInfo.height,
            )
        )

        with(animalsDao) {
            saveBreeds(breeds)
            saveAnimal(animalEntity)
            savAnimalBreedCrossRefs(breedRefs)
            saveCategories(categories)
            saveAnimalCategoryCrossRefs(categoriesRefs)
        }
    }

    override suspend fun getBreedDetails(breedId: String): BreedDetails? {
        return animalsDao.getBreed(breedId = breedId)?.toBreedDetails()
    }

    override suspend fun saveBreedDetails(breedDetails: BreedDetails) {
        animalsDao.saveBreeds(listOf(BreedDetailsEntity(breedDetails)))
    }

    override suspend fun updateAllBreeds(list: Set<BreedDetails>) {
        animalsDao.saveBreeds(list.map { BreedDetailsEntity(it) })
    }

    override fun loadAllBreeds(): Flow<Set<BreedDetails>> {
        return animalsDao.loadAllBreeds().transform { entities ->
            val list = entities.map { it.toBreedDetails() }.toSet()
            if (list.isNotEmpty())
                emit(list)
        }
    }
}