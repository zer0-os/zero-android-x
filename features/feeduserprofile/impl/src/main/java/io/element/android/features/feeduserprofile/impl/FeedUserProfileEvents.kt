/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

sealed interface FeedUserProfileEvents {
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int) : FeedUserProfileEvents
    data object LoadMoreUserFeeds : FeedUserProfileEvents
    data object ToggleFollowUser : FeedUserProfileEvents
    data object StartDM : FeedUserProfileEvents
    data object ClearStartDMState : FeedUserProfileEvents

    data object HideError : FeedUserProfileEvents
}
