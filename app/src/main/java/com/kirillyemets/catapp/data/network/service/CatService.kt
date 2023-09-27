package com.kirillyemets.catapp.data.network.service

import com.kirillyemets.catapp.data.network.model.BreedDetailsResponseBody
import com.kirillyemets.catapp.data.network.model.SingleAnimalResponseBody
import com.kirillyemets.catapp.data.network.model.UnknownAnimalThumbnailResponseBody
import com.kirillyemets.catapp.mylibs.networkadapter.GeneralResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CatService {
    @GET("images/search")
    suspend fun getRandomCats(
        @Query("limit") limit: Int = 10,
        @Query(value = "breed_ids") breedId: String? = null
    ): GeneralResponse<List<UnknownAnimalThumbnailResponseBody>>

    @GET("images/{catId}")
    suspend fun getSingleCatInfo(@Path("catId") catId: String): GeneralResponse<SingleAnimalResponseBody>

    @GET("breeds")
    suspend fun getBreeds(): GeneralResponse<List<BreedDetailsResponseBody>>

    @GET("breeds/{breedId}")
    suspend fun getSingleBreedInfo(@Path("breedId") breedId: String): GeneralResponse<BreedDetailsResponseBody>
}