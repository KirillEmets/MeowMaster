package com.kirillyemets.catapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kirillyemets.catapp.domain.RemoteConfigService
import com.kirillyemets.catapp.ui.theme.FreeJLPTTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val remoteConfigService by inject<RemoteConfigService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remoteConfigService.updateConfigAsync()

        setContent {
            FreeJLPTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Root()
                }
            }
        }
    }
}