/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction

sealed interface HomeEvents {
    data class SelectHomeNavigationBarItem(val item: HomeNavigationBarItem) : HomeEvents

    data class DismissRewardsIntimation(val immediate: Boolean = true) : HomeEvents
    data object ClaimRewards : HomeEvents
    data object HideError : HomeEvents

    sealed interface ChannelEvents: HomeEvents {
        data class OpenChannel(val channel: HomeScreenChannel) : ChannelEvents
        data object ChannelRoomOpened : ChannelEvents
    }

    sealed interface FeedEvents: HomeEvents {
        data class LoadMoreFeeds(val currentFeeds: List<ZeroFeed>, val followingFeeds: Boolean) : FeedEvents
        data class RefreshFeeds(val followingFeeds: Boolean) : FeedEvents
        data class AddMeowToFeed(val feed: ZeroFeed, val meowCount: Int) : FeedEvents
        data class LoadFeedMedia(val mediaId: String) : FeedEvents
        data object DismissFeedMedia : FeedEvents
    }

    sealed interface ProfileEvents: HomeEvents {
        data class LoadMoreMyFeeds(val currentFeeds: List<ZeroFeed>) : ProfileEvents
        data object RefreshMyFeeds : ProfileEvents
    }

    sealed interface WalletEvents: HomeEvents {
        data class LoadMoreTokens(val currentTokens: List<ZeroWalletToken>) : WalletEvents
        data class LoadMoreTransactions(val currentTransactions: List<ZeroWalletTransaction>) : WalletEvents
        data class ViewWalletTransaction(val transactionId: String, val chainId: Long? = null) : WalletEvents
        data object OnWalletTransactionViewed : WalletEvents
        data object ToggleWalletBalance : WalletEvents
        data object RefreshWalletBalance : WalletEvents
        data class StakePoolSelected(val pool: HomeStakePool) : WalletEvents
        data class StakeAmount(val amount: String) : WalletEvents
        data class UnstakeAmount(val amount: String) : WalletEvents
        data object DismissStakingSheet : WalletEvents
        data object ClaimStakingRewards : WalletEvents
        data object RefreshWallet : WalletEvents
    }

    data class SwitchToAccount(val sessionId: SessionId) : HomeEvents
}
