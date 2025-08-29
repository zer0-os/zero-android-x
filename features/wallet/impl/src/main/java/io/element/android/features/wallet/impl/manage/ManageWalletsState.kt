/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.core.UserId

@Immutable
data class ManageWalletsState(
    val userId: UserId,

    val eventSink: (ManageWalletsEvents) -> Unit
)
