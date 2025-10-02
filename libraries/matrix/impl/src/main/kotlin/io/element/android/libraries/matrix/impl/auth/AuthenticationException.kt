/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import io.element.android.libraries.matrix.api.auth.AuthenticationException
import io.element.android.support.zero.datastore.converter.AppJson.decodeJson
import io.element.android.support.zero.network.model.response.ApiErrorResponse
import org.matrix.rustcomponents.sdk.ClientBuildException
import org.matrix.rustcomponents.sdk.OidcException
import retrofit2.HttpException

fun Throwable.mapAuthenticationException(): AuthenticationException {
    val message = this.message ?: "Unknown error"
    return when (this) {
        is AuthenticationException -> this
        is ClientBuildException -> when (this) {
            is ClientBuildException.Generic -> AuthenticationException.Generic(message)
            is ClientBuildException.InvalidServerName -> AuthenticationException.InvalidServerName(message)
            is ClientBuildException.SlidingSyncVersion -> AuthenticationException.SlidingSyncVersion(message)
            is ClientBuildException.Sdk -> AuthenticationException.Generic(message)
            is ClientBuildException.ServerUnreachable -> AuthenticationException.Generic(message)
            is ClientBuildException.SlidingSync -> AuthenticationException.Generic(message)
            is ClientBuildException.WellKnownDeserializationException -> AuthenticationException.Generic(message)
            is ClientBuildException.WellKnownLookupFailed -> AuthenticationException.Generic(message)
            is ClientBuildException.EventCache -> AuthenticationException.Generic(message)
        }
        is OidcException -> when (this) {
            is OidcException.Generic -> AuthenticationException.Oidc(message)
            is OidcException.CallbackUrlInvalid -> AuthenticationException.Oidc(message)
            is OidcException.Cancelled -> AuthenticationException.Oidc(message)
            is OidcException.MetadataInvalid -> AuthenticationException.Oidc(message)
            is OidcException.NotSupported -> AuthenticationException.Oidc(message)
        }
        // Handling ZERO error cases
        is HttpException -> {
            val zeroError = parseError(this)
            if (zeroError != null) {
                when (zeroError.code) {
                    "INVALID_EMAIL_PASSWORD" -> AuthenticationException.Generic("Incorrect email or password")
                    "INVALID_OTP" -> AuthenticationException.Generic("Invalid or expired OTP")
                    "USER_NOT_FOUND" -> AuthenticationException.Generic("User not found")
                    "INVITE_CODE_NOT_FOUND" -> AuthenticationException.Generic("Invite code not found. Please check your invite message.")
                    "PROFILE_PRIMARY_EMAIL_ALREADY_EXISTS" -> AuthenticationException.Generic("This email is already associated with a ZERO account")
                    "PUBLIC_ADDRESS_ALREADY_EXISTS" -> AuthenticationException.Generic("This wallet is already associated with a ZERO account")
                    else -> AuthenticationException.Generic(zeroError.message)
                }
            } else {
                AuthenticationException.Generic(message)
            }
        }
        else -> AuthenticationException.Generic(message)
    }
}

private fun parseError(exception: HttpException): ApiErrorResponse? {
    return try {
        val errorBody = exception.response()?.errorBody()?.string()
        if (errorBody.isNullOrEmpty()) return null
        errorBody.decodeJson<ApiErrorResponse>()
    } catch (e: Exception) {
        null // fallback if parsing fails
    }
}
