package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.ZeroNotifyMessageRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ZeroConversationService {

	@POST("matrix/message")
	suspend fun notifyMessage(@Body request: ZeroNotifyMessageRequest)
}
