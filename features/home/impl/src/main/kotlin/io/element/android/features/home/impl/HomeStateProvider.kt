/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.channel.ChannelListContentState
import io.element.android.features.home.impl.channel.aPlaceholderChannelListContentState
import io.element.android.features.home.impl.feed.FeedListContentState
import io.element.android.features.home.impl.feed.aPlaceholderFeedListContentState
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.roomlist.RoomListStateProvider
import io.element.android.features.home.impl.roomlist.aRoomListState
import io.element.android.features.home.impl.roomlist.aRoomsContentState
import io.element.android.features.home.impl.roomlist.generateRoomListRoomSummaryList
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.home.impl.wallet.aWalletContentState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.logout.api.direct.aDirectLogoutState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarMessage
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.ui.strings.CommonStrings

open class HomeStateProvider : PreviewParameterProvider<HomeState> {
    override val values: Sequence<HomeState>
        get() = sequenceOf(
            aHomeState(),
            aHomeState(hasNetworkConnection = false),
            aHomeState(snackbarMessage = SnackbarMessage(CommonStrings.common_verification_complete)),
            aHomeState(
                isSpaceFeatureEnabled = true,
                roomListState = aRoomListState(
                    // Add more rooms to see the blur effect under the NavigationBar
                    contentState = aRoomsContentState(
                        summaries = generateRoomListRoomSummaryList(),
                    )
                ),
            ),
            aHomeState(
                isSpaceFeatureEnabled = true,
                currentHomeNavigationBarItem = HomeNavigationBarItem.Spaces,
            ),
        ) + RoomListStateProvider().values.map {
            aHomeState(roomListState = it)
        }
}

internal fun aHomeState(
    matrixUser: MatrixUser = MatrixUser(userId = UserId("@id:domain"), displayName = "User#1"),
    showAvatarIndicator: Boolean = false,
    hasNetworkConnection: Boolean = true,
    genericActionState: AsyncAction<Unit> = AsyncAction.Uninitialized,
    snackbarMessage: SnackbarMessage? = null,
    currentHomeNavigationBarItem: HomeNavigationBarItem = HomeNavigationBarItem.Chats,
    roomListState: RoomListState = aRoomListState(),
    channelContentState: ChannelListContentState = aPlaceholderChannelListContentState(),
    allFeedsContentState: FeedListContentState = aPlaceholderFeedListContentState(),
    myFeedsContentState: FeedListContentState = aPlaceholderFeedListContentState(),
    feedMediaMap: Map<String, FeedMedia> = emptyMap(),
    feedLinkMetaDataMap: Map<String, ZeroLinkPreview> = emptyMap(),
    resolvedChannelRoom: RoomId? = null,
    canReportBug: Boolean = true,
    isSpaceFeatureEnabled: Boolean = false,
    directLogoutState: DirectLogoutState = aDirectLogoutState(),
    walletContentState: WalletContentState = aWalletContentState(),
    eventSink: (HomeEvents) -> Unit = {}
) = HomeState(
    matrixUser = matrixUser,
    showAvatarIndicator = showAvatarIndicator,
    hasNetworkConnection = hasNetworkConnection,
    genericActionState = genericActionState,
    currentHomeNavigationBarItem = currentHomeNavigationBarItem,
    roomListState = roomListState,
    channelContentState = channelContentState,
    allFeedsContentState = allFeedsContentState,
    myFeedsContentState = myFeedsContentState,
    feedMediaMap = feedMediaMap,
    feedLinkMetaDataMap = feedLinkMetaDataMap,
    resolvedChannelRoom = resolvedChannelRoom,
    snackbarMessage = snackbarMessage,
    canReportBug = canReportBug,
    directLogoutState = directLogoutState,
    isSpaceFeatureEnabled = isSpaceFeatureEnabled,
    walletContentState = walletContentState,
    eventSink = eventSink,
)
