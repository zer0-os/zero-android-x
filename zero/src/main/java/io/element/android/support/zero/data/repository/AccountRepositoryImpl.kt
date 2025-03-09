package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import io.element.android.support.zero.network.model.request.ResetUserPasswordRequest
import io.element.android.support.zero.network.service.ZeroAccountService
import io.element.android.support.zero.network.service.ZeroUserService

class AccountRepositoryImpl(
    private val zeroAccountService: ZeroAccountService,
    private val zeroUserService: ZeroUserService,
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
}
