/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import android.adservices.adid.AdId
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

sealed interface FeedDetailsEvents {
    data object RefreshFeed : FeedDetailsEvents
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int) : FeedDetailsEvents
    data object LoadMoreReplies : FeedDetailsEvents
    data class PostReplyTextChanged(val text: String) : FeedDetailsEvents
    data object PostReply : FeedDetailsEvents
    data object SelectMedia : FeedDetailsEvents
    data object RemoveMedia : FeedDetailsEvents
    data class LoadFeedMedia(val mediaId: String): FeedDetailsEvents
    data object DismissFeedMedia: FeedDetailsEvents

    data object HideError : FeedDetailsEvents
}
