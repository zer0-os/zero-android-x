package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ZeroNotifyMessageRequest(val roomId: String, val sentAt: Long) {
	companion object {
		fun newRequest(roomId: String, sentAt: Long) = ZeroNotifyMessageRequest(roomId, sentAt)
	}
}
