/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.channel.ChannelListContentState
import io.element.android.features.home.impl.feed.FeedListContentState
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.features.home.impl.model.SelectedStakePool
import io.element.android.features.home.impl.model.channelId
import io.element.android.features.home.impl.roomlist.RoomListState
import io.element.android.features.home.impl.spaces.HomeSpacesState
import io.element.android.features.home.impl.wallet.WalletContentState
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.features.home.impl.wallet.WalletTransactionsListState
import io.element.android.features.logout.api.direct.DirectLogoutState
import io.element.android.features.rageshake.api.RageshakeFeatureAvailability
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarDispatcher
import io.element.android.libraries.designsystem.utils.snackbar.collectSnackbarMessageAsState
import io.element.android.libraries.featureflag.api.FeatureFlagService
import io.element.android.libraries.featureflag.api.FeatureFlags
import io.element.android.libraries.indicator.api.IndicatorService
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.api.sync.SyncService
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingConfig
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingStatus
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUserRewardsInfo
import io.element.android.libraries.matrix.api.zero.staking.ZeroStakingUtil
import io.element.android.libraries.matrix.api.zero.staking.ZeroTokenAddress
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenBalance
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.isClaimableToken
import io.element.android.libraries.matrix.api.zero.wallet.tokenAmount
import io.element.android.support.zero.common.extension.safeAsync
import io.element.android.support.zero.common.extension.withIOScope
import io.element.android.support.zero.common.extension.withScope
import io.element.android.support.zero.common.state.StateBus
import io.element.android.support.zero.common.util.FeedItemMediaCache
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.jvm.optionals.getOrNull

private const val HOME_FEED_PAGE_SIZE = 15

@Inject
class HomePresenter(
    private val client: MatrixClient,
    private val syncService: SyncService,
    private val snackbarDispatcher: SnackbarDispatcher,
    private val indicatorService: IndicatorService,
    private val roomListPresenter: Presenter<RoomListState>,
    private val homeSpacesPresenter: Presenter<HomeSpacesState>,
    private val logoutPresenter: Presenter<DirectLogoutState>,
    private val rageshakeFeatureAvailability: RageshakeFeatureAvailability,
    private val featureFlagService: FeatureFlagService,
) : Presenter<HomeState> {

    private val channelRoomMap: MutableMap<String, RoomSummary> = mutableMapOf()

    private val _allFeeds: MutableList<ZeroFeed> = mutableListOf()
    private val _myFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): HomeState {
        val coroutineScope = rememberCoroutineScope()
        val matrixUser = client.userProfile.collectAsState()
        val isOnline by syncService.isOnline.collectAsState()
        val canReportBug by remember { rageshakeFeatureAvailability.isAvailable() }.collectAsState(false)
        val roomListState = roomListPresenter.present()
        val homeSpacesState = homeSpacesPresenter.present()
        val isSpaceFeatureEnabled by remember {
            featureFlagService.isFeatureEnabledFlow(FeatureFlags.Space)
        }.collectAsState(initial = false)
        var currentHomeNavigationBarItemOrdinal by rememberSaveable { mutableIntStateOf(HomeNavigationBarItem.Chats.ordinal) }
        val currentHomeNavigationBarItem by remember {
            derivedStateOf {
                HomeNavigationBarItem.from(currentHomeNavigationBarItemOrdinal)
            }
        }

        var shouldShowRoomIntimation by rememberSaveable { mutableStateOf(true) }
        val shouldShowNewRewardsIntimation = client.shouldShowNewRewardsIntimation.collectAsState()
        val userRewards = client.userRewards.collectAsState()
        val claimableUserRewards = remember { mutableStateOf(ZeroUserRewards.empty()) }
        val showClaimRewardsSheet = StateBus.claimRewardsStateObservable.collectAsState(initial = false)
        val claimRewardsActionState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val walletTransactionUrlState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val resolvedChannelRoomId: MutableState<RoomId?> = remember { mutableStateOf(null) }

        val feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }

        val showWalletBalance = remember { mutableStateOf(true) }
        val userWalletBalance = remember { mutableDoubleStateOf(0.0) }
        val meowPrice: MutableState<ZeroMeowPrice?> = remember { mutableStateOf(null) }
        val walletTokensListState: MutableState<WalletTokensListState> = remember {
            mutableStateOf(WalletTokensListState.Skeleton(10))
        }
        val walletTokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?> = remember {
            mutableStateOf(null)
        }
        val walletTransactionsListState: MutableState<WalletTransactionsListState> = remember {
            mutableStateOf(WalletTransactionsListState.Skeleton(10))
        }
        val walletTransactionsPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?> =
            remember { mutableStateOf(null) }
        val walletStakingContent: MutableState<List<HomeStakePool>> = remember { mutableStateOf(emptyList()) }
        val selectedStakePool: MutableState<SelectedStakePool?> = remember { mutableStateOf(null) }
        val showWalletStakingSheet = remember { mutableStateOf(false) }
        val walletStakeActionState: MutableState<AsyncAction<String>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val fetchWalletData: (walletAddress: String) -> Unit = { walletAddress ->
            coroutineScope.fetchWalletData(
                meowPrice, walletAddress, userWalletBalance,
                walletStakingContent, walletTokensListState, walletTransactionsListState,
                walletTokenPaginationParams, walletTransactionsPaginationParams
            )
        }

        LaunchedEffect(Unit) {
            // Force a refresh of the profile
            client.getUserProfile()
            // Fetch initial zero data
            fetchInitialData()
        }
        LaunchedEffect(Unit) {
            client.userProfile
                .mapNotNull { it.walletAddress }
                .distinctUntilChanged()
                .collectLatest { fetchWalletData(it) }
        }
        // Avatar indicator
        val showAvatarIndicator by indicatorService.showRoomListTopBarIndicator()
        val directLogoutState = logoutPresenter.present()

        fun handleEvents(event: HomeEvents) {
            when (event) {
                is HomeEvents.SelectHomeNavigationBarItem -> {
                    currentHomeNavigationBarItemOrdinal = event.item.ordinal
                }
                is HomeEvents.DismissRewardsIntimation -> {
                    if (event.immediate) {
                        shouldShowRoomIntimation = false
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            shouldShowRoomIntimation = false
                        }, 3_000)
                    }
                }
                HomeEvents.HideError -> genericActionState.value = AsyncAction.Uninitialized
                is HomeEvents.OpenChannel -> coroutineScope.openChannel(event.channel, resolvedChannelRoomId, genericActionState)
                HomeEvents.ChannelRoomOpened -> resolvedChannelRoomId.value = null
                is HomeEvents.LoadMoreFeeds -> {
                    _allFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreHomeFeeds(event.followingFeeds, event.currentFeeds.size)
                }
                is HomeEvents.RefreshFeeds -> coroutineScope.forceRefreshHomeFeeds(event.followingFeeds)
                is HomeEvents.LoadMoreMyFeeds -> {
                    _myFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineScope.loadMoreMyFeeds(event.currentFeeds.size)
                }
                HomeEvents.RefreshMyFeeds -> coroutineScope.forceRefreshMyFeeds()
                is HomeEvents.AddMeowToFeed -> GlobalScope.addMeowToFeed(event.feed, event.meowCount)
                is HomeEvents.LoadFeedMedia -> {
                    coroutineScope.loadFeedMediaPreview(event.mediaId, feedMediaPreviewActionState)
                }
                HomeEvents.DismissFeedMedia -> feedMediaPreviewActionState.value = AsyncAction.Uninitialized
                is HomeEvents.LoadMoreTokens -> {
                    matrixUser.value.walletAddress?.let { address ->
                        coroutineScope.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = event.currentTokens,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
                is HomeEvents.LoadMoreTransactions -> {
                    matrixUser.value.walletAddress?.let { address ->
                        coroutineScope.loadMoreWalletTransactions(
                            walletAddress = address,
                            currentList = event.currentTransactions,
                            walletTransactionsListState = walletTransactionsListState,
                            transactionPaginationParams = walletTransactionsPaginationParams
                        )
                    }
                }
                is HomeEvents.ViewWalletTransaction -> {
                    coroutineScope.loadWalletTransaction(
                        event.transactionId, event.chainId, walletTransactionUrlState, genericActionState
                    )
                }
                HomeEvents.OnWalletTransactionViewed ->
                    walletTransactionUrlState.value = AsyncAction.Uninitialized
                HomeEvents.ToggleWalletBalance -> showWalletBalance.value = !showWalletBalance.value
                HomeEvents.ClaimRewards -> {
                    claimableUserRewards.value = userRewards.value
                    coroutineScope.claimUserRewards(
                        matrixUser = matrixUser.value,
                        claimRewardsActionState = claimRewardsActionState,
                        refreshWallet = {
                            client.userProfile.value.walletAddress?.let { walletAddress ->
                                fetchWalletData(walletAddress)
                            }
                        }
                    )
                }
                HomeEvents.RefreshWalletBalance -> {
                    matrixUser.value.walletAddress?.let { address ->
                        val currentList = (walletTokensListState.value as? WalletTokensListState.Tokens)
                            ?.tokens ?: emptyList()
                        coroutineScope.loadMoreWalletTokens(
                            walletAddress = address,
                            currentList = currentList,
                            walletTokensListState = walletTokensListState,
                            tokenPaginationParams = walletTokenPaginationParams,
                            meowPrice = meowPrice,
                            userWalletBalance = userWalletBalance
                        )
                    }
                }
                is HomeEvents.StakePoolSelected -> {
                    coroutineScope.fetchPoolData(
                        pool = event.pool,
                        selectedStakePoolState = selectedStakePool,
                        genericActionState = genericActionState,
                        showWalletStakingSheetState = showWalletStakingSheet
                    )
                }
                is HomeEvents.StakeAmount -> {
                    selectedStakePool.value?.let {
                        coroutineScope.stakeAmount(it, event.amount, walletStakeActionState)
                    }
                }
                is HomeEvents.UnstakeAmount -> {
                    selectedStakePool.value?.let {
                        coroutineScope.unstakeAmount(it, event.amount, walletStakeActionState)
                    }
                }
                HomeEvents.DismissStakingSheet -> {
                    showWalletStakingSheet.value = false
                    walletStakeActionState.value = AsyncAction.Uninitialized
                    selectedStakePool.value = null
                    client.userProfile.value.walletAddress?.let { walletAddress ->
                        fetchWalletData(walletAddress)
                    }
                }
                HomeEvents.ClaimStakingRewards -> {
                    selectedStakePool.value?.poolInfo?.let { pool ->
                        coroutineScope.claimStakingRewards(
                            pool = pool,
                            genericActionState = genericActionState,
                            onDone = {
                                val walletAddress = client.userProfile.value.walletAddress ?: return@claimStakingRewards
                                fetchStakingData(
                                    stakePoolsContent = walletStakingContent,
                                    meowPrice = meowPrice.value,
                                    userAddress = walletAddress,
                                    refreshAllData = true,
                                    onRefreshAllData = { pool ->
                                        coroutineScope.fetchPoolData(
                                            pool = pool,
                                            selectedStakePoolState = selectedStakePool,
                                            genericActionState = genericActionState,
                                            showWalletStakingSheetState = showWalletStakingSheet
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
                HomeEvents.RefreshWallet -> client.userProfile.value.walletAddress?.let { walletAddress ->
                    fetchWalletData(walletAddress)
                }
            }
        }

        val snackbarMessage by snackbarDispatcher.collectSnackbarMessageAsState()

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

        return HomeState(
            matrixUser = matrixUser.value,
            showAvatarIndicator = showAvatarIndicator,
            hasNetworkConnection = isOnline,
            genericActionState = genericActionState.value,
            currentHomeNavigationBarItem = currentHomeNavigationBarItem,
            roomListState = roomListState,
            channelContentState = channelContentState,
            allFeedsContentState = allFeedsContentState,
            myFeedsContentState = myFeedsContentState,
            feedMediaMap = feedMediaMap,
            feedLinkMetaDataMap = feedLinkMetaDataMap,
            resolvedChannelRoom = resolvedChannelRoomId.value,
            homeSpacesState = homeSpacesState,
            snackbarMessage = snackbarMessage,
            canReportBug = canReportBug,
            directLogoutState = directLogoutState,
            isSpaceFeatureEnabled = isSpaceFeatureEnabled,
            shouldShowNewRewardsIntimation = shouldShowRoomIntimation && shouldShowNewRewardsIntimation.value,
            userRewards = userRewards.value,
            feedMediaPreviewState = feedMediaPreviewActionState.value,
            walletContentState = WalletContentState(
                userName = matrixUser.value.displayName ?: "",
                showWalletBalance = showWalletBalance.value,
                walletBalance = userWalletBalance.doubleValue,
                walletTransactionUrlState = walletTransactionUrlState.value,
                claimableRewards = claimableUserRewards.value,
                tokensListState = walletTokensListState.value,
                transactionsListState = walletTransactionsListState.value,
                tokensPaginationParams = walletTokenPaginationParams.value,
                transactionsPaginationParams = walletTransactionsPaginationParams.value,
                meowPrice = meowPrice.value,
                stakePools = walletStakingContent.value,
                selectedPool = selectedStakePool.value,
                showStakingSheet = showWalletStakingSheet.value,
                walletStakeActionState = walletStakeActionState.value,
                eventSink = ::handleEvents
            ),
            showClaimRewardsSheet = showClaimRewardsSheet.value,
            claimRewardActionState = claimRewardsActionState.value,
            eventSink = ::handleEvents,
        )
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
        val channelRoomId = client.resolveRoomAlias(RoomAlias(channelId))
            .getOrNull()?.getOrNull()?.roomId
        if (channelRoomId != null) {
            resolvedChannelRoomId.value = channelRoomId
            genericActionState.value = AsyncAction.Success(Unit)
        } else {
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

    private fun CoroutineScope.loadFeedMediaPreview(
        mediaId: String, feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>>
    ) = launch {
        feedMediaPreviewActionState.value = AsyncAction.Loading
        client.fetchFeedMedia(mediaId, isPreview = false)
            .onSuccess { media ->
                media?.let {
                    feedMediaPreviewActionState.value = AsyncAction.Success(it)
                } ?: run {
                    feedMediaPreviewActionState.value = AsyncAction.Failure(Throwable("Media not found."))
                }
            }
            .onFailure { failure ->
                feedMediaPreviewActionState.value = AsyncAction.Failure(failure)
            }
    }

    private fun CoroutineScope.fetchWalletData(
        meowPrice: MutableState<ZeroMeowPrice?>,
        walletAddress: String,
        userWalletBalance: MutableDoubleState,
        stakePoolsContent: MutableState<List<HomeStakePool>>,
        walletTokensListState: MutableState<WalletTokensListState>,
        walletTransactionsListState: MutableState<WalletTransactionsListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        transactionPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?>
    ) = launch(context = Dispatchers.IO) {
        val results = awaitAll(
            async { client.getMeowPrice() },
            async { client.getWalletTokens(
                walletAddress = walletAddress,
                paginationParams = tokenPaginationParams.value
            ) },
            async { client.getWalletTransactions(
                walletAddress = walletAddress,
                paginationParams = transactionPaginationParams.value
            ) },
        )
        (results[0] as? Result<ZeroMeowPrice>)?.let {
            meowPrice.value = it.getOrNull()
        }
        (results[1] as? Result<ZeroWalletTokensResponse>)?.let {
            it.onSuccess { result ->
                val tokensList = result.tokens
                setWalletBalance(tokensList, meowPrice.value, userWalletBalance)
                fetchStakingData(stakePoolsContent, meowPrice.value, walletAddress)
                walletTokensListState.value = WalletTokensListState.Tokens(
                    tokensList
                        .distinctBy { token -> token.tokenAddress }
                        .toPersistentList()
                )
                tokenPaginationParams.value = result.paginationParams
            }.onFailure {
                walletTokensListState.value = WalletTokensListState.Empty
            }
        }
        (results[2] as? Result<ZeroWalletTransactionsResponse>)?.let {
            it.onSuccess { result ->
                walletTransactionsListState.value = WalletTransactionsListState.Transactions(
                    result.transactions
                        .distinctBy { transaction -> transaction.hash }
                        .toPersistentList()
                )
                transactionPaginationParams.value = result.paginationParams
            }.onFailure {
                walletTransactionsListState.value = WalletTransactionsListState.Empty
            }
        }
    }

    private fun CoroutineScope.loadMoreWalletTokens(
        walletAddress: String,
        currentList: List<ZeroWalletToken>,
        walletTokensListState: MutableState<WalletTokensListState>,
        tokenPaginationParams: MutableState<ZeroWalletTokensPaginationParams?>,
        meowPrice: MutableState<ZeroMeowPrice?>,
        userWalletBalance: MutableDoubleState,
    ) = launch {
        client.getWalletTokens(
            walletAddress = walletAddress,
            paginationParams = tokenPaginationParams.value
        ).onSuccess {
            val newList = mutableListOf<ZeroWalletToken>().apply {
                addAll(currentList)
                addAll(it.tokens)
            }.distinctBy { token -> token.tokenAddress }
            setWalletBalance(newList, meowPrice.value, userWalletBalance)
            walletTokensListState.value = WalletTokensListState.Tokens(newList.toPersistentList())
            tokenPaginationParams.value = it.paginationParams
        }.onFailure {
            //Failed to load tokens next page
            walletTokensListState.value = WalletTokensListState.Tokens(currentList.toPersistentList())
        }
    }

    private fun CoroutineScope.loadMoreWalletTransactions(
        walletAddress: String,
        currentList: List<ZeroWalletTransaction>,
        walletTransactionsListState: MutableState<WalletTransactionsListState>,
        transactionPaginationParams: MutableState<ZeroWalletTransactionsPaginationParams?>
    ) = launch {
        client.getWalletTransactions(
            walletAddress = walletAddress,
            paginationParams = transactionPaginationParams.value
        ).onSuccess {
            val newList = mutableListOf<ZeroWalletTransaction>().apply {
                addAll(currentList)
                addAll(it.transactions)
            }.distinctBy { trans -> trans.hash }
            walletTransactionsListState.value = WalletTransactionsListState.Transactions(newList.toPersistentList())
            transactionPaginationParams.value = it.paginationParams
        }.onFailure {
            //Failed to load transactions next page
            walletTransactionsListState.value = WalletTransactionsListState.Transactions(currentList.toPersistentList())
        }
    }

    private fun CoroutineScope.loadWalletTransaction(
        transactionId: String,
        chainId: Int?,
        walletTransactionUrlState: MutableState<AsyncAction<String>>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.getTransactionReceipt(transactionId, chainId)
            .onSuccess {
                genericActionState.value = AsyncAction.Success(Unit)
                walletTransactionUrlState.value = AsyncAction.Success(it.blockExplorerUrl)
            }
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
            }
    }

    private fun setWalletBalance(tokensList: List<ZeroWalletToken>,
                                 meowPrice: ZeroMeowPrice?,
                                 userWalletBalance: MutableDoubleState
    ) {
        val meowPrice = meowPrice ?: return
        val meowTokens = tokensList.filter { it.isClaimableToken }
        val userBalance = ZeroWalletUtil.getBalance(
            meowTokenAmount = meowTokens.sumOf { it.tokenAmount },
            meowPrice = meowPrice
        )
        userWalletBalance.doubleValue = userBalance
    }

    private fun CoroutineScope.claimUserRewards(matrixUser: MatrixUser,
                                                claimRewardsActionState: MutableState<AsyncAction<String>>,
                                                refreshWallet: () -> Unit
    ) = launch {
        matrixUser.walletAddress?.let {
            claimRewardsActionState.value = AsyncAction.Loading
            client.claimRewards(it)
                .onSuccess { transaction ->
                    claimRewardsActionState.value = AsyncAction.Success(transaction)
                    client.getUserRewards()
                    refreshWallet()
                }
                .onFailure { error ->
                    claimRewardsActionState.value = AsyncAction.Failure(error)
                }
        }
    }

    private fun fetchStakingData(
        stakePoolsContent: MutableState<List<HomeStakePool>>,
        meowPrice: ZeroMeowPrice?,
        userAddress: String,
        refreshAllData: Boolean = false,
        onRefreshAllData: (HomeStakePool) -> Unit = {},
    ) {
        val price = meowPrice ?: return
        val stakePools = ZeroStakingUtil.stakePools
        stakePools.forEach { stakePool ->
            val poolAddress = stakePool.address
            val chainId = stakePool.chainId
            withIOScope {
                coroutineScope {
                    val (totalStakedResult, stakingConfigResult, stakeStatusResult, rewardsInfoResult) = awaitAll(
                        async { client.getTotalStaked(poolAddress, chainId) },
                        async { client.getStakingConfig(poolAddress, chainId) },
                        async { client.getStakerStatusInfo(userAddress, poolAddress, chainId) },
                        async { client.getStakeRewardsInfo(userAddress, poolAddress, chainId) }
                    )

                    if (listOf(totalStakedResult, stakingConfigResult, stakeStatusResult, rewardsInfoResult).all { it.isSuccess }) {
                        val totalStaked = (totalStakedResult as Result<String>).getOrNull() ?: return@coroutineScope
                        val stakingConfig = (stakingConfigResult as Result<ZeroStakingConfig>).getOrNull() ?: return@coroutineScope
                        val stakeStatus = (stakeStatusResult as Result<ZeroStakingStatus>).getOrNull() ?: return@coroutineScope
                        val rewardsInfo = (rewardsInfoResult as Result<ZeroStakingUserRewardsInfo>).getOrNull() ?: return@coroutineScope

                        val pool = HomeStakePool.from(
                            userAddress = userAddress,
                            pool = stakePool,
                            meowPrice = price,
                            totalStakedAmount = totalStaked,
                            stakingConfig = stakingConfig,
                            stakingStatus = stakeStatus,
                            rewardsInfo = rewardsInfo
                        )

                        val existingPoolsList = stakePoolsContent.value.toMutableList()
                        existingPoolsList.add(pool)
                        stakePoolsContent.value = existingPoolsList.distinctBy { it.poolAddress }

                        if (refreshAllData) { onRefreshAllData(pool) }
                    }
                }
            }
        }
    }

    private fun CoroutineScope.fetchPoolData(
        pool: HomeStakePool,
        selectedStakePoolState: MutableState<SelectedStakePool?>,
        genericActionState: MutableState<AsyncAction<Unit>>,
        showWalletStakingSheetState: MutableState<Boolean>,
        isSilentRefresh: Boolean = false
    ) = launch {
        genericActionState.value = AsyncAction.Uninitialized
        if (!isSilentRefresh) {
            genericActionState.value = AsyncAction.Loading
        }

        val chainId = pool.chainId
        val fetchTokenData: suspend (String) -> Pair<ZeroWalletTokenInfo?, ZeroWalletTokenBalance?> = { tokenAddress ->
            val results = awaitAll(
                async { client.getTokenInfo(tokenAddress, chainId) },
                async { client.getTokenBalance(pool.userWalletAddress, tokenAddress, chainId) },
            )
            val tokenInfoResult = (results[0] as? Result<ZeroWalletTokenInfo>)?.getOrNull()
            val tokenBalanceResult = (results[1] as? Result<ZeroWalletTokenBalance>)?.getOrNull()
            (tokenInfoResult to tokenBalanceResult)
        }
        val tokensResult = awaitAll(
            async { client.getStakingToken(pool.poolAddress, chainId) },
            async { client.getRewardToken(pool.poolAddress, chainId) },
        )
        var stakeTokenInfo: ZeroWalletTokenInfo? = null
        var stakeTokenBalance: ZeroWalletTokenBalance? = null
        var rewardsTokenInfo: ZeroWalletTokenInfo? = null
        var rewardsTokenBalance: ZeroWalletTokenBalance? = null
        (tokensResult[0] as? Result<ZeroTokenAddress>)?.getOrNull()?.let {
            val (tokenInfo, tokenBalance) = fetchTokenData(it.address)
            stakeTokenInfo = tokenInfo
            stakeTokenBalance = tokenBalance
        }
        (tokensResult[1] as? Result<ZeroTokenAddress>)?.getOrNull()?.let {
            val (tokenInfo, tokenBalance) = fetchTokenData(it.address)
            rewardsTokenInfo = tokenInfo
            rewardsTokenBalance = tokenBalance
        }
        if (stakeTokenInfo != null && stakeTokenBalance != null && rewardsTokenInfo != null && rewardsTokenBalance != null) {
            genericActionState.value = AsyncAction.Success(Unit)
            selectedStakePoolState.value = SelectedStakePool(
                poolInfo = pool,
                stakeTokenInfo = stakeTokenInfo,
                stakeTokenBalance = stakeTokenBalance,
                rewardsTokenInfo = rewardsTokenInfo,
                rewardsTokenBalance = rewardsTokenBalance
            )
            showWalletStakingSheetState.value = true
        } else {
            genericActionState.value = AsyncAction.Failure(Throwable("Failed to fetch pool data. Required values are missing"))
        }
    }

    private fun CoroutineScope.claimStakingRewards(
        pool: HomeStakePool,
        genericActionState: MutableState<AsyncAction<Unit>>,
        onDone: () -> Unit,
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.claimStakingRewards(
            userAddress = pool.userWalletAddress,
            poolAddress = pool.poolAddress,
            chainId = pool.chainId
        ).onSuccess {
            genericActionState.value = AsyncAction.Success(Unit)
            onDone()
        }.onFailure {
            genericActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun CoroutineScope.stakeAmount(
        stakePool: SelectedStakePool,
        amount: String,
        walletStakeActionState: MutableState<AsyncAction<String>>
    ) = launch {
        walletStakeActionState.value = AsyncAction.Loading
        val transactionAmount = amount.toDoubleOrNull() ?: 0.0
        client.stakeAmount(
            userAddress = stakePool.poolInfo.userWalletAddress,
            amount = toSmallestUnit(transactionAmount, 18),
            poolAddress = stakePool.poolInfo.poolAddress,
            tokenAddress = stakePool.stakeTokenInfo.address,
            chainId = stakePool.poolInfo.chainId
        ).onSuccess {
            walletStakeActionState.value = AsyncAction.Success(it)
        }.onFailure {
            walletStakeActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun CoroutineScope.unstakeAmount(
        stakePool: SelectedStakePool,
        amount: String,
        walletStakeActionState: MutableState<AsyncAction<String>>
    ) = launch {
        walletStakeActionState.value = AsyncAction.Loading
        val transactionAmount = amount.toDoubleOrNull() ?: 0.0
        client.unstakeAmount(
            userAddress = stakePool.poolInfo.userWalletAddress,
            amount = toSmallestUnit(transactionAmount, 18),
            poolAddress = stakePool.poolInfo.poolAddress,
            chainId = stakePool.poolInfo.chainId
        ).onSuccess {
            walletStakeActionState.value = AsyncAction.Success(it)
        }.onFailure {
            walletStakeActionState.value = AsyncAction.Failure(it)
        }
    }

    private fun toSmallestUnit(amount: Double, decimals: Int): String {
        val decimalValue = BigDecimal.valueOf(amount)
            .multiply(BigDecimal.TEN.pow(decimals))
            .setScale(0, RoundingMode.DOWN)
        return decimalValue.toBigInteger().toString()
    }
}
