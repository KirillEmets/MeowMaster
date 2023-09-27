package com.kirillyemets.catapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigator = staticCompositionLocalOf<GlobalNavigator> {
    throw IllegalAccessException("GlobalNavigator have not been provided in the composition")
}

@Composable
fun ProvideGlobalNavigator(globalNavigator: GlobalNavigator, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavigator provides globalNavigator) {
        content()
    }
}

class GlobalNavigator(
    val controller: NavHostController
)

@Composable
fun localNavController() = LocalNavigator.current.controller