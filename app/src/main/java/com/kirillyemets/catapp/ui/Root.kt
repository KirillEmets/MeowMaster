package com.kirillyemets.catapp.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kirillyemets.catapp.R
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.analytics.toAnalyticsScreen
import com.kirillyemets.catapp.ui.navigation.Destination
import com.kirillyemets.catapp.ui.navigation.GlobalNavigator
import com.kirillyemets.catapp.ui.navigation.ProvideGlobalNavigator
import com.kirillyemets.catapp.ui.navigation.allDestinations
import com.kirillyemets.catapp.ui.screens.home.HomeScreenDestination
import com.kirillyemets.catapp.ui.screens.likedanimals.LikedAnimalsDestination
import com.kirillyemets.catapp.ui.screens.search.BreedsScreenDestination
import com.kirillyemets.catapp.ui.screens.settings.SettingsScreenDestination
import org.koin.compose.rememberKoinInject

data class BottomNavItem(
    val destination: Destination,
    val icon: ImageVector? = null,
    val iconId: Int? = null
)

@Composable
fun Root() {
    val navController = rememberNavController()
    val startDestination = if (hasTestScreen()) "test" else HomeScreenDestination.destination
    val globalNavigator = remember { GlobalNavigator(navController) }


    val currentBackStack = navController.currentBackStack.collectAsState().value

    val lastEntry = currentBackStack.lastOrNull()?.destination?.route
    val analytics = rememberKoinInject<AnalyticsService>()
    LaunchedEffect(key1 = lastEntry, block = {
        val analyticsScreen: Analytics.Screen? = allDestinations.find { d ->
            d.destination == lastEntry
        }?.toAnalyticsScreen()

        analyticsScreen?.let {
            analytics.logScreenView(screen = analyticsScreen)
        }
    })

    val bottomNavItems = listOf(
        BottomNavItem(HomeScreenDestination, icon = Icons.Default.Home),
        BottomNavItem(LikedAnimalsDestination, icon = Icons.Outlined.Favorite),
        BottomNavItem(BreedsScreenDestination, icon = Icons.Default.Search),
        BottomNavItem(SettingsScreenDestination, icon = Icons.Default.Settings),
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            BottomAppBar {

                val entry = currentBackStack.reversed()
                    .firstOrNull { entry -> entry.destination.route in bottomNavItems.map { it.destination.destination } }

                val currentTabStartDestination = bottomNavItems.find { item ->
                    entry?.destination?.route == item.destination.destination
                }?.destination ?: HomeScreenDestination

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = item.destination == currentTabStartDestination,
                        onClick = {
                            navController.navigate(item.destination.destination) {

                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }

                        },
                        icon = {
                            if (item.iconId != null) {
                                Icon(painterResource(id = item.iconId), "")
                            } else if (item.icon != null) {
                                Icon(item.icon, "")
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        ProvideGlobalNavigator(globalNavigator = globalNavigator) {
            NavHost(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .paint(
                        painterResource(id = R.drawable.bg),
                        alpha = 0.2f,
                        contentScale = ContentScale.Crop
                    ),
                navController = navController,
                startDestination = startDestination,
                enterTransition = {
                    fadeIn()
                },
                exitTransition = {
                    fadeOut()
                }
            ) {
                allDestinations.forEach {
                    it.navGraph(this)
                }

                testScreen()
            }
        }
    }
}