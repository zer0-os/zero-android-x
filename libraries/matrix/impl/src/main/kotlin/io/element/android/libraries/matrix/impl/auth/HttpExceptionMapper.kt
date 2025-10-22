/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.auth

import io.element.android.support.zero.datastore.converter.AppJson.decodeJson
import io.element.android.support.zero.network.model.response.ApiErrorResponse
import retrofit2.HttpException

fun HttpException.mapZeroException(
    fallback: Exception
): Exception {
    val zeroError = parseError(this)
    return if (zeroError != null) {
        when (zeroError.code) {
            "INVALID_EMAIL_PASSWORD" -> Exception("Incorrect email or password")
            "INVALID_OTP" -> Exception("Invalid or expired OTP")
            "USER_NOT_FOUND" -> Exception("User not found")
            "INVITE_CODE_NOT_FOUND" -> Exception("Invite code not found. Please check your invite message.")
            "PROFILE_PRIMARY_EMAIL_ALREADY_EXISTS" -> Exception("This email is already associated with a ZERO account")
            "PUBLIC_ADDRESS_ALREADY_EXISTS" -> Exception("This wallet is already associated with a ZERO account")
            "INSUFFICIENT_BALANCE" -> Exception("Gas balance is not enough for this transaction")
            "INSUFFICIENT_MEOW_BALANCE" -> Exception("Insufficient Meow Balance")
            else -> Exception(zeroError.message)
        }
    } else {
        fallback
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
