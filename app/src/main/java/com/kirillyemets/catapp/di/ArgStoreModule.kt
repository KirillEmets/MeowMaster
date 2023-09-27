package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.domain.argstore.ImageViewerArgStore
import org.koin.dsl.module

val argStoreModule = module {
    single { ImageViewerArgStore() }
}