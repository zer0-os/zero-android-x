package io.element.android.support.zero.data.delegate

import io.element.android.support.zero.data.model.UserRewards

interface Preferences {
    fun zosToken(): String

    suspend fun setZeroToken(token: String)

    fun matrixToken(): String

    suspend fun setMatrixToken(token: String)

    fun userId(): String

    suspend fun setUserId(id: String)

    suspend fun saveUserRewards(rewards: UserRewards)

    fun userRewards(): UserRewards
}
