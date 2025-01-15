/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

sealed interface ZeroCreateAccountEvents {
    data class SetEmail(val email: String) : ZeroCreateAccountEvents
    data class SetPassword(val password: String) : ZeroCreateAccountEvents
    data class SetConfirmPassword(val confirmPassword: String) : ZeroCreateAccountEvents
    data object Submit : ZeroCreateAccountEvents
    data object ClearError : ZeroCreateAccountEvents
    data object OpenLogin : ZeroCreateAccountEvents
    data class ToggleWeb3Modal(val show: Boolean) : ZeroCreateAccountEvents
}
