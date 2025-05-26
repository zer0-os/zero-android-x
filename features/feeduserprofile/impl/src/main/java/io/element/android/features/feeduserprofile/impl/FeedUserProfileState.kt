/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Immutable
data class FeedUserProfileState(
    val userProfile: FeedUserProfileView?,
    val userRewards: ZeroUserRewards,
    val userFeeds: List<ZeroFeed>,
    val userFeedsMediaMap: Map<String, FeedMedia>,
    val userFeedsLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    val isUserFollowed: Boolean?,
    val isMyOwnProfile: Boolean,
    val dmRoomId: RoomId?,
    val startDmActionState: AsyncAction<RoomId>,
    val eventSink: (FeedUserProfileEvents) -> Unit,
    val genericActionState: AsyncAction<Unit>,
) {
    val shouldShowFollowButton: Boolean
        get() = !isMyOwnProfile && isUserFollowed != null
}
