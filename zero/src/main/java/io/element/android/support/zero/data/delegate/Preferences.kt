package io.element.android.support.zero.data.delegate

import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.support.zero.data.model.UserRewards
import io.element.android.support.zero.network.model.response.user.ApiUser

interface Preferences {
    fun zosToken(): String

    suspend fun setZeroToken(token: String)

    fun matrixToken(): String

    suspend fun setMatrixToken(token: String)

    fun userId(): String

    suspend fun setUserId(id: String)

    suspend fun saveUserRewards(rewards: UserRewards)

    fun userRewards(): UserRewards

    suspend fun saveLoggedInUserInfo(info: MatrixUser)

    fun loggedInUserInfo(): MatrixUser?

    fun getCachedUser(id: String): ApiUser?

    fun getCachedUsers(ids: List<String>): List<ApiUser>

    suspend fun cacheUser(user: ApiUser)

    suspend fun cacheUsers(users: List<ApiUser>)
}
