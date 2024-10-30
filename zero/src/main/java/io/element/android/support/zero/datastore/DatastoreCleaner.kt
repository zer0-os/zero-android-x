package io.element.android.support.zero.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class DatastoreCleaner(private val dataStore: DataStore<Preferences>) {

    suspend fun clean() {
        dataStore.edit { mPrefs ->
            val preferencesKeys = mPrefs.asMap().keys
            preferencesKeys.forEach { mPrefs.remove(it) }
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
