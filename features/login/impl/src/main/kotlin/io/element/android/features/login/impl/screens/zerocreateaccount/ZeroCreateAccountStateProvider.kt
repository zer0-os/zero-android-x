/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncData

open class ZeroCreateAccountStateProvider : PreviewParameterProvider<ZeroCreateAccountState> {
    override val values: Sequence<ZeroCreateAccountState>
        get() = sequenceOf(
            aZeroCreateAccountState(),
            // Loading
            aZeroCreateAccountState().copy(createAccountAction = AsyncData.Loading()),
            // Error
            aZeroCreateAccountState().copy(createAccountAction = AsyncData.Failure(Exception("An error occurred"))),
        )
}

fun aZeroCreateAccountState() = ZeroCreateAccountState(
    inviteCode = "",
    formState = ZeroCreateAccountFormState.Default,
    createAccountAction = AsyncData.Uninitialized,
    loginFlow = AsyncData.Uninitialized,
    eventSink = {}
)
