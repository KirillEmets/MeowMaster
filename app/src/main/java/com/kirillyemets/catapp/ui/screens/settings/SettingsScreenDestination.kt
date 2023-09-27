package com.kirillyemets.catapp.ui.screens.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kirillyemets.catapp.ui.navigation.Destination

object SettingsScreenDestination : Destination("SettingsScreen") {
    override fun navGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(destination) {
            SettingsScreen()
        }
    }
}