package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class FinaliseCreateAccountRequest(
	val inviteCode: String,
	val name: String,
	val userId: String,
	val profileImage: String? = null
) {
	companion object {
		fun newRequest(inviteCode: String, name: String, userId: String, profileImage: String? = null) =
			FinaliseCreateAccountRequest(inviteCode, name, userId, profileImage)
	}
}
