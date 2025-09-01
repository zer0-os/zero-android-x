package io.element.android.support.zero.data.repository

import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.support.zero.data.delegate.Preferences
import io.element.android.support.zero.datastore.converter.AppJson.decodeJson
import io.element.android.support.zero.datastore.converter.AppJson.toJson
import io.element.android.support.zero.network.model.request.AddWalletRequest
import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import io.element.android.support.zero.network.model.request.ResetUserPasswordRequest
import io.element.android.support.zero.network.model.response.ApiErrorResponse
import io.element.android.support.zero.network.model.response.wallet.ApiWallet
import io.element.android.support.zero.network.service.ZeroAccountService
import io.element.android.support.zero.network.service.ZeroUserService

class AccountRepositoryImpl(
    private val zeroAccountService: ZeroAccountService,
    private val zeroUserService: ZeroUserService,
    private val preferences: Preferences
): AccountRepository {

    override suspend fun deleteUserAccount() {
        runCatching {
            zeroAccountService.deleteUserAccount()
        }
    }

    override suspend fun linkUserAccount(userMatrixId: String) {
        runCatching {
            val currentUser = zeroUserService.getCurrentUser()
            if (currentUser.matrixId.isNullOrBlank()) {
                val payload = LinkZeroUserRequest.newRequest(matrixUserId = userMatrixId)
                zeroAccountService.linkZeroUser(payload)
            }
        }
    }

    override suspend fun verifyUserPassword(password: String): Boolean {
        return runCatching {
            zeroAccountService.resetAccountPassword(ResetUserPasswordRequest(password)).isSuccessful
        }.getOrDefault(false)
    }

    override suspend fun fetchUserZIds(): List<String> {
        return runCatching {
            zeroAccountService.fetchUserZIds()
        }.getOrDefault(emptyList())
    }

    override suspend fun fetchUserWallets(): List<ApiWallet> {
        return runCatching {
            zeroAccountService.fetchUserWallets().wallets
        }.getOrDefault(emptyList())
    }

    override suspend fun saveLoggedInUserInfo(user: MatrixUser) {
        preferences.saveLoggedInUserInfo(user)
    }

    override fun getLoggedInUser(): MatrixUser? {
        return preferences.loggedInUserInfo()
    }

    override suspend fun addWallet(canAuthenticate: Boolean, token: String): Boolean {
        return runCatching {
            zeroAccountService
                .addWallet(request = AddWalletRequest(canAuthenticate, token))
                .isSuccessful
        }.getOrDefault(false)
    }

    override suspend fun deleteWallet(walletId: String): Boolean {
        return runCatching {
            val request = zeroAccountService.deleteWallet(walletId)
            if (request.isSuccessful) {
                true
            } else {
                val errorJson = request.errorBody()?.string()?.decodeJson<ApiErrorResponse>()
                val errorMessage = if (errorJson?.code == "CANNOT_REMOVE_ONLY_AUTH_METHOD") {
                    "This wallet is the only login method on this account. Add another login method (e.g., email or a different wallet), in order to remove it."
                } else {
                    errorJson?.message ?: "Failed to remove wallet address."
                }
                throw Exception(errorMessage)
            }
        }.getOrElse { throw it }
    }
}
