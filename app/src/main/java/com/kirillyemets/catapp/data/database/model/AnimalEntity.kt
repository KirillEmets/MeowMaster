package com.kirillyemets.catapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.kirillyemets.catapp.domain.model.AnimalInfo
import com.kirillyemets.catapp.domain.model.BreedDetails
import com.kirillyemets.catapp.domain.model.Category
import com.kirillyemets.catapp.domain.model.WeightInfo

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val animalId: String,

    @Embedded
    val imageInfo: ImageInfoEmbedded,
)

@Entity
data class BreedDetailsEntity(
    @PrimaryKey val breedId: String,
    @ColumnInfo(name = "weight_imperial") val weightImperial: String?,
    @ColumnInfo(name = "weight_metric") val weightMetric: String?,
    @ColumnInfo(name = "breed") val breed: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "temperament") val temperament: String?,
    @ColumnInfo(name = "origin") val origin: String?,
    @ColumnInfo(name = "life_span") val lifeSpan: String?,
    @ColumnInfo(name = "wikipedia_url") val wikipediaUrl: String?,
    @ColumnInfo(name = "reference_image_id") val referenceImageId: String?
) {
    constructor(breedDetails: BreedDetails) : this(
        breedId = breedDetails.id,
        weightImperial = breedDetails.weight?.imperial,
        weightMetric = breedDetails.weight?.metric,
        breed = breedDetails.breed,
        description = breedDetails.description,
        temperament = breedDetails.temperament,
        origin = breedDetails.origin,
        lifeSpan = breedDetails.lifeSpan,
        wikipediaUrl = breedDetails.wikipediaUrl,
        referenceImageId = breedDetails.referenceImageId
    )

    fun toBreedDetails() = BreedDetails(
        id = breedId,
        weight = WeightInfo(
            weightImperial.orEmpty(),
            weightMetric.orEmpty()
        ),
        breed = breed,
        description = description,
        temperament = temperament,
        origin = origin,
        lifeSpan = lifeSpan,
        wikipediaUrl = wikipediaUrl,
        referenceImageId = referenceImageId
    )

}

@Entity(primaryKeys = ["animalId", "breedId"])
data class AnimalBreedDetailsCrossRef(
    val animalId: String,
    val breedId: String,
)

@Entity
data class CategoryEntity(
    @PrimaryKey val categoryId: Int,
    @ColumnInfo(name = "name") val name: String
)

@Entity(primaryKeys = ["animalId", "categoryId"])
data class AnimalCategoryCrossRef(
    val animalId: String,
    val categoryId: Int,
)

data class AnimalWithBreedsAndCategories(
    @Embedded val animal: AnimalEntity,
    @Relation(
        parentColumn = "animalId",
        entityColumn = "breedId",
        associateBy = Junction(AnimalBreedDetailsCrossRef::class)
    )
    val breeds: List<BreedDetailsEntity>,

    @Relation(
        parentColumn = "animalId",
        entityColumn = "categoryId",
        associateBy = Junction(AnimalCategoryCrossRef::class)
    )
    val categories: List<CategoryEntity>
) {
    fun toAnimalInfo() = AnimalInfo(
        id = animal.animalId,
        imageInfo = animal.imageInfo.toImageInfo(),
        breeds = breeds.map {
            it.toBreedDetails()
        },
        categories = categories.map {
            Category(
                id = it.categoryId,
                name = it.name
            )
        }
    )
}