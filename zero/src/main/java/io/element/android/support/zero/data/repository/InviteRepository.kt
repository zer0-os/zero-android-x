package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.model.MessengerInvite
import kotlinx.coroutines.flow.StateFlow

interface InviteRepository {
    val messengerInvite: StateFlow<MessengerInvite>

    suspend fun validateInvite(inviteCode: String)

    suspend fun fetchMessengerInvite()
}
