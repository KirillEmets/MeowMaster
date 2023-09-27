package com.kirillyemets.catapp.ui

import android.app.Application
import com.kirillyemets.catapp.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(appModules)
        }
    }
}