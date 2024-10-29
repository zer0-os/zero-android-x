package io.element.android.support.zero.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.element.android.support.zero.common.extension.runBlockingWithTimeOut
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
internal class AppPreferences(private val dataStore: DataStore<Preferences>) {

    suspend fun setZeroToken(token: String) {
        dataStore.edit { preferences -> preferences[ZOS_TOKEN] = token }
    }

    suspend fun setMatrixToken(token: String) {
        dataStore.edit { preferences -> preferences[MATRIX_TOKEN] = token }
    }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences -> preferences[USER_ID] = id }
    }

    fun zosToken(): String =
        runBlockingWithTimeOut {
            dataStore.data.map { preferences -> preferences[ZOS_TOKEN] }.firstOrNull()
        }
            ?: ""

    fun matrixToken(): String =
        runBlockingWithTimeOut {
            dataStore.data.map { preferences -> preferences[MATRIX_TOKEN] }.firstOrNull()
        }
            ?: ""

    fun userId(): String =
        runBlockingWithTimeOut {
            dataStore.data.map { preferences -> preferences[USER_ID] }.firstOrNull()
        }
            ?: ""

    internal companion object {
        internal val USER_ID = stringPreferencesKey("USER_ID")
        internal val ZOS_TOKEN = stringPreferencesKey("ZOS_TOKEN")
        internal val MATRIX_TOKEN = stringPreferencesKey("MATRIX_TOKEN")
    }
}
