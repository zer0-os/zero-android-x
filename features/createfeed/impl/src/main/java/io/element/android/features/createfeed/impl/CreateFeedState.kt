/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.CreateFeedMediaAttachment

@Immutable
data class CreateFeedState(
    val feedText: String,
    val matrixUser: MatrixUser,
    val mediaAttachment: CreateFeedMediaAttachment?,

    val eventSink: (CreateFeedEvents) -> Unit,
    val genericActionState: AsyncAction<Unit>,
) {
    val canSendPost: Boolean
        get() = feedText.isNotBlank()
}
