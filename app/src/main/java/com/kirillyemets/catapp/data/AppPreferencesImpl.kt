package com.kirillyemets.catapp.data

import android.content.Context
import androidx.core.content.edit
import com.kirillyemets.catapp.BuildConfig
import com.kirillyemets.catapp.domain.AppPreferences
import com.kirillyemets.catapp.domain.model.ThemeProfile
import kotlinx.coroutines.flow.MutableStateFlow

class AppPreferencesImpl(context: Context) : AppPreferences {
    companion object {
        private const val SELECTED_THEME = "selected_theme"
    }

    private val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override var selectedThemeProfile: ThemeProfile
        get() = sharedPreferences.getString(SELECTED_THEME, null)?.let { stringValue ->
            ThemeProfile.valueOf(stringValue)
        } ?: ThemeProfile.Auto
        set(value) {
            sharedPreferences.edit { putString(SELECTED_THEME, value.name) }
            selectedThemeProfileFlow.value = value
        }

    override val selectedThemeProfileFlow = MutableStateFlow(selectedThemeProfile)
}