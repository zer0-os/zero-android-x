package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.service.ZeroAccountService

class AccountRepositoryImpl(
    private val zeroAccountService: ZeroAccountService,
): AccountRepository {

    override suspend fun deleteUserAccount() {
        runCatching {
            zeroAccountService.deleteUserAccount()
        }
    }
}
