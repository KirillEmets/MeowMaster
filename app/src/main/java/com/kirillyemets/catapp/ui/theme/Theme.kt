package com.kirillyemets.catapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.kirillyemets.catapp.domain.AppPreferences
import com.kirillyemets.catapp.domain.model.ThemeProfile
import org.koin.compose.rememberKoinInject


val DarkColorScheme = darkColorScheme(
    primary = WarmTeal,
    secondary = CoralPink,
//    tertiary = SoftLilac,
//    background = CreamyIvory,
//    surface = CreamyIvory,
    error = MutedGold,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = WarmTeal,
    secondary = CoralPink,
//    tertiary = SoftLilac,
    background = CreamyIvory,
    surface = CreamyIvory,
    error = MutedGold,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DeepIndigo,
    onSurface = DeepIndigo,
    onError = DeepIndigo
)

@Composable
fun appDarkModeActive(): Boolean {
    val appPreferences = rememberKoinInject<AppPreferences>()
    val themeOption by appPreferences.selectedThemeProfileFlow.collectAsState()

    val darkModeActive = when (themeOption) {
        ThemeProfile.Dark -> true
        ThemeProfile.Light -> false
        ThemeProfile.Auto -> isSystemInDarkTheme()
    }

    return darkModeActive
}

@Composable
fun FreeJLPTTheme(
    darkTheme: Boolean = appDarkModeActive(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) DarkColorScheme.surface.toArgb() else colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}