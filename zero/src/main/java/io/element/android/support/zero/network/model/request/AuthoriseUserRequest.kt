package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthoriseUserRequest(val email: String, val password: String) {
	companion object {
		fun newRequest(email: String, password: String) = AuthoriseUserRequest(email, password)
	}
}
