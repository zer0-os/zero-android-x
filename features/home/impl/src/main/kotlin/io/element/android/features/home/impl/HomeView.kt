/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.channel.ChannelListEvents
import io.element.android.features.home.impl.channel.HomeChannelListContentView
import io.element.android.features.home.impl.components.ClaimRewardsSheet
import io.element.android.features.home.impl.components.HomeFabButton
import io.element.android.features.home.impl.components.HomeScreenTabView
import io.element.android.features.home.impl.components.HomeScreenTopBar
import io.element.android.features.home.impl.components.WalletReceiveTokenSheet
import io.element.android.features.home.impl.components.WalletStakingSheet
import io.element.android.features.home.impl.feed.FeedListEvents
import io.element.android.features.home.impl.feed.HomeFeedListContentView
import io.element.android.features.home.impl.model.ChannelsScreenTab
import io.element.android.features.home.impl.model.HomeScreenTab
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.notification.HomeNotificationListContentView
import io.element.android.features.home.impl.roomlist.RoomListContentView
import io.element.android.features.home.impl.roomlist.RoomListContextMenu
import io.element.android.features.home.impl.roomlist.RoomListDeclineInviteMenu
import io.element.android.features.home.impl.roomlist.RoomListEvents
import io.element.android.features.home.impl.roomlist.RoomListMenuAction
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.search.RoomListSearchView
import io.element.android.features.home.impl.wallet.HomeWalletContent
import io.element.android.features.networkmonitor.api.ui.ConnectivityIndicatorContainer
import io.element.android.libraries.androidutils.throttler.FirstThrottler
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ModalBottomSheet
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.extension.getActivity
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.state.StateBus
import io.element.android.support.zero.common.ui.component.feed.FeedMediaPreview

@Composable
fun HomeView(
    homeState: HomeState,
    onRoomClick: (RoomId) -> Unit,
    onSettingsClick: () -> Unit,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onSearchUserClick: () -> Unit,
    onRoomSettingsClick: (roomId: RoomId) -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onReportRoomClick: (roomId: RoomId) -> Unit,
    onDeclineInviteAndBlockUser: (roomSummary: RoomListRoomSummary) -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onUserProfileClick: () -> Unit,
    onCreateFeedClick: () -> Unit,
    onSendWalletToken: () -> Unit,
    modifier: Modifier = Modifier,
    acceptDeclineInviteView: @Composable () -> Unit,
) {
    val roomListState: RoomListState = homeState.roomListState
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val firstThrottler = remember { FirstThrottler(300, coroutineScope) }

    val resolvedChannelRoomId by remember(homeState.channelListState.resolvedChannelRoom) {
        derivedStateOf { homeState.channelListState.resolvedChannelRoom }
    }
    val walletTransactionUrl by remember(homeState.walletContentState.walletTransactionUrlState) {
        derivedStateOf { homeState.walletContentState.walletTransactionUrlState }
    }
    resolvedChannelRoomId?.let {
        homeState.channelListState.eventSink(ChannelListEvents.ChannelRoomOpened)
        onRoomClick(it)
    }
    walletTransactionUrl.dataOrNull()?.let {
        context.openExternalUri(it)
        homeState.eventSink(HomeEvents.WalletEvents.OnWalletTransactionViewed)
    }

    val selectedHomeNavigationTab = rememberSaveable { mutableStateOf(HomeScreenTab.CHAT) }
    val selectedChannelsTab = rememberSaveable { mutableStateOf(ChannelsScreenTab.CHANNELS) }

    BackHandler {
        if (selectedHomeNavigationTab.value != HomeScreenTab.CHAT) {
            selectedHomeNavigationTab.value = HomeScreenTab.CHAT
        } else {
            context.getActivity()?.finishAffinity()
        }
    }

    ConnectivityIndicatorContainer(
        modifier = modifier,
        isOnline = homeState.hasNetworkConnection,
    ) { contentModifier ->
        Box {
            if (roomListState.contextMenu is RoomListState.ContextMenu.Shown) {
                RoomListContextMenu(
                    contextMenu = roomListState.contextMenu,
                    canReportRoom = roomListState.canReportRoom,
                    eventSink = roomListState.eventSink,
                    onRoomSettingsClick = onRoomSettingsClick,
                    onReportRoomClick = onReportRoomClick,
                )
            }
            if (roomListState.declineInviteMenu is RoomListState.DeclineInviteMenu.Shown) {
                RoomListDeclineInviteMenu(
                    menu = roomListState.declineInviteMenu,
                    canReportRoom = roomListState.canReportRoom,
                    eventSink = roomListState.eventSink,
                    onDeclineAndBlockClick = onDeclineInviteAndBlockUser,
                )
            }

            //LeaveRoomView(state = roomListState.leaveRoomState)

            HomeScaffold(
                state = homeState,
                selectedHomeNavigationTab = selectedHomeNavigationTab.value,
                selectedChannelContentTab = selectedChannelsTab.value,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
                onOpenSettings = { if (firstThrottler.canHandle()) onSettingsClick() },
                onCreateRoomClick = { if (firstThrottler.canHandle()) onCreateRoomClick() },
                onSearchUserClick = { if (firstThrottler.canHandle()) onSearchUserClick() },
                onMenuActionClick = onMenuActionClick,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                onUserProfileClick = onUserProfileClick,
                onCreateFeedClick = onCreateFeedClick,
                onSendWalletToken = onSendWalletToken,
                onHomeNavTabSelected = {
                    // clear room filters just to clean content
                    homeState.roomListState.filtersState.clearFilters()
                    selectedChannelsTab.value = ChannelsScreenTab.CHANNELS
                    selectedHomeNavigationTab.value = it
                },
                onChannelsContentTabSelected = { selectedChannelsTab.value = it },
                modifier = contentModifier,
            )
            // This overlaid view will only be visible when state.displaySearchResults is true
            RoomListSearchView(
                state = roomListState.searchState,
                channelsListState = homeState.channelListState.contentState,
                eventSink = roomListState.eventSink,
                roomMappedUserProStatus = roomListState.roomMappedUserProStatus,
                hideInvitesAvatars = roomListState.hideInvitesAvatars,
                selectedHomeNavigationTab = selectedHomeNavigationTab.value,
                selectedChannelContentTab = selectedChannelsTab.value,
                onRoomClick = { if (firstThrottler.canHandle()) onRoomClick(it) },
                onChannelClick = { homeState.channelListState.eventSink(ChannelListEvents.OpenChannel(it)) },
                modifier = contentModifier
                    .statusBarsPadding()
                    .fillMaxSize()
                    .background(ElementTheme.colors.bgCanvasDefault)
            )
            acceptDeclineInviteView()

            if (homeState.genericActionState is AsyncAction.Loading) {
                ProgressDialog()
            }

            if (homeState.genericActionState is AsyncAction.Failure) {
                ErrorDialog(
                    content = homeState.genericActionState.errorOrNull()?.message ?: stringResource(CommonStrings.error_unknown),
                    onSubmit = { homeState.eventSink(HomeEvents.HideError) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScaffold(
    state: HomeState,
    selectedHomeNavigationTab: HomeScreenTab,
    selectedChannelContentTab: ChannelsScreenTab,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomId) -> Unit,
    onOpenSettings: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onSearchUserClick: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onUserProfileClick: () -> Unit,
    onCreateFeedClick: () -> Unit,
    onSendWalletToken: () -> Unit,
    onHomeNavTabSelected: (HomeScreenTab) -> Unit,
    onChannelsContentTabSelected: (ChannelsScreenTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun onRoomClick(room: RoomListRoomSummary) {
        onRoomClick(room.roomId)
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)
    val roomListState: RoomListState = state.roomListState

    val showFeedMediaPreview by remember(state.feedListState.feedMediaPreviewState) {
        mutableStateOf(state.feedListState.feedMediaPreviewState != AsyncAction.Uninitialized)
    }

    val claimRewardsSheetState = rememberModalBottomSheetState()
    val walletStakeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showWalletReceiveTokenSheet = remember { mutableStateOf(false) }
    val walletReceiveTokenSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeScreenTopBar(
                    matrixUser = state.matrixUser,
                    showAvatarIndicator = state.showAvatarIndicator,
                    areSearchResultsDisplayed = roomListState.searchState.isSearchActive,
                    onToggleSearch = {
                        when (selectedHomeNavigationTab) {
                            HomeScreenTab.FEED -> {
                                onSearchUserClick()
                            }
                            else -> {
                                roomListState.eventSink(RoomListEvents.ToggleSearchResults)
                            }
                        }
                    },
                    onMenuActionClick = onMenuActionClick,
                    onOpenSettings = onOpenSettings,
                    onOpenProfile = onUserProfileClick,
                    scrollBehavior = scrollBehavior,
                    displayMenuItems = state.showDisplayMenuItems(selectedHomeNavigationTab),
                    displayFilters = roomListState.shouldDisplayFilters(selectedHomeNavigationTab),
                    filtersState = roomListState.filtersState,
                    canReportBug = state.canReportBug,
                    shouldShowNewRewardsIntimation = state.shouldShowNewRewardsIntimation,
                    userRewards = state.userRewards,
                    onDismissRewardsTooltip = { immediate ->
                        state.eventSink(HomeEvents.DismissRewardsIntimation(immediate))
                    }
                )
            },
            content = { padding ->
                HomeScreenContent(
                    state = state,
                    selectedHomeScreenTab = selectedHomeNavigationTab,
                    selectedChannelContentTab = selectedChannelContentTab,
                    onSetUpRecoveryClick = onSetUpRecoveryClick,
                    onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                    onRoomClick = ::onRoomClick,
                    onCreateRoomClick = onCreateRoomClick,
                    onFeedClick = onFeedClick,
                    onFeedUserClick = onFeedUserClick,
                    onSendWalletToken = onSendWalletToken,
                    onReceiveWalletToken = {
                        showWalletReceiveTokenSheet.value = true
                    },
                    onChannelsContentTabSelected = onChannelsContentTabSelected,
                    modifier = Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            // Floating Action button
            if (state.shouldDisplayActions(selectedHomeNavigationTab)) {
                HomeFabButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 16.dp),
                    onClick = {
                        when {
                            selectedHomeNavigationTab == HomeScreenTab.CHAT -> onCreateRoomClick()
                            else -> onCreateFeedClick()
                        }
                    }
                )
            }

            // Home tab view
            HomeScreenTabView(
                selectedNavigationTab = selectedHomeNavigationTab,
                onTabSelected = onHomeNavTabSelected
            )
        }
    }

    if (showFeedMediaPreview) {
        FeedMediaPreview(state.feedListState.feedMediaPreviewState, onDismiss = {
            state.feedListState.eventSink(FeedListEvents.DismissFeedMedia)
        })
    }

    if (state.showClaimRewardsSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                StateBus.onRewardsClaimed()
            },
            sheetState = claimRewardsSheetState,
            dragHandle = null,
            content = {
                ClaimRewardsSheet(
                    userRewards = state.walletContentState.claimableRewards,
                    actionState = state.claimRewardActionState,
                    meowPrice = state.walletContentState.meowPrice,
                    onViewTransaction = { transaction ->
                        state.eventSink(HomeEvents.WalletEvents.ViewWalletTransaction(transaction))
                    },
                    onClaimRewards = {
                        state.eventSink(HomeEvents.ClaimRewards)
                    },
                    onRewardsClaimed = {
                        state.eventSink(HomeEvents.WalletEvents.RefreshWalletBalance)
                    }
                )
            }
        )
    }

    if (
        state.walletContentState.showStakingSheet &&
        state.walletContentState.selectedPool != null
    ) {
        ModalBottomSheet(
            onDismissRequest = { state.eventSink(HomeEvents.WalletEvents.DismissStakingSheet) },
            sheetState = walletStakeSheetState,
            content = {
                WalletStakingSheet(
                    selectedPool = state.walletContentState.selectedPool,
                    actionState = state.walletContentState.walletStakeActionState,
                    eventSink = state.walletContentState.eventSink
                )
            }
        )
    }

    if (showWalletReceiveTokenSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showWalletReceiveTokenSheet.value = false },
            sheetState = walletReceiveTokenSheetState,
            content = {
                WalletReceiveTokenSheet(
                    state = state,
                    onDismissSheet = { showWalletReceiveTokenSheet.value = false }
                )
            }
        )
    }
}

@Composable
internal fun HomeScreenContent(
    state: HomeState,
    selectedHomeScreenTab: HomeScreenTab,
    selectedChannelContentTab: ChannelsScreenTab,
    onSetUpRecoveryClick: () -> Unit,
    onConfirmRecoveryKeyClick: () -> Unit,
    onRoomClick: (RoomListRoomSummary) -> Unit,
    onCreateRoomClick: () -> Unit,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onSendWalletToken: () -> Unit = {},
    onReceiveWalletToken: () -> Unit = {},
    onChannelsContentTabSelected: (ChannelsScreenTab) -> Unit,
    modifier: Modifier,
) {
    fun onNotificationClick(room: RoomListRoomSummary) {
        onRoomClick(room)
    }

    when (selectedHomeScreenTab) {
        HomeScreenTab.CHAT -> {
            RoomListContentView(
                modifier = modifier,
                contentState = state.roomListState.contentState,
                filtersState = state.roomListState.filtersState,
                roomMappedUserProStatus = state.roomListState.roomMappedUserProStatus,
                hideInvitesAvatars = state.roomListState.hideInvitesAvatars,
                eventSink = state.roomListState.eventSink,
                onSetUpRecoveryClick = onSetUpRecoveryClick,
                onConfirmRecoveryKeyClick = onConfirmRecoveryKeyClick,
                onRoomClick = onRoomClick,
                onCreateRoomClick = onCreateRoomClick,
                contentPadding = PaddingValues(0.dp)
            )
        }
        HomeScreenTab.CHANNEL -> {
            HomeChannelListContentView(
                selectedChannelContentTab = selectedChannelContentTab,
                channelsContentState = state.channelListState.contentState,
                roomListState = state.roomListState,
                eventSink = state.channelListState.eventSink,
                roomEventSink = state.roomListState.eventSink,
                onRoomClick = onRoomClick,
                onChannelTabSelected = onChannelsContentTabSelected,
                modifier = modifier
            )
        }
        HomeScreenTab.FEED -> {
            HomeFeedListContentView(
                contentState = state.feedListState.contentState,
                feedMediaMap = state.feedListState.feedMediaMap,
                feedLinkMetaDataMap = state.feedListState.feedLinkMetaDataMap,
                eventSink = state.feedListState.eventSink,
                zeroUserRewards = state.userRewards,
                loggedInUserId = state.matrixUser.userId,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                modifier = modifier
            )
        }
        HomeScreenTab.NOTIFICATION -> {
            HomeNotificationListContentView(
                contentState = state.roomListState.contentState,
                eventSink = state.roomListState.eventSink,
                onNotificationClick = ::onNotificationClick,
                modifier = modifier
            )
        }
        HomeScreenTab.WALLET -> {
            HomeWalletContent(
                modifier = modifier,
                state = state.walletContentState,
                onSendWalletToken = onSendWalletToken,
                onReceiveWalletToken = onReceiveWalletToken
            )
        }
        /*HomeScreenTab.PROFILE -> {
            HomeFeedListContentView(
                contentState = state.myFeedsContentState,
                feedMediaMap = state.feedMediaMap,
                feedLinkMetaDataMap = state.feedLinkMetaDataMap,
                eventSink = state.eventSink,
                zeroUserRewards = state.userRewards,
                isProfileFeedList = true,
                onFeedClick = onFeedClick,
                onFeedUserClick = onFeedUserClick,
                modifier = modifier
            )
        }*/
    }
}

internal fun RoomListRoomSummary.contentType() = displayType.ordinal

@PreviewsDayNight
@Composable
internal fun HomeViewPreview(@PreviewParameter(HomeStateProvider::class) state: HomeState) = ElementPreview {
    HomeView(
        homeState = state,
        onRoomClick = {},
        onSettingsClick = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onCreateRoomClick = {},
        onSearchUserClick = {},
        onRoomSettingsClick = {},
        onReportRoomClick = {},
        onMenuActionClick = {},
        onFeedClick = {},
        onFeedUserClick = {},
        onUserProfileClick = {},
        onCreateFeedClick = {},
        onDeclineInviteAndBlockUser = {},
        acceptDeclineInviteView = {},
        onSendWalletToken = {},
    )
}

@Preview
@Composable
internal fun HomeViewA11yPreview() = ElementPreview {
    HomeView(
        homeState = aHomeState(),
        onRoomClick = {},
        onSettingsClick = {},
        onSetUpRecoveryClick = {},
        onConfirmRecoveryKeyClick = {},
        onRoomSettingsClick = {},
        onReportRoomClick = {},
        onMenuActionClick = {},
        onDeclineInviteAndBlockUser = {},
        acceptDeclineInviteView = {},
        onCreateRoomClick = {},
        onSearchUserClick = {},
        onFeedClick = {},
        onFeedUserClick = {},
        onUserProfileClick = {},
        onCreateFeedClick = {},
        onSendWalletToken = {},
    )
}
