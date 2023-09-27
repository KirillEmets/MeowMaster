package com.kirillyemets.catapp.data.repository

import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.data.cache.AnimalCache
import com.kirillyemets.catapp.data.database.LikedAnimalsDao
import com.kirillyemets.catapp.data.database.model.AnimalBreedDetailsCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalCategoryCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalEntity
import com.kirillyemets.catapp.data.database.model.BreedDetailsEntity
import com.kirillyemets.catapp.data.database.model.CategoryEntity
import com.kirillyemets.catapp.data.database.model.ImageInfoEmbedded
import com.kirillyemets.catapp.data.database.model.LikedAnimalEntity
import com.kirillyemets.catapp.data.network.service.CatService
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.model.UnknownAnimalThumbnail
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.mylibs.networkadapter.toResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AnimalRepositoryImpl(
    private val animalsDao: LikedAnimalsDao,
    private val catService: CatService,
    private val cache: AnimalCache,
    private val analyticsService: AnalyticsService,
) : AnimalRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val animalIdToImageUrlCache = mutableMapOf<String, UnknownAnimalThumbnail>()

    override suspend fun getUnknownAnimals(amount: Int): Result<List<UnknownAnimalThumbnail>> {
        return catService.getRandomCats(limit = amount).toResult { list ->
            list.map {
                it.toUnknownAnimalThumbnail()
            }
        }.onSuccess { list ->
            animalIdToImageUrlCache += list.associateBy { it.animalId }
            analyticsService.logNetworkCall("thumbnail")
        }
    }

    override suspend fun getAnimalInfo(animalId: String): Result<AnimalInfo> {
        val cached = cache.getAnimalInfo(animalId)
        if (cached != null) {
            return Result.success(cached)
        }

        val result = catService.getSingleCatInfo(animalId).toResult {
            it.toAnimalInfo()
        }

        result.onSuccess {
            cache.saveAnimalInfo(it)
            analyticsService.logNetworkCall("info")
        }

        return result
    }

    override fun loadBreeds(): Flow<List<BreedDetails>> = flow {
        coroutineScope {
            launch {
                val result = catService.getBreeds().toResult { list ->
                    list.map { it.toBreedDetails() }
                }

                result.onSuccess {
                    cache.updateAllBreeds(it.toSet())
                    analyticsService.logNetworkCall("breeds")
                }.onFailure {
                    emit(emptyList())
                }
            }

            emitAll(cache.loadAllBreeds().map { it.toList() })
        }
    }

    override suspend fun reloadBreeds(): Result<List<BreedDetails>> {
        val result = catService.getBreeds().toResult { list ->
            list.map { it.toBreedDetails() }
        }

        result.onSuccess {
            cache.updateAllBreeds(it.toSet())
            analyticsService.logNetworkCall("breeds")
        }

        if (result.isFailure) {
            delay(1500)
        }

        return result
    }

    override suspend fun getUnknownAnimalsForBreed(breedId: String): Result<List<UnknownAnimalThumbnail>> {
        return catService.getRandomCats(limit = 45, breedId = breedId).toResult { list ->
            list.map {
                it.toUnknownAnimalThumbnail()
            }
        }.onSuccess { list ->
            animalIdToImageUrlCache += list.associateBy { it.animalId }
            analyticsService.logNetworkCall("thumbnail")
        }
    }

    override suspend fun getExploreCardThumbnail(): Result<UnknownAnimalThumbnail> {
        return catService.getRandomCats(limit = 1).toResult {
            it.single().toUnknownAnimalThumbnail()
        }.onSuccess { thumbnail ->
            animalIdToImageUrlCache += thumbnail.animalId to thumbnail
            analyticsService.logNetworkCall("thumbnail")
        }
    }

    override fun saveAnimalAsLikedAsync(animalId: String): Deferred<Result<Unit>> {
        val deferred = CompletableDeferred<Result<Unit>>()
        coroutineScope.launch {
            val infoResult = getAnimalInfo(animalId)
            val info = infoResult.getOrNull()
            if (info == null) {
                deferred.complete(Result.failure(Throwable("Couldn't load animal Info to save it")))
                return@launch
            }

            val breeds = info.breeds.map {
                BreedDetailsEntity(it)
            }


            val breedRefs = breeds.map {
                AnimalBreedDetailsCrossRef(
                    animalId = animalId,
                    breedId = it.breedId
                )
            }

            val categories = info.categories.map {
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
                    imageUrl = info.imageInfo.imageUrl,
                    width = info.imageInfo.width,
                    height = info.imageInfo.height,
                )
            )

            val likedAnimalEntity = LikedAnimalEntity(animalId = animalId)

            with(animalsDao) {
                saveBreeds(breeds)
                saveAnimal(animalEntity)
                saveLikedAnimal(likedAnimalEntity)
                savAnimalBreedCrossRefs(breedRefs)
                saveCategories(categories)
                saveAnimalCategoryCrossRefs(categoriesRefs)
            }

            deferred.complete(Result.success(Unit))
        }

        return deferred
    }

    override suspend fun getBreedInfo(breedId: String): Result<BreedDetails> {
        val cached = cache.getBreedDetails(breedId)
        if (cached != null) {
            return Result.success(cached)
        }

        val result = catService.getSingleBreedInfo(breedId).toResult { it.toBreedDetails() }

        result.onSuccess {
            cache.saveBreedDetails(it)
            analyticsService.logNetworkCall("breedInfo")
        }

        return result
    }

    override fun loadLikedAnimals(): Flow<List<AnimalInfo>> {
        return animalsDao.loadLikedAnimals().map { list ->
            list.map {
                it.toAnimalInfo()
            }
        }
    }

    override suspend fun deleteFromLiked(animalId: String) {
        animalsDao.deleteFromLiked(animalId)
    }

    override suspend fun undoDeletion(animalId: String) {
        animalsDao.saveLikedAnimal(LikedAnimalEntity(animalId))
    }

    override fun getCachedThumbnail(animalId: String) = animalIdToImageUrlCache[animalId]
}