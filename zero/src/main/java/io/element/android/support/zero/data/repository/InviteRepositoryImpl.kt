package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.model.MessengerInvite
import io.element.android.support.zero.network.service.ZeroInviteService

class InviteRepositoryImpl(
    private val zeroInviteService: ZeroInviteService
): InviteRepository {

    override suspend fun validateInvite(inviteCode: String): Boolean {
        val result = zeroInviteService.validateInvite(inviteCode)
        return result.isSuccessful
    }

    override suspend fun fetchMessengerInvite(): MessengerInvite {
        return runCatching {
            val apiInvite = zeroInviteService.fetchMessengerInvite()
            apiInvite.toModel()
        }.getOrElse { MessengerInvite.empty() }
    }
}
