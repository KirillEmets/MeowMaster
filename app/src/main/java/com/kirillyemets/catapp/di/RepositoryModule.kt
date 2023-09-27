package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.data.repository.AnimalRepositoryImpl
import com.kirillyemets.catapp.data.repository.MediaRepositoryImpl
import com.kirillyemets.catapp.domain.repository.AnimalRepository
import com.kirillyemets.catapp.domain.repository.MediaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single <AnimalRepository> { AnimalRepositoryImpl(get(), get(), get(), get()) }
    single <MediaRepository> { MediaRepositoryImpl(androidContext()) }
}