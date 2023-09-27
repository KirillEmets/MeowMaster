package com.kirillyemets.catapp.di

import androidx.room.Room
import com.kirillyemets.catapp.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java, "database-name",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        get<AppDatabase>().likedAnimalsDao()
    }
}