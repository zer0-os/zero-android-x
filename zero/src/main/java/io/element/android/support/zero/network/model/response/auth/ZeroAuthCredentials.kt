package io.element.android.support.zero.network.model.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class ZeroAuthCredentials(
	val accessToken: String = "",
	val chatAccessToken: String = "",
	val expiresIn: Long,
	val identityToken: String = ""
)
