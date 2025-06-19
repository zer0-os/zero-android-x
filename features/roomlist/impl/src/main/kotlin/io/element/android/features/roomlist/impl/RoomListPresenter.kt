/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import im.vector.app.features.analytics.plan.Interaction
import io.element.android.features.invite.api.SeenInvitesStore
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents.AcceptInvite
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteEvents.DeclineInvite
import io.element.android.features.invite.api.acceptdecline.AcceptDeclineInviteState
import io.element.android.features.leaveroom.api.LeaveRoomEvent.ShowConfirmation
import io.element.android.features.leaveroom.api.LeaveRoomState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.features.roomlist.impl.datasource.RoomListDataSource
import io.element.android.features.roomlist.impl.filters.RoomListFiltersState
import io.element.android.features.roomlist.impl.model.HomeScreenChannel
import io.element.android.features.roomlist.impl.model.channelId
import io.element.android.features.roomlist.impl.search.RoomListSearchEvents
import io.element.android.features.roomlist.impl.search.RoomListSearchState
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.fullscreenintent.api.FullScreenIntentPermissionsState
import io.element.android.libraries.indicator.api.IndicatorService
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.encryption.EncryptionService
import io.element.android.libraries.matrix.api.encryption.RecoveryState
import io.element.android.libraries.matrix.api.roomlist.RoomList
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.timeline.ReceiptType
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.preferences.api.store.AppPreferencesStore
import io.element.android.libraries.preferences.api.store.SessionPreferencesStore
import io.element.android.libraries.push.api.battery.BatteryOptimizationState
import io.element.android.libraries.push.api.notifications.NotificationCleaner
import io.element.android.services.analytics.api.AnalyticsService
import io.element.android.services.analyticsproviders.api.trackers.captureInteraction
import io.element.android.support.zero.common.extension.safeAsync
import io.element.android.support.zero.common.extension.withIOScope
import io.element.android.support.zero.common.extension.withScope
import io.element.android.support.zero.common.util.FeedItemMediaCache
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

private const val EXTENDED_RANGE_SIZE = 40
private const val SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS = 300L
private const val HOME_FEED_PAGE_SIZE = 15

class RoomListPresenter @Inject constructor(
    private val client: MatrixClient,
    private val syncService: SyncService,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val leaveRoomPresenter: Presenter<LeaveRoomState>,
    private val roomListDataSource: RoomListDataSource,
    private val featureFlagService: FeatureFlagService,
    private val indicatorService: IndicatorService,
    private val filtersPresenter: Presenter<RoomListFiltersState>,
    private val searchPresenter: Presenter<RoomListSearchState>,
    private val sessionPreferencesStore: SessionPreferencesStore,
    private val analyticsService: AnalyticsService,
    private val acceptDeclineInvitePresenter: Presenter<AcceptDeclineInviteState>,
    private val fullScreenIntentPermissionsPresenter: Presenter<FullScreenIntentPermissionsState>,
    private val batteryOptimizationPresenter: Presenter<BatteryOptimizationState>,
    private val notificationCleaner: NotificationCleaner,
    private val logoutPresenter: Presenter<DirectLogoutState>,
    private val appPreferencesStore: AppPreferencesStore,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val seenInvitesStore: SeenInvitesStore,
) : Presenter<RoomListState> {
    private val encryptionService: EncryptionService = client.encryptionService()

    private val channelRoomMap: MutableMap<String, RoomSummary> = mutableMapOf()

    private val _allFeeds: MutableList<ZeroFeed> = mutableListOf()
    private val _myFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): RoomListState {
        val coroutineScope = rememberCoroutineScope()
        val leaveRoomState = leaveRoomPresenter.present()
        val matrixUser = client.userProfile.collectAsState()
        val isOnline by syncService.isOnline.collectAsState()
        val filtersState = filtersPresenter.present()
        val searchState = searchPresenter.present()
        val acceptDeclineInviteState = acceptDeclineInvitePresenter.present()
        val canReportBug = remember { rageshakeFeatureAvailability.isAvailable() }

        var shouldShowRoomIntimation by rememberSaveable { mutableStateOf(true) }
        val shouldShowNewRewardsIntimation = client.shouldShowNewRewardsIntimation.collectAsState()
        val userRewards = client.userRewards.collectAsState()

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val resolvedChannelRoomId: MutableState<RoomId?> = remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            roomListDataSource.launchIn(this)
            fetchInitialData()
        }

        var securityBannerDismissed by rememberSaveable { mutableStateOf(false) }

        // Avatar indicator
        val showAvatarIndicator by indicatorService.showRoomListTopBarIndicator()
        val hideInvitesAvatar by remember {
            appPreferencesStore.getHideInviteAvatarsFlow()
        }.collectAsState(initial = false)

        val contextMenu = remember { mutableStateOf<RoomListState.ContextMenu>(RoomListState.ContextMenu.Hidden) }
        val declineInviteMenu = remember { mutableStateOf<RoomListState.DeclineInviteMenu>(RoomListState.DeclineInviteMenu.Hidden) }

        val directLogoutState = logoutPresenter.present()

        fun handleEvents(event: RoomListEvents) {
            when (event) {
                is RoomListEvents.UpdateVisibleRange -> coroutineScope.launch {
                    updateVisibleRange(event.range)
                }
                RoomListEvents.DismissRequestVerificationPrompt -> securityBannerDismissed = true
                RoomListEvents.DismissBanner -> securityBannerDismissed = true
                RoomListEvents.ToggleSearchResults -> searchState.eventSink(RoomListSearchEvents.ToggleSearchVisibility)
                is RoomListEvents.ShowContextMenu -> {
                    coroutineScope.showContextMenu(event, contextMenu)
                }
                is RoomListEvents.HideContextMenu -> {
                    contextMenu.value = RoomListState.ContextMenu.Hidden
                }
                is RoomListEvents.LeaveRoom -> leaveRoomState.eventSink(ShowConfirmation(event.roomId))
                is RoomListEvents.SetRoomIsFavorite -> coroutineScope.setRoomIsFavorite(event.roomId, event.isFavorite)
                is RoomListEvents.MarkAsRead -> coroutineScope.markAsRead(event.roomId)
                is RoomListEvents.MarkAsUnread -> coroutineScope.markAsUnread(event.roomId)
                is RoomListEvents.AcceptInvite -> {
                    acceptDeclineInviteState.eventSink(
                        AcceptInvite(event.roomSummary.toInviteData())
                    )
                }
                is RoomListEvents.DeclineInvite -> {
                    acceptDeclineInviteState.eventSink(
                        DeclineInvite(event.roomSummary.toInviteData(), blockUser = event.blockUser, shouldConfirm = false)
                    )
                }
                is RoomListEvents.ShowDeclineInviteMenu -> declineInviteMenu.value = RoomListState.DeclineInviteMenu.Shown(event.roomSummary)
                RoomListEvents.HideDeclineInviteMenu -> declineInviteMenu.value = RoomListState.DeclineInviteMenu.Hidden
                is RoomListEvents.ClearCacheOfRoom -> coroutineScope.clearCacheOfRoom(event.roomId)
                is RoomListEvents.DismissRewardsIntimation -> {
                    if (event.immediate) {
                        shouldShowRoomIntimation = false
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            shouldShowRoomIntimation = false
                        }, 3_000)
                    }
                }
                RoomListEvents.HideError -> genericActionState.value = AsyncAction.Uninitialized
                is RoomListEvents.OpenChannel -> coroutineScope.openChannel(event.channel, resolvedChannelRoomId, genericActionState)
                is RoomListEvents.LoadMoreFeeds -> {
                    _allFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreHomeFeeds(event.followingFeeds, event.currentFeeds.size)
                }
                is RoomListEvents.RefreshFeeds -> coroutineScope.forceRefreshHomeFeeds(event.followingFeeds)
                is RoomListEvents.LoadMoreMyFeeds -> {
                    _myFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreMyFeeds(event.currentFeeds.size)
                }
                RoomListEvents.RefreshMyFeeds -> coroutineScope.forceRefreshMyFeeds()
                is RoomListEvents.AddMeowToFeed -> coroutineScope.addMeowToFeed(event.feed, event.meowCount)
            }
        }

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

        val contentState = roomListContentState(securityBannerDismissed)

        val canReportRoom by produceState(false) { value = client.canReportRoom() }

        val channelContentState = channelListContentState()
        createChannelRoomMap(
            (channelContentState as? ChannelListContentState.Channels)?.channels.orEmpty()
        )

        val allFeedsContentState = allFeedsListContentState()
        val myFeedsContentState = allMyFeedsListContentState()

        /*val feedMediaMap = rememberSaveable(
            saver = mapSaver(
                save = { it.toMap() },
                restore = {
                    mutableStateMapOf<String, FeedMedia>().apply {
                        putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
                    }
                }
            )
        ) { mutableStateMapOf() }*/
        val feedMediaMap = remember { mutableStateMapOf<String, FeedMedia>() }
        val feedLinkMetaDataMap = remember { mutableStateMapOf<String, ZeroLinkPreview>() }

        LaunchedEffect(Unit) {
            feedMediaMap.putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
            feedLinkMetaDataMap.putAll(FeedItemMediaCache.getCachedFeedItemLinkMetaDataMap())
        }
        val allCombinedFeeds = extractFeedsToFetchData(allFeedsContentState, myFeedsContentState)
        fetchFeedMediaIfRequired(allCombinedFeeds, feedMediaMap)
        fetchLinksMetaDataIfRequired(allCombinedFeeds, feedLinkMetaDataMap)

        return RoomListState(
            matrixUser = matrixUser.value,
            showAvatarIndicator = showAvatarIndicator,
            snackbarMessage = snackbarMessage,
            hasNetworkConnection = isOnline,
            genericActionState = genericActionState.value,
            contextMenu = contextMenu.value,
            declineInviteMenu = declineInviteMenu.value,
            leaveRoomState = leaveRoomState,
            filtersState = filtersState,
            canReportBug = canReportBug,
            searchState = searchState,
            contentState = contentState,
            channelContentState = channelContentState,
            allFeedsContentState = allFeedsContentState,
            myFeedsContentState = myFeedsContentState,
            feedMediaMap = feedMediaMap,
            feedLinkMetaDataMap = feedLinkMetaDataMap,
            resolvedChannelRoom = resolvedChannelRoomId.value,
            acceptDeclineInviteState = acceptDeclineInviteState,
            directLogoutState = directLogoutState,
            hideInvitesAvatars = hideInvitesAvatar,
            canReportRoom = canReportRoom,
            eventSink = ::handleEvents,
            shouldShowNewRewardsIntimation = shouldShowRoomIntimation && shouldShowNewRewardsIntimation.value,
            userRewards = userRewards.value,
        )
    }

    @Composable
    private fun rememberSecurityBannerState(
        securityBannerDismissed: Boolean,
    ): State<SecurityBannerState> {
        val currentSecurityBannerDismissed by rememberUpdatedState(securityBannerDismissed)
        val recoveryState by encryptionService.recoveryStateStateFlow.collectAsState()
        return remember {
            derivedStateOf {
                calculateBannerState(
                    securityBannerDismissed = currentSecurityBannerDismissed,
                    recoveryState = recoveryState,
                )
            }
        }
    }

    private fun calculateBannerState(
        securityBannerDismissed: Boolean,
        recoveryState: RecoveryState,
    ): SecurityBannerState {
        if (securityBannerDismissed) {
            return SecurityBannerState.None
        }

        when (recoveryState) {
            RecoveryState.DISABLED -> return SecurityBannerState.SetUpRecovery
            RecoveryState.INCOMPLETE -> return SecurityBannerState.RecoveryKeyConfirmation
            RecoveryState.UNKNOWN,
            RecoveryState.WAITING_FOR_SYNC,
            RecoveryState.ENABLED -> Unit
        }

        return SecurityBannerState.None
    }

    @Composable
    private fun roomListContentState(
        securityBannerDismissed: Boolean,
    ): RoomListContentState {
        val roomSummaries by produceState(initialValue = AsyncData.Loading()) {
            roomListDataSource.allRooms.collect { value = AsyncData.Success(it) }
        }
        val loadingState by roomListDataSource.loadingState.collectAsState()
        val showEmpty by remember {
            derivedStateOf {
                (loadingState as? RoomList.LoadingState.Loaded)?.numberOfRooms == 0
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                loadingState == RoomList.LoadingState.NotLoaded || roomSummaries is AsyncData.Loading
            }
        }
        val seenRoomInvites by remember { seenInvitesStore.seenRoomIds() }.collectAsState(emptySet())
        val securityBannerState by rememberSecurityBannerState(securityBannerDismissed)
        return when {
            showEmpty -> RoomListContentState.Empty(securityBannerState = securityBannerState)
            showSkeleton -> RoomListContentState.Skeleton(count = 16)
            else -> {
                RoomListContentState.Rooms(
                    securityBannerState = securityBannerState,
                    fullScreenIntentPermissionsState = fullScreenIntentPermissionsPresenter.present(),
                    batteryOptimizationState = batteryOptimizationPresenter.present(),
                    summaries = roomSummaries.dataOrNull().orEmpty().toPersistentList(),
                    seenRoomInvites = seenRoomInvites.toPersistentSet(),
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun CoroutineScope.showContextMenu(event: RoomListEvents.ShowContextMenu, contextMenuState: MutableState<RoomListState.ContextMenu>) = launch {
        val initialState = RoomListState.ContextMenu.Shown(
            roomId = event.roomSummary.roomId,
            roomName = event.roomSummary.name,
            isDm = event.roomSummary.isDm,
            isFavorite = event.roomSummary.isFavorite,
            markAsUnreadFeatureFlagEnabled = featureFlagService.isFeatureEnabled(FeatureFlags.MarkAsUnread),
            hasNewContent = event.roomSummary.hasNewContent,
            displayClearRoomCacheAction = appPreferencesStore.isDeveloperModeEnabledFlow().first(),
        )
        contextMenuState.value = initialState

        client.getRoom(event.roomSummary.roomId)?.use { room ->

            val isShowingContextMenuFlow = snapshotFlow { contextMenuState.value is RoomListState.ContextMenu.Shown }
                .distinctUntilChanged()

            val isFavoriteFlow = room.roomInfoFlow
                .map { it.isFavorite }
                .distinctUntilChanged()

            isFavoriteFlow
                .onEach { isFavorite ->
                    contextMenuState.value = initialState.copy(isFavorite = isFavorite)
                }
                .flatMapLatest { isShowingContextMenuFlow }
                .takeWhile { isShowingContextMenu -> isShowingContextMenu }
                .collect()
        }
    }

    private fun CoroutineScope.setRoomIsFavorite(roomId: RoomId, isFavorite: Boolean) = launch {
        client.getRoom(roomId)?.use { room ->
            room.setIsFavorite(isFavorite)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuFavouriteToggle)
                }
        }
    }

    private fun CoroutineScope.markAsRead(roomId: RoomId) = launch {
        notificationCleaner.clearMessagesForRoom(client.sessionId, roomId)
        client.getRoom(roomId)?.use { room ->
            room.setUnreadFlag(isUnread = false)
            val receiptType = if (sessionPreferencesStore.isSendPublicReadReceiptsEnabled().first()) {
                ReceiptType.READ
            } else {
                ReceiptType.READ_PRIVATE
            }
            room.markAsRead(receiptType)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuUnreadToggle)
                }
        }
    }

    private fun CoroutineScope.markAsUnread(roomId: RoomId) = launch {
        client.getRoom(roomId)?.use { room ->
            room.setUnreadFlag(isUnread = true)
                .onSuccess {
                    analyticsService.captureInteraction(name = Interaction.Name.MobileRoomListRoomContextMenuUnreadToggle)
                }
        }
    }

    private fun CoroutineScope.clearCacheOfRoom(roomId: RoomId) = launch {
        client.getRoom(roomId)?.use { room ->
            room.clearEventCacheStorage()
        }
    }

    private var currentUpdateVisibleRangeJob: Job? = null
    private fun CoroutineScope.updateVisibleRange(range: IntRange) {
        currentUpdateVisibleRangeJob?.cancel()
        currentUpdateVisibleRangeJob = launch {
            // Debounce the subscription to avoid subscribing to too many rooms
            delay(SUBSCRIBE_TO_VISIBLE_ROOMS_DEBOUNCE_IN_MILLIS)

            if (range.isEmpty()) return@launch
            val currentRoomList = roomListDataSource.allRooms.first()
            // Use extended range to 'prefetch' the next rooms info
            val midExtendedRangeSize = EXTENDED_RANGE_SIZE / 2
            val extendedRange = range.first until range.last + midExtendedRangeSize
            val roomIds = extendedRange.mapNotNull { index ->
                currentRoomList.getOrNull(index)?.roomId
            }
            roomListDataSource.subscribeToVisibleRooms(roomIds)
        }
    }

    private fun CoroutineScope.fetchInitialData() = launch {
        awaitAll(
            // Check zero thirdWeb wallet
            safeAsync { client.checkZeroThirdWebWallet() },
            // Fetch user rewards
            safeAsync { client.getUserRewards(shouldCheckRewardsIntimation = true) },
            // Fetch home channels
            safeAsync { client.getUserZIds() },
            // Fetch all home feeds
            safeAsync { client.fetchAllFeeds(followingFeeds = true, limit = HOME_FEED_PAGE_SIZE, skip = 0) },
            // Fetch all my feeds
            safeAsync { client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = 0) },
        )
    }

    @Composable
    private fun channelListContentState(): ChannelListContentState {
        val homeChannelsState by produceState(initialValue = AsyncData.Loading()) {
            client.userZIds.collect {
                value = AsyncData.Success(it)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (homeChannelsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                homeChannelsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> ChannelListContentState.Empty
            showSkeleton -> ChannelListContentState.Skeleton(20)
            else -> {
                val mappedChannels = homeChannelsState.dataOrNull()
                    .orEmpty()
                    .sorted()
                    .map { HomeScreenChannel(channelFullName = it) }
                    .distinctBy { it.channelId() }
                    .toPersistentList()
                ChannelListContentState.Channels(mappedChannels)
            }
        }
    }

    @Composable
    private fun allFeedsListContentState(): FeedListContentState {
        val homeFeedsState by produceState(initialValue = AsyncData.Loading()) {
            client.allFeeds.collect {
                val feeds: List<ZeroFeed> = mutableListOf<ZeroFeed>().apply {
                    addAll(_allFeeds)
                    addAll(it)
                }.distinctBy { it.id }
                value = AsyncData.Success(feeds)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (homeFeedsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                homeFeedsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> FeedListContentState.Empty
            showSkeleton -> FeedListContentState.Skeleton(HOME_FEED_PAGE_SIZE)
            else -> {
                val mappedAllFeeds = homeFeedsState.dataOrNull()
                    .orEmpty()
                    .toPersistentList()
                FeedListContentState.Feeds(mappedAllFeeds)
            }
        }
    }

    @Composable
    private fun allMyFeedsListContentState(): FeedListContentState {
        val myFeedsState by produceState(initialValue = AsyncData.Loading()) {
            client.allMyFeeds.collect {
                val feeds: List<ZeroFeed> = mutableListOf<ZeroFeed>().apply {
                    addAll(_myFeeds)
                    addAll(it)
                }.distinctBy { it.id }
                value = AsyncData.Success(feeds)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (myFeedsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                myFeedsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> FeedListContentState.Empty
            showSkeleton -> FeedListContentState.Skeleton(HOME_FEED_PAGE_SIZE)
            else -> {
                val mappedMyFeeds = myFeedsState.dataOrNull()
                    .orEmpty()
                    .toPersistentList()
                FeedListContentState.Feeds(mappedMyFeeds)
            }
        }
    }

    private fun createChannelRoomMap(channels: List<HomeScreenChannel>) = withScope(Dispatchers.IO) {
        for (channel in channels) {
            channel.channelId()?.let { channelId ->
                val roomSummary = client.getRoomSummaryFlow(RoomAlias(channelId).toRoomIdOrAlias())
                    .firstOrNull()
                    ?.getOrNull()
                roomSummary?.let { summary ->
                    channel.notificationsCount = summary.info.numUnreadMessages.toInt()
                    channelRoomMap.put(channelId, summary)
                }
            }
        }
    }

    private fun CoroutineScope.openChannel(
        channel: HomeScreenChannel,
        resolvedChannelRoomId: MutableState<RoomId?>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Uninitialized
        val channelId = channel.channelId() ?: return@launch
        channelRoomMap[channelId]?.let { roomSummary ->
            resolvedChannelRoomId.value = roomSummary.roomId
            return@launch
        }
        genericActionState.value = AsyncAction.Loading
        client.resolveRoomAlias(RoomAlias(channelId)).getOrNull()?.getOrNull()?.roomId?.let { roomId ->
            resolvedChannelRoomId.value = roomId
            genericActionState.value = AsyncAction.Success(Unit)
            return@launch
        }
        client.joinZeroChannel(channelId)
            .onSuccess { roomId ->
                roomId?.let {
                    genericActionState.value = AsyncAction.Success(Unit)
                    resolvedChannelRoomId.value = RoomId(roomId)
                } ?: run {
                    genericActionState.value = AsyncAction.Failure(Throwable("RoomId not found"))
                }
            }
            .onFailure { failure ->
                genericActionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.loadMoreHomeFeeds(followingFeedsOnly: Boolean, skip: Int) = launch {
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = skip)
    }

    private fun CoroutineScope.forceRefreshHomeFeeds(followingFeedsOnly: Boolean) = launch {
        _allFeeds.clear()
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = 0)
    }

    private fun CoroutineScope.loadMoreMyFeeds(skip: Int) = launch {
        client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = skip)
    }

    private fun CoroutineScope.forceRefreshMyFeeds() = launch {
        _myFeeds.clear()
        client.fetchAllMyFeeds(limit = HOME_FEED_PAGE_SIZE, skip = 0)
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int) = launch {
        client.addMeowToFeed(feed, meowCount)
    }

    private fun extractFeedsToFetchData(
        allFeedsContentState: FeedListContentState,
        myFeedsContentState: FeedListContentState,
    ): List<ZeroFeed> {
        val allFeeds = (allFeedsContentState as? FeedListContentState.Feeds)?.feeds ?: emptyList()
        val myFeeds = (myFeedsContentState as? FeedListContentState.Feeds)?.feeds ?: emptyList()
        val feeds = mutableListOf<ZeroFeed>().apply {
            addAll(allFeeds)
            addAll(myFeeds)
        }.distinctBy { it.id }.toList()
        return feeds
    }

    private fun fetchFeedMediaIfRequired(
        feeds: List<ZeroFeed>,
        feedMediaMap: SnapshotStateMap<String, FeedMedia>
    ) {
        withIOScope {
            coroutineScope {
                val feedsToFetch = feeds.mapNotNull { feed ->
                    val mediaId = feed.media?.id ?: return@mapNotNull null
                    if (feedMediaMap.contains(feed.id) || FeedItemMediaCache.containsMedia(feed.id)) return@mapNotNull null
                    else feed
                }
                val results = feedsToFetch.map { feed ->
                    async { feed.id to client.fetchFeedMedia(feed.media!!.id) }
                }.awaitAll()
                results.forEach { (feedId, media) ->
                    media.getOrNull()?.let { feedMedia ->
                        FeedItemMediaCache.addFeedMedia(feedId, feedMedia)
                        feedMediaMap[feedId] = feedMedia
                    }
                }
            }
        }
    }

    private fun fetchLinksMetaDataIfRequired(
        feeds: List<ZeroFeed>,
        feedLinkMetaDataMap: SnapshotStateMap<String, ZeroLinkPreview>
    ) {
        withIOScope {
            coroutineScope {
                val feedsToFetch = feeds.mapNotNull { feed ->
                    val availableYoutubeUrl = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text) ?: return@mapNotNull null
                    if (feedLinkMetaDataMap.contains(feed.id) || FeedItemMediaCache.containsUrlMetaData(feed.id)) return@mapNotNull null
                    else feed
                }
                val results = feedsToFetch.map { feed ->
                    val url = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text)!!
                    async { feed.id to client.fetchUrlMetaData(url) }
                }.awaitAll()
                results.forEach { (feedId, urlMetaData) ->
                    urlMetaData.getOrNull()?.let { metaData ->
                        FeedItemMediaCache.addLinkMetaData(feedId, metaData)
                        feedLinkMetaDataMap[feedId] = metaData
                    }
                }
            }
        }
    }
}
