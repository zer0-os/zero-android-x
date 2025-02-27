package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.ZeroNotifyMessageRequest
import io.element.android.support.zero.network.service.ZeroConversationService
import javax.inject.Inject

class ConversationRepositoryImpl(
    private val zeroConversationService: ZeroConversationService
) : ConversationRepository {

    override suspend fun onNewMessageSent(roomId: String, isRoomAChannel: Boolean) {
        runCatching {
            val sentAt = System.currentTimeMillis()
            zeroConversationService.notifyMessage(
                request = ZeroNotifyMessageRequest.newRequest(roomId, sentAt, isRoomAChannel)
            )
        }
    }
}
