package io.element.android.support.zero.data.repository

interface AccountRepository {
    suspend fun deleteUserAccount()

    suspend fun linkUserAccount(userMatrixId: String)

    suspend fun verifyUserPassword(password: String): Boolean

    suspend fun fetchUserZIds(): List<String>
}
