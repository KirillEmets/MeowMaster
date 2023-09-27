package com.kirillyemets.catapp.domain

import com.kirillyemets.catapp.domain.model.ThemeProfile
import kotlinx.coroutines.flow.StateFlow

interface AppPreferences {
    var selectedThemeProfile: ThemeProfile
    val selectedThemeProfileFlow: StateFlow<ThemeProfile>
}