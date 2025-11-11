package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.auth.AuthenticationChallenge
import io.element.android.support.zero.data.model.AuthSSOToken
import io.element.android.support.zero.network.model.response.auth.ApiAuthenticationChallenge
import io.element.android.support.zero.network.model.response.auth.ZeroSSOToken

internal fun ZeroSSOToken.toModel() = AuthSSOToken(token)

fun ApiAuthenticationChallenge.toModel() = AuthenticationChallenge(message, nonce)
fun AuthenticationChallenge.toApi() = ApiAuthenticationChallenge(message, nonce)
