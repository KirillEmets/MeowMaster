package com.kirillyemets.catapp.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.data.RemoteConfigServiceImpl
import com.kirillyemets.catapp.data.analytics.AnalyticsServiceImpl
import com.kirillyemets.catapp.domain.RemoteConfigService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {
    single { FirebaseAnalytics.getInstance(androidContext())}
    single<AnalyticsService> { AnalyticsServiceImpl(get()) }

    single { FirebaseRemoteConfig.getInstance() }
    single<RemoteConfigService> { RemoteConfigServiceImpl(get()) }
}