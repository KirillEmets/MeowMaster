package com.kirillyemets.catapp.di

import com.kirillyemets.catapp.ui.screens.animalinfo.AnimalInfoScreenViewModel
import com.kirillyemets.catapp.ui.screens.findapet.tindercards.TinderCardsScreenViewModel
import com.kirillyemets.catapp.ui.screens.home.HomeScreenViewModel
import com.kirillyemets.catapp.ui.screens.imageviewer.BatchImageViewerScreenViewModel
import com.kirillyemets.catapp.ui.screens.likedanimals.LikedAnimalsScreenViewModel
import com.kirillyemets.catapp.ui.screens.search.BreedsScreenViewModel
import com.kirillyemets.catapp.ui.screens.search.breedinfo.BreedInfoScreenViewModel
import com.kirillyemets.catapp.ui.screens.settings.SettingsScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::TinderCardsScreenViewModel)
    viewModelOf(::AnimalInfoScreenViewModel)
    viewModelOf (::LikedAnimalsScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::BreedsScreenViewModel)
    viewModelOf(::BreedInfoScreenViewModel)
    viewModelOf(::BatchImageViewerScreenViewModel)
    viewModelOf(::SettingsScreenViewModel)
}