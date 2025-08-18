package io.element.android.support.zero.network.model.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZeroMatrixAuthCredentials(
	@SerialName("user_id") val userId: String = "",
	@SerialName("access_token") val accessToken: String = "",
	@SerialName("home_server") val homeServer: String = "",
	@SerialName("device_id") val deviceId: String = ""
)
