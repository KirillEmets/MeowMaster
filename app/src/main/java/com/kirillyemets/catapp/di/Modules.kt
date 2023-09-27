package com.kirillyemets.catapp.di

val appModules = listOf(
    repositoryModule,
    interactorModule,
    networkModule,
    databaseModule,
    cacheModule,
    appPreferencesModule,

    viewModelModule,
    argStoreModule,

    serviceModule
)