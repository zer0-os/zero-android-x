/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

open class FeedDetailsStateProvider : PreviewParameterProvider<FeedDetailsState> {
    override val values: Sequence<FeedDetailsState>
        get() = sequenceOf(
            aFeedDetailState(),
            aFeedDetailState(genericActionState = AsyncAction.Loading),
            aFeedDetailState(genericActionState = AsyncAction.Failure(Throwable("Failed to post feed."))),
        )
}

internal fun aFeedDetailState(
    zeroFeed: ZeroFeed = ZeroFeed.placeholder,
    zeroRewards: ZeroUserRewards = ZeroUserRewards.empty(),
    matrixUser: MatrixUser = MatrixUser(userId = UserId("@id:domain"), displayName = "User#1"),
    genericActionState: AsyncAction<Unit> = AsyncAction.Uninitialized,
) = FeedDetailsState(
    zeroFeed = zeroFeed,
    userRewards = zeroRewards,
    matrixUser = matrixUser,
    loggedInUserId = "",
    feedComments = emptyList(),
    feedCommentsMediaMap = emptyMap(),
    feedCommentsLinkMetaDataMap = emptyMap(),
    postReplyText = "",
    postReplyAttachment = null,
    eventSink = {},
    genericActionState = genericActionState
)
