package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.data.usecase.MediaInteractorImpl
import com.kirillyemets.catapp.domain.usecase.MediaInteractor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val interactorModule = module {
    single<MediaInteractor> { MediaInteractorImpl(androidApplication(), get()) }
}