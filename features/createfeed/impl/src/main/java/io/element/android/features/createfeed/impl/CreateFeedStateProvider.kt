/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser

open class CreateFeedStateProvider : PreviewParameterProvider<CreateFeedState> {
    override val values: Sequence<CreateFeedState>
        get() = sequenceOf(
            aCreateFeedState(),
            aCreateFeedState(feedText = "This is a small feed text..."),
            aCreateFeedState(genericActionState = AsyncAction.Loading),
            aCreateFeedState(genericActionState = AsyncAction.Failure(Throwable("Failed to post feed."))),
        )
}

internal fun aCreateFeedState(
    feedText: String = "",
    matrixUser: MatrixUser = MatrixUser(userId = UserId("@id:domain"), displayName = "User#1"),
    genericActionState: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = CreateFeedState(
    feedText = feedText,
    matrixUser = matrixUser,
    mediaAttachment = null,
    eventSink = {},
    genericActionState = genericActionState
)
