package io.element.android.support.zero.data.delegate

import io.element.android.support.zero.datastore.AppPreferences

class PreferencesImpl(private val appPreferences: AppPreferences) :
    Preferences {
    override fun zosToken() = appPreferences.zosToken()

    override suspend fun setZeroToken(token: String) {
        appPreferences.setZeroToken(token)
    }

    override fun matrixToken() = appPreferences.matrixToken()

    override suspend fun setMatrixToken(token: String) {
        appPreferences.setMatrixToken(token)
    }

    override fun userId() = appPreferences.userId()

    override suspend fun setUserId(id: String) {
        appPreferences.setUserId(id)
    }
}
