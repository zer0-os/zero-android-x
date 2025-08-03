package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser

interface AccountRepository {
    suspend fun deleteUserAccount()

    suspend fun linkUserAccount(userMatrixId: String)

    suspend fun verifyUserPassword(password: String): Boolean

    suspend fun fetchUserZIds(): List<String>

    suspend fun saveLoggedInUserInfo(user: MatrixUser)

    fun getLoggedInUser(): MatrixUser?
}
