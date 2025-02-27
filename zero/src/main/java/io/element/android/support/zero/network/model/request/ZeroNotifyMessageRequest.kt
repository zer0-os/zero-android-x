package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ZeroNotifyMessageRequest(val roomId: String, val sentAt: Long, val type: String) {
    companion object {
        fun newRequest(roomId: String, sentAt: Long, isRoomAChannel: Boolean) =
            ZeroNotifyMessageRequest(
                roomId,
                sentAt,
                if (isRoomAChannel) "channel" else "group"
            )
    }
}
