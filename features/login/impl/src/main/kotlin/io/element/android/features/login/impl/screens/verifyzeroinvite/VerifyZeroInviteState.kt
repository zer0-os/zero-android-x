/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.verifyzeroinvite

import io.element.android.libraries.architecture.AsyncAction

data class VerifyZeroInviteState (
    val inviteCode: String,
    val actionState: AsyncAction<Unit>,
    val eventSink: (VerifyZeroInviteEvents) -> Unit
) {
    val submitEnabled: Boolean
        get() = inviteCode.isNotBlank()
}
