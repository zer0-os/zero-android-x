/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.element.android.support.zero.datastore.converter.AppJson.decodeJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
internal data class ZeroRemoteConfigs(
    val android: AppRemoteConfigs,
    val iOS: AppRemoteConfigs
)

@Serializable
internal data class AppRemoteConfigs(
    @SerialName("app_version")
    val appVersion: String,
    @SerialName("maintenance_mode")
    val maintenanceModeEnabled: Boolean,
    @SerialName("force_update")
    val forceUpdateEnabled: Boolean
)

object ZeroRemoteConfigsManager {

    // StateFlow for reactive updates
    private val _forceUpdateEnabled = MutableStateFlow(false)
    val forceUpdateEnabled: StateFlow<Boolean> = _forceUpdateEnabled.asStateFlow()

    private val _maintenanceModeEnabled = MutableStateFlow(false)
    val maintenanceModeEnabled: StateFlow<Boolean> = _maintenanceModeEnabled.asStateFlow()

    init {
        setupRemoteConfigs()
    }

    private fun setupRemoteConfigs() {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        initializeRemoteConfigs(remoteConfig)
    }

    private fun initializeRemoteConfigs(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag("RemoteConfig").i("Fetch and activate succeeded")
                    updateValues(remoteConfig)
                    addRemoteConfigObserver(remoteConfig)
                } else {
                    Timber.tag("RemoteConfig").e(task.exception, "Fetch failed")
                }
            }
    }

    private fun addRemoteConfigObserver(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Timber.tag("RemoteConfig").i("Config observer success")
                remoteConfig.activate().addOnCompleteListener {
                    updateValues(remoteConfig)
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Timber.tag("RemoteConfig").e(error, "Config observer failed")
            }
        })
    }

    private fun updateValues(remoteConfig: FirebaseRemoteConfig) {
        val jsonString = remoteConfig.getString("remote_configs")
        if (jsonString.isEmpty()) {
            Timber.tag("RemoteConfig").i("JSON string is empty")
            return
        }
        try {
            val zeroRemoteConfigs = jsonString.decodeJson<ZeroRemoteConfigs>()
            _forceUpdateEnabled.value = zeroRemoteConfigs?.android?.forceUpdateEnabled ?: false
            _maintenanceModeEnabled.value = zeroRemoteConfigs?.android?.maintenanceModeEnabled ?: false
        } catch (e: Exception) {
            Timber.tag("RemoteConfig").e(e, "Failed to parse remote configs json")
        }
    }
}
