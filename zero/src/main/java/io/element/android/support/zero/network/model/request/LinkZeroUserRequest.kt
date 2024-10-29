package io.element.android.support.zero.network.model.request

import io.element.android.support.zero.network.model.response.ZeroMatrixAuthCredentials
import kotlinx.serialization.Serializable

@Serializable
data class LinkZeroUserRequest(val matrixId: String, val matrixAccessToken: String) {
	companion object {
		fun newRequest(matrixCredentials: ZeroMatrixAuthCredentials) =
			LinkZeroUserRequest(matrixCredentials.userId, "not-used")
	}
}
