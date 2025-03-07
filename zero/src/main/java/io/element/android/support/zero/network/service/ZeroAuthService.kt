package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.AuthoriseUserRequest
import io.element.android.support.zero.network.model.request.CreateAndAuthoriseUserRequest
import io.element.android.support.zero.network.model.request.FinaliseCreateAccountRequest
import io.element.android.support.zero.network.model.request.LinkZeroUserRequest
import io.element.android.support.zero.network.model.response.ApiInviter
import io.element.android.support.zero.network.model.response.ZeroAuthCredentials
import io.element.android.support.zero.network.model.response.ZeroNonce
import io.element.android.support.zero.network.model.response.ZeroSSOToken
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ZeroAuthService {
	@POST(value = "api/v2/accounts/login")
	suspend fun authorise(@Body payload: AuthoriseUserRequest): ZeroAuthCredentials

	@GET(value = "accounts/ssoToken")
	suspend fun getSSOToken(): ZeroSSOToken

	@POST(value = "authentication/nonce")
	suspend fun authenticateNonce(): ZeroNonce

	@POST(value = "api/v2/accounts/createAndAuthorize")
	suspend fun createAndAuthorise(
		@Header("Authorization") nonceToken: String,
		@Body payload: CreateAndAuthoriseUserRequest
	): ZeroAuthCredentials

	@POST(value = "api/v2/accounts/finalize")
	suspend fun finaliseSignUp(@Body payload: FinaliseCreateAccountRequest): ApiInviter

	@POST(value = "matrix/link-zero-user")
	suspend fun linkZeroUser(@Body payload: LinkZeroUserRequest)

	@POST(value = "authentication/nonceOrAuthorize")
	suspend fun nonceOrAuthorise(@Header("Authorization") web3Token: String): ZeroAuthCredentials
}
