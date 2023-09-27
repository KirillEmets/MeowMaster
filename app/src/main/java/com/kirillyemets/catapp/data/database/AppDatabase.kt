package com.kirillyemets.catapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kirillyemets.catapp.data.database.model.BreedDetailsEntity
import com.kirillyemets.catapp.data.database.model.CategoryEntity
import com.kirillyemets.catapp.data.database.model.AnimalBreedDetailsCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalCategoryCrossRef
import com.kirillyemets.catapp.data.database.model.AnimalEntity
import com.kirillyemets.catapp.data.database.model.LikedAnimalEntity

@Database(
    entities = [
        AnimalEntity::class,
        BreedDetailsEntity::class,
        AnimalBreedDetailsCrossRef::class,
        CategoryEntity::class,
        AnimalCategoryCrossRef::class,
        LikedAnimalEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun likedAnimalsDao(): LikedAnimalsDao
}