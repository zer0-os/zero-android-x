package io.element.android.support.zero.network.model.response.auth

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ZeroNonce(val nonceToken: String, val expiresIn: Long) {
	@Transient val nonceHeader: String = "Nonce $nonceToken"
}
