package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateAndAuthoriseUserRequest(val user: CreateUserInfo? = null, val inviteSlug: String) {
	@Serializable
	data class CreateUserInfo(val email: String, val password: String, val handle: String = "")

	companion object {
		fun newRequest(email: String, password: String, inviteSlug: String) =
			CreateAndAuthoriseUserRequest(CreateUserInfo(email, password, email), inviteSlug)
	}
}
