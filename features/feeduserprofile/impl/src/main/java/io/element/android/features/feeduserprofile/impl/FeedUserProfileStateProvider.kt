/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

open class FeedUserProfileStateProvider : PreviewParameterProvider<FeedUserProfileState> {
    override val values: Sequence<FeedUserProfileState>
        get() = sequenceOf(
            aFeedUserProfileState(),
        )
}

internal fun aFeedUserProfileState(
    userProfileView: FeedUserProfileView = FeedUserProfileView.placeholder,
    zeroRewards: ZeroUserRewards = ZeroUserRewards.empty(),
    genericActionState: AsyncData<Unit> = AsyncData.Uninitialized,
) = FeedUserProfileState(
    userProfile = userProfileView,
    userRewards = zeroRewards,
    userFeeds = emptyList(),
    userFeedsMediaMap = emptyMap(),
    userFeedsLinkMetaDataMap = emptyMap(),
    isUserFollowed = false,
    isMyOwnProfile = false,
    eventSink = {},
    genericActionState = genericActionState
)
