package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.data.AppPreferencesImpl
import com.kirillyemets.catapp.domain.AppPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appPreferencesModule = module {
    single<AppPreferences> { AppPreferencesImpl(androidApplication()) }
}