package com.kirillyemets.catapp.domain

interface RemoteConfigService {
    fun getStringValue(key: String): String
    fun updateConfigAsync()
}

object RemoteConfig {
    const val KEY_API_KEY = "apiKey"
}