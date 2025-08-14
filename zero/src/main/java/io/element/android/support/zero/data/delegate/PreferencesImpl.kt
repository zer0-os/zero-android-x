package io.element.android.support.zero.data.delegate

import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.datastore.AppPreferences
import io.element.android.support.zero.network.model.response.user.ApiUser

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

    override suspend fun saveUserRewards(rewards: UserRewards) {
        appPreferences.saveUserRewards(rewards)
    }

    override fun userRewards(): UserRewards = appPreferences.userRewards()

    override suspend fun saveLoggedInUserInfo(info: MatrixUser) {
        appPreferences.saveLoggedInUserInfo(info)
    }

    override fun loggedInUserInfo() = appPreferences.loggedInUserInfo()

    override fun getCachedUser(id: String): ApiUser? = appPreferences.getCachedUser(id)

    override fun getCachedUsers(ids: List<String>): List<ApiUser> = appPreferences.getCachedUsers(ids)

    override suspend fun cacheUser(user: ApiUser) {
        appPreferences.cacheUser(user)
    }

    override suspend fun cacheUsers(users: List<ApiUser>) {
        appPreferences.cacheUsers(users)
    }
}
