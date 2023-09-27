package com.kirillyemets.catapp.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kirillyemets.catapp.domain.RemoteConfigService

class RemoteConfigServiceImpl(
    private val remoteConfig: FirebaseRemoteConfig
) : RemoteConfigService {

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    override fun getStringValue(key: String): String {
        return remoteConfig.getString(key)
    }

    override fun updateConfigAsync() {
        remoteConfig.fetchAndActivate()
    }
}

