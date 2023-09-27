package com.kirillyemets.catapp.data.network.model

import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.Breed
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.model.Category
import com.kirillyemets.catapp.domain.model.ImageInfo
import com.kirillyemets.catapp.domain.model.WeightInfo

data class SingleAnimalResponseBody(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String,
    val breeds: List<BreedDetailsResponseBody>?,
    val categories: List<CategoryResponseBody>?
) {
    fun toAnimalInfo() = AnimalInfo(
        id = id,
        imageInfo = ImageInfo(
            imageUrl = url,
            width = width,
            height = height
        ),
        breeds = breeds?.map {
            it.toBreedDetails()
        } ?: emptyList(),
        categories = categories?.map {
            it.toCategory()
        } ?: emptyList()
    )
}

data class WeightResponseBody(
    val imperial: String,
    val metric: String
)

data class BreedDetailsResponseBody(
    val weight: WeightResponseBody?,
    val id: String,
    val name: String,
    val description: String?,
    val temperament: String?,
    val origin: String?,
    val country_codes: String?,
    val country_code: String?,
    val life_span: String?,
    val wikipedia_url: String?,
    val reference_image_id: String?
) {
    fun toBreedDetails() = BreedDetails(
        id = id,
        weight = weight?.let { WeightInfo(weight.imperial, weight.metric) },
        breed = name,
        description = description,
        temperament = temperament,
        origin = origin,
        lifeSpan = life_span,
        wikipediaUrl = wikipedia_url,
        referenceImageId = reference_image_id
    )
}


data class CategoryResponseBody(
    val id: Int,
    val name: String
) {
    fun toCategory() = Category(id, name)
}

data class BreedResponseBody(
    val id: Int,
    val name: String
) {
    fun toBreed() = Breed(id, name)
}