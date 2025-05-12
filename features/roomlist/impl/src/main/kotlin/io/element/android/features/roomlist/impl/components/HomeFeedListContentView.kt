/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.roomlist.impl.FeedListContentState
import io.element.android.features.roomlist.impl.FeedListContentStateProvider
import io.element.android.features.roomlist.impl.RoomListEvents
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards

@Composable
fun HomeFeedListContentView(
    contentState: FeedListContentState,
    feedMediaMap: Map<String, FeedMedia>,
    eventSink: (RoomListEvents) -> Unit,
    zeroUserRewards: ZeroUserRewards,
    isProfileFeedList: Boolean,
    onFeedClick: (ZeroFeed) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (contentState) {
            is FeedListContentState.Skeleton -> {
                SkeletonView(
                    count = contentState.count,
                )
            }
            is FeedListContentState.Empty -> {
                EmptyView(modifier = modifier)
            }
            is FeedListContentState.Feeds -> {
                FeedsViewList(
                    state = contentState,
                    feedMediaMap = feedMediaMap,
                    eventSink = eventSink,
                    zeroUserRewards = zeroUserRewards,
                    isProfileFeedList = isProfileFeedList,
                    onFeedClick = onFeedClick
                )
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

@Composable
private fun EmptyView(
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        EmptyScaffold(
            title = "No feeds yet",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FeedsViewList(
    state: FeedListContentState.Feeds,
    feedMediaMap: Map<String, FeedMedia>,
    eventSink: (RoomListEvents) -> Unit,
    zeroUserRewards: ZeroUserRewards,
    isProfileFeedList: Boolean,
    onFeedClick: (ZeroFeed) -> Unit,
    modifier: Modifier = Modifier,
) {
    var refreshing by remember(state) { mutableStateOf(false) }
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        refreshing = true
        if (isProfileFeedList) {
            eventSink(RoomListEvents.RefreshMyFeeds)
        } else {
            eventSink(RoomListEvents.RefreshFeeds)
        }
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
            if (isProfileFeedList) {
                eventSink(RoomListEvents.LoadMoreMyFeeds(state.feeds))
            } else {
                eventSink(RoomListEvents.LoadMoreFeeds(state.feeds))
            }
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
                HomeFeedRow(
                    feed = feed,
                    feedMedia = feedMedia,
                    zeroUserRewards = zeroUserRewards,
                    isMyOwnFeed = isProfileFeedList,
                    onFeedClick = { onFeedClick(
                        feed.copy(media = feedMedia)
                    ) },
                    onAddMeowToFeed = { meowCount ->
                        eventSink(RoomListEvents.AddMeowToFeed(feed, meowCount))
                    }
                )
                if (index != state.feeds.lastIndex) {
                    HorizontalDivider()
                }
            }

            /*item {
                CircularProgressIndicator(Modifier.size(32.dp))
            }*/
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
        zeroUserRewards = ZeroUserRewards.empty(),
        isProfileFeedList = false,
        eventSink = {},
        onFeedClick = {}
    )
}
