package com.kirillyemets.catapp.domain.model

data class Pet(
    val type: AnimalType,
    val name: String,
    val breed: String,
    val imageUrl: String
)

