package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.data.AuthInterceptor
import com.kirillyemets.catapp.data.network.service.CatService
import com.kirillyemets.catapp.domain.RemoteConfig
import com.kirillyemets.catapp.domain.RemoteConfigService
import com.kirillyemets.catapp.mylibs.networkadapter.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single<Retrofit> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val remoteConfigService: RemoteConfigService = get()
        val apiKey = remoteConfigService.getStringValue(RemoteConfig.KEY_API_KEY)
        val authInterceptor = AuthInterceptor(apiKey)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()

    }

    single<CatService> {
        get<Retrofit>().create(CatService::class.java)
    }
}