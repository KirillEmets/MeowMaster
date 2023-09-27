package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.data.cache.AnimalCache
import com.kirillyemets.catapp.data.cache.AnimalCacheImpl
import org.koin.dsl.module

val cacheModule = module {
    single<AnimalCache> { AnimalCacheImpl(get()) }
}