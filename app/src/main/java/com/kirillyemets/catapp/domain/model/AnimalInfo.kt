package com.kirillyemets.catapp.domain.model

data class AnimalInfo(
    val id: String,
    val imageInfo: ImageInfo,
    val breeds: List<BreedDetails>,
    val categories: List<Category>
)