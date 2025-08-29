package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.support.zero.network.model.response.wallet.ApiWallet

interface AccountRepository {
    suspend fun deleteUserAccount()

    suspend fun linkUserAccount(userMatrixId: String)

    suspend fun verifyUserPassword(password: String): Boolean

    suspend fun fetchUserZIds(): List<String>

    suspend fun fetchUserWallets(): List<ApiWallet>

    suspend fun saveLoggedInUserInfo(user: MatrixUser)

    fun getLoggedInUser(): MatrixUser?
}
