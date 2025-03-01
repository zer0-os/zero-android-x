package io.element.android.support.zero.data.repository

interface ConversationRepository {
    suspend fun onNewMessageSent(roomId: String, isRoomAChannel: Boolean)
}
