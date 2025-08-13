package io.element.android.support.zero.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.support.zero.common.extension.runBlockingWithTimeOut
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.datastore.converter.AppJson.decodeJson
import io.element.android.support.zero.datastore.converter.AppJson.toJson
import io.element.android.support.zero.network.model.response.ApiUser
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    suspend fun setZeroToken(token: String) {
        dataStore.edit { preferences -> preferences[ZOS_TOKEN] = token }
    }

    suspend fun setMatrixToken(token: String) {
        dataStore.edit { preferences -> preferences[MATRIX_TOKEN] = token }
    }

    suspend fun setUserId(id: String) {
        dataStore.edit { preferences -> preferences[USER_ID] = id }
    }

    suspend fun saveUserRewards(userRewards: UserRewards) {
        dataStore.edit { preferences -> preferences[USER_REWARDS] = userRewards.toJson() }
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

    fun userRewards(): UserRewards =
        runBlockingWithTimeOut {
            dataStore.data.map { preferences ->
                preferences[USER_REWARDS]
            }.firstOrNull()?.decodeJson()
        } ?: UserRewards.empty()

    suspend fun saveLoggedInUserInfo(info: MatrixUser) {
        dataStore.edit { preferences -> preferences[LOGGED_IN_USER_INFO] = info.toJson() }
    }

    fun loggedInUserInfo(): MatrixUser? =
        runBlockingWithTimeOut {
            dataStore.data.map { preferences ->
                preferences[LOGGED_IN_USER_INFO]
            }.firstOrNull()?.decodeJson()
        }

    fun getCachedUser(id: String): ApiUser? =
        runBlockingWithTimeOut {
            getAllCachedUsers().firstOrNull { it.matrixId == id }
        }

    fun getCachedUsers(ids: List<String>): List<ApiUser> =
        runBlockingWithTimeOut {
            getAllCachedUsers().filter { it.matrixId in ids }
        } ?: emptyList()

    suspend fun cacheUser(user: ApiUser) {
        cacheUsers(listOf(user))
    }

    suspend fun cacheUsers(users: List<ApiUser>) {
        val updatedList = (getAllCachedUsers() + users)
            .distinctBy { it.matrixId } // Ensure uniqueness
        dataStore.edit { preferences ->
            preferences[CACHED_ZERO_USERS] = updatedList.toJson()
        }
    }

    private suspend fun getAllCachedUsers(): List<ApiUser> {
        return dataStore.data
            .map { it[CACHED_ZERO_USERS] }
            .firstOrNull()
            ?.decodeJson()
            ?: emptyList()
    }

    internal companion object {
        internal val USER_ID = stringPreferencesKey("USER_ID")
        internal val ZOS_TOKEN = stringPreferencesKey("ZOS_TOKEN")
        internal val MATRIX_TOKEN = stringPreferencesKey("MATRIX_TOKEN")
        internal val USER_REWARDS = stringPreferencesKey("USER_REWARDS")
        internal val LOGGED_IN_USER_INFO = stringPreferencesKey("LOGGED_IN_USER_INFO")
        internal val CACHED_ZERO_USERS = stringPreferencesKey("CACHED_ZERO_USERS")
    }
}
