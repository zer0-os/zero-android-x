/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Immutable
data class HomeState(
    val matrixUser: MatrixUser,
    val showAvatarIndicator: Boolean,
    val hasNetworkConnection: Boolean,
    val genericActionState: AsyncAction<Unit>,
    val roomListState: RoomListState,
    val channelContentState: ChannelListContentState,
    val allFeedsContentState: FeedListContentState,
    val myFeedsContentState: FeedListContentState,
    val feedMediaMap: Map<String, FeedMedia>,
    val feedLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    val resolvedChannelRoom: RoomId?,
    val snackbarMessage: SnackbarMessage?,
    val canReportBug: Boolean,
    val directLogoutState: DirectLogoutState,
    val shouldShowNewRewardsIntimation: Boolean = true,
    val userRewards: ZeroUserRewards = ZeroUserRewards.empty(),
    val eventSink: (HomeEvents) -> Unit,
) {
    private val displayActions = true

    fun shouldDisplayActions(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT, HomeScreenTab.FEED, HomeScreenTab.PROFILE)
    }

    fun showDisplayMenuItems(selectedHomeTab: HomeScreenTab): Boolean {
        return displayActions &&
            selectedHomeTab in listOf(HomeScreenTab.CHAT)
    }
}
