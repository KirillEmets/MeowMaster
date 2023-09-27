package com.kirillyemets.catapp.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kirillyemets.catapp.data.database.model.BreedDetailsEntity
import com.kirillyemets.catapp.data.database.model.CategoryEntity
import com.kirillyemets.catapp.data.database.model.AnimalBreedDetailsCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalCategoryCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalEntity
import com.kirillyemets.catapp.data.database.model.AnimalWithBreedsAndCategories
import com.kirillyemets.catapp.data.database.model.LikedAnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedAnimalsDao {
    @Transaction
    @Query("SELECT animals.* FROM animals " +
        "INNER JOIN liked_animals ON animals.animalId = liked_animals.animalId")
    fun loadLikedAnimals(): Flow<List<AnimalWithBreedsAndCategories>>

    @Query("SELECT * from breeddetailsentity WHERE breedId = :breedId")
    suspend fun getBreed(breedId: String): BreedDetailsEntity?

    @Query("SELECT * from breeddetailsentity")
    fun loadAllBreeds(): Flow<List<BreedDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveBreeds(breeds: List<BreedDetailsEntity>)

    @Query("SELECT * from animals WHERE animalId = :animalId")
    suspend fun getAnimal(animalId: String): AnimalWithBreedsAndCategories?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAnimal(animalEntity: AnimalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveLikedAnimal(animalEntity: LikedAnimalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun savAnimalBreedCrossRefs(animalBreedDetailsCrossRefs: List<AnimalBreedDetailsCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAnimalCategoryCrossRefs(animalCategoryCrossRef: List<AnimalCategoryCrossRef>)

    @Query ("DELETE from liked_animals WHERE animalId = :animalId")
    suspend fun deleteFromLiked(animalId: String)
}