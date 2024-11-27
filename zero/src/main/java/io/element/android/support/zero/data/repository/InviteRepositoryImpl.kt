package io.element.android.support.zero.data.repository

import io.element.android.support.zero.data.conversion.toModel
import io.element.android.support.zero.data.model.MessengerInvite
import io.element.android.support.zero.network.service.ZeroInviteService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InviteRepositoryImpl(
    private val zeroInviteService: ZeroInviteService
): InviteRepository {

    private val _messengerInvite = MutableStateFlow(MessengerInvite.empty())
    override val messengerInvite: StateFlow<MessengerInvite> = _messengerInvite

    override suspend fun validateInvite(inviteCode: String) {
        zeroInviteService.validateInvite(inviteCode)
    }

    override suspend fun fetchMessengerInvite() {
        val apiInvite = zeroInviteService.fetchMessengerInvite()
        val messengerInvite = apiInvite.toModel()
        _messengerInvite.emit(messengerInvite)
    }
}
