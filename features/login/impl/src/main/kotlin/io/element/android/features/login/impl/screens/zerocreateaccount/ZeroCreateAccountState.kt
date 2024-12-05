/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.SessionId
import kotlinx.parcelize.Parcelize

@Immutable
data class ZeroCreateAccountState(
    val inviteCode: String = "",
    val formState: ZeroCreateAccountFormState,
    val createAccountAction: AsyncData<SessionId>,
    val eventSink: (ZeroCreateAccountEvents) -> Unit
) {
    val submitEnabled: Boolean
        get() = createAccountAction !is AsyncData.Failure &&
            formState.areInputsValid()

}

@Parcelize
data class ZeroCreateAccountFormState(
    val email: String,
    val password: String,
    val confirmPassword: String,
) : Parcelable {

    fun areInputsValid(): Boolean {
        return isEmailValid() && isPasswordValid() && isConfirmPasswordValid()
    }

    fun isEmailValid(): Boolean {
        return email.isNotBlank()
    }

    fun isPasswordValid(): Boolean {
        return password.isNotBlank()
    }

    fun isConfirmPasswordValid(): Boolean {
        return confirmPassword.isNotBlank()
    }

    companion object {
        val Default = ZeroCreateAccountFormState("", "", "")
    }
}
