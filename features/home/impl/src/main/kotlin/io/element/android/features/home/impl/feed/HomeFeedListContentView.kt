/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.features.home.impl.model.FeedsScreenTab
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.userProfile
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Composable
fun HomeFeedListContentView(
    contentState: FeedListContentState,
    feedMediaMap: Map<String, FeedMedia>,
    feedLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    eventSink: (HomeEvents.FeedEvents) -> Unit,
    zeroUserRewards: ZeroUserRewards,
    loggedInUserId: UserId,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedFeedTab = rememberSaveable { mutableStateOf(FeedsScreenTab.FOLLOWING) }
    Column(modifier = modifier) {
        FeedsScreenTabView(
            onTabSelected = { tab ->
                selectedFeedTab.value = tab
                eventSink(HomeEvents.FeedEvents.RefreshFeeds(
                    followingFeeds = tab == FeedsScreenTab.FOLLOWING
                ))
            }
        )
        Box {
            when (contentState) {
                is FeedListContentState.Skeleton -> {
                    SkeletonView(
                        count = contentState.count,
                    )
                }
                is FeedListContentState.Empty -> {
                    HomeTabContentEmptyView(modifier = modifier, text = "No feeds yet")
                }
                is FeedListContentState.Feeds -> {
                    FeedsViewList(
                        state = contentState,
                        feedMediaMap = feedMediaMap,
                        feedLinkMetaDataMap = feedLinkMetaDataMap,
                        eventSink = eventSink,
                        zeroUserRewards = zeroUserRewards,
                        loggedInUserId = loggedInUserId,
                        onFeedClick = onFeedClick,
                        onFeedUserClick = onFeedUserClick,
                        isFollowingFeedsTabSelected = {
                            selectedFeedTab.value == FeedsScreenTab.FOLLOWING
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SkeletonView(count: Int, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(count) { index ->
            item {
                HomeFeedPlaceholderRow()
                if (index != count - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FeedsViewList(
    state: FeedListContentState.Feeds,
    feedMediaMap: Map<String, FeedMedia>,
    feedLinkMetaDataMap: Map<String, ZeroLinkPreview>,
    eventSink: (HomeEvents.FeedEvents) -> Unit,
    zeroUserRewards: ZeroUserRewards,
    loggedInUserId: UserId,
    onFeedClick: (ZeroFeed) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    isFollowingFeedsTabSelected: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    var refreshing by remember(state) { mutableStateOf(false) }
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        refreshing = true
        eventSink(HomeEvents.FeedEvents.RefreshFeeds(isFollowingFeedsTabSelected()))
        Handler(Looper.getMainLooper()).postDelayed({
            refreshing = false
        }, 1_500)
    })
    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.feeds.lastIndex - 1
        }
    }

    // Load more items when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems) {
            isLoadingMoreItems = true
            eventSink(HomeEvents.FeedEvents.LoadMoreFeeds(state.feeds, isFollowingFeedsTabSelected()))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = state.feeds
            ) { index, feed ->
                val feedMedia = feedMediaMap[feed.id]
                val feedLinkMetaData = feedLinkMetaDataMap[feed.id]
                HomeFeedRow(
                    feed = feed.copy(media = feedMedia, linkMetaData = feedLinkMetaData),
                    zeroUserRewards = zeroUserRewards,
                    isMyOwnFeed = feed.userId == loggedInUserId.extractedDisplayName,
                    onFeedClick = { onFeedClick(
                        feed.copy(media = feedMedia, linkMetaData = feedLinkMetaData)
                    ) },
                    onFeedUserClick = {
                        onFeedUserClick(feed.userProfile)
                    },
                    onAddMeowToFeed = { meowCount ->
                        eventSink(HomeEvents.FeedEvents.AddMeowToFeed(feed, meowCount))
                    },
                    onMediaTapped = { mediaId ->
                        eventSink(HomeEvents.FeedEvents.LoadFeedMedia(mediaId))
                    }
                )
                if (index != state.feeds.lastIndex) {
                    HorizontalDivider()
                }
            }

            /*item {
                CircularProgressIndicator(Modifier.size(32.dp))
            }*/
            item {
                Spacer(Modifier.size(100.dp))
            }
        }

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@PreviewsDayNight
@Composable
internal fun HomeFeedListContentViewPreview(@PreviewParameter(FeedListContentStateProvider::class) state: FeedListContentState) = ElementPreview {
    HomeFeedListContentView(
        contentState = state,
        feedMediaMap = emptyMap(),
        feedLinkMetaDataMap = emptyMap(),
        zeroUserRewards = ZeroUserRewards.empty(),
        loggedInUserId = UserId(""),
        eventSink = {},
        onFeedClick = {},
        onFeedUserClick = {}
    )
}
