package io.element.android.support.zero.data.conversion

import io.element.android.support.zero.data.model.AuthSSOToken
import io.element.android.support.zero.network.model.response.ZeroSSOToken

fun ZeroSSOToken.toModel() = AuthSSOToken(token)
