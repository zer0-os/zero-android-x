package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.MessengerInvite

interface InviteRepository {
    suspend fun validateInvite(inviteCode: String): Boolean

    suspend fun fetchMessengerInvite(): MessengerInvite
}
