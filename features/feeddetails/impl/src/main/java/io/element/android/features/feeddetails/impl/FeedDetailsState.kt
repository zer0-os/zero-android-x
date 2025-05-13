/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.CreateFeedMediaAttachment
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Immutable
data class FeedDetailsState(
    val zeroFeed: ZeroFeed,
    val userRewards: ZeroUserRewards,
    val matrixUser: MatrixUser,
    val loggedInUserId: String,
    val feedComments: List<ZeroFeed>,
    val feedCommentsMediaMap: Map<String, FeedMedia>,
    val postReplyText: String,
    val postReplyAttachment: CreateFeedMediaAttachment?,

    val eventSink: (FeedDetailsEvents) -> Unit,
    val genericActionState: AsyncData<Unit>,
) {
    val canPostReply: Boolean
        get() = postReplyText.isNotBlank()
}
