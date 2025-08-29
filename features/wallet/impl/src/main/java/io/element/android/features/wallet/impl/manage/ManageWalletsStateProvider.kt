/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId

open class ManageWalletsStateProvider : PreviewParameterProvider<ManageWalletsState> {
    override val values: Sequence<ManageWalletsState>
        get() = sequenceOf(
            aManageWalletsState(),
        )
}

fun aManageWalletsState(
) = ManageWalletsState(
    userId = UserId(""),
    wallets = emptyList(),
    actionState = AsyncAction.Uninitialized,
    eventSink = {}
)
