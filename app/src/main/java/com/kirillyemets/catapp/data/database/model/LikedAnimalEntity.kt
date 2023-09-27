package com.kirillyemets.catapp.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_animals")
data class LikedAnimalEntity(
    @PrimaryKey val animalId: String
)