/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction

sealed interface HomeEvents {
    data class SelectHomeNavigationBarItem(val item: HomeNavigationBarItem) : HomeEvents

    data class DismissRewardsIntimation(val immediate: Boolean = true) : HomeEvents
    data object ClaimRewards : HomeEvents
    data object HideError : HomeEvents

    sealed interface HomeChannelEvents : HomeEvents
    data class OpenChannel(val channel: HomeScreenChannel) : HomeChannelEvents
    data object ChannelRoomOpened : HomeChannelEvents

    sealed interface HomeFeedEvents: HomeEvents
    data class LoadMoreFeeds(val currentFeeds: List<ZeroFeed>, val followingFeeds: Boolean): HomeFeedEvents
    data class RefreshFeeds(val followingFeeds: Boolean): HomeFeedEvents
    data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int): HomeFeedEvents
    data class LoadFeedMedia(val mediaId: String): HomeFeedEvents
    data object DismissFeedMedia: HomeFeedEvents

    sealed interface HomeProfileEvents: HomeEvents
    data class LoadMoreMyFeeds(val currentFeeds: List<ZeroFeed>): HomeProfileEvents
    data object RefreshMyFeeds: HomeProfileEvents

    sealed interface HomeWalletEvents: HomeEvents
    data class LoadMoreTokens(val currentTokens: List<ZeroWalletToken>): HomeWalletEvents
    data class LoadMoreTransactions(val currentTransactions: List<ZeroWalletTransaction>): HomeWalletEvents
    data class ViewWalletTransaction(val transactionId: String): HomeWalletEvents
    data object OnWalletTransactionViewed: HomeWalletEvents
    data object ToggleWalletBalance: HomeWalletEvents
    data object RefreshWalletBalance: HomeWalletEvents
    data class StakePoolSelected(val pool: HomeStakePool): HomeWalletEvents
    data class StakeAmount(val amount: String): HomeWalletEvents
    data class UnstakeAmount(val amount: String): HomeWalletEvents
    data object DismissStakingSheet: HomeWalletEvents
    data object ClaimStakingRewards: HomeWalletEvents
    data object RefreshWallet: HomeWalletEvents
}
