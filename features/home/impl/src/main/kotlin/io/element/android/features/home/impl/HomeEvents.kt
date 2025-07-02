/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

sealed interface HomeEvents {
    data class SelectHomeNavigationBarItem(val item: HomeNavigationBarItem) : HomeEvents

    data class DismissRewardsIntimation(val immediate: Boolean = true) : HomeEvents
    data object HideError : HomeEvents

    sealed interface HomeChannelEvents : HomeEvents
    data class OpenChannel(val channel: HomeScreenChannel) : HomeChannelEvents

    sealed interface HomeFeedEvents: HomeEvents
    data class LoadMoreFeeds(val currentFeeds: List<ZeroFeed>, val followingFeeds: Boolean): HomeFeedEvents
    data class RefreshFeeds(val followingFeeds: Boolean): HomeFeedEvents
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int): HomeFeedEvents

    sealed interface HomeProfileEvents: HomeEvents
    data class LoadMoreMyFeeds(val currentFeeds: List<ZeroFeed>): HomeProfileEvents
    data object RefreshMyFeeds: HomeProfileEvents
}
