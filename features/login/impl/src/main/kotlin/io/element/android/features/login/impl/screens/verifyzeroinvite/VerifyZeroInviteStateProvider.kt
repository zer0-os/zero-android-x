/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.verifyzeroinvite

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction

open class VerifyZeroInviteStateProvider : PreviewParameterProvider<VerifyZeroInviteState> {
    override val values: Sequence<VerifyZeroInviteState>
        get() = sequenceOf(
            aVerifyZeroInviteState()
        )
}

private fun aVerifyZeroInviteState(
    inviteCode: String = "",
    actionState: AsyncAction<Unit> = AsyncAction.Uninitialized
) = VerifyZeroInviteState(
    inviteCode = inviteCode,
    actionState = actionState,
    eventSink = {}
)
