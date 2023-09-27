package com.kirillyemets.catapp.domain.model

data class BreedDetails(
    val id: String,
    val weight: WeightInfo?,
    val breed: String,
    val description: String?,
    val temperament: String?,
    val origin: String?,
    val lifeSpan: String?,
    val wikipediaUrl: String?,
    val referenceImageId: String?
)