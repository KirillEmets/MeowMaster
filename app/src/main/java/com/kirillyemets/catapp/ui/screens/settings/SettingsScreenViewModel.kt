package com.kirillyemets.catapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.kirillyemets.catapp.analytics.Analytics
import com.kirillyemets.catapp.analytics.AnalyticsService
import com.kirillyemets.catapp.domain.AppPreferences
import com.kirillyemets.catapp.domain.model.ThemeProfile
import com.kirillyemets.catapp.mylibs.combineStateFlow

class SettingsScreenViewModel(
    private val appPreferences: AppPreferences,
    private val analyticsService: AnalyticsService
) : ViewModel() {

    private val currentThemeProfile = appPreferences.selectedThemeProfileFlow

    val state = combineStateFlow(currentThemeProfile) { currentThemeProfile ->
        ViewState(currentThemeProfile)
    }

    fun setThemeProfile(themeProfile: ThemeProfile) {
        appPreferences.selectedThemeProfile = themeProfile
        analyticsService.logClick(Analytics.ClickType.SettingsScreen.ChangeTheme(themeProfile))
    }

    data class ViewState(
        val currentTheme: ThemeProfile
    )
}