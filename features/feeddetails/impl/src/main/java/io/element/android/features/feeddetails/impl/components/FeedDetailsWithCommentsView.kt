/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl.components

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import io.element.android.features.feeddetails.impl.FeedDetailsState
import io.element.android.features.roomlist.impl.components.HomeFeedRow
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedDetailsWithCommentsView(
    modifier: Modifier = Modifier,
    state: FeedDetailsState,
    onReplyClick: (ZeroFeed) -> Unit,
    onAddMeowToFeed: (Int) -> Unit,
) {
    var refreshing by remember(state) { mutableStateOf(false) }
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        refreshing = true
        //TODO: eventSink(RoomListEvents.RefreshMyFeeds)
        Handler(Looper.getMainLooper()).postDelayed({
            refreshing = false
        }, 1_500)
    })
    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.feedComments.lastIndex - 1
        }
    }

    // Load more comments when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems) {
            isLoadingMoreItems = true
            //TODO: eventSink(RoomListEvents.RefreshMyFeeds)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column {
                    FeedDetailsCell(
                        feed = state.zeroFeed,
                        zeroUserRewards = state.userRewards,
                        isMyOwnFeed = state.zeroFeed.userId == state.loggedInUserId,
                        onAddMeowToFeed = onAddMeowToFeed
                    )
                    HorizontalDivider()
                }
            }

            itemsIndexed(
                items = state.feedComments
            ) { index, comment ->
                HomeFeedRow(
                    feed = comment,
                    zeroUserRewards = state.userRewards,
                    isMyOwnFeed = comment.userId == state.loggedInUserId,
                    onFeedClick = { onReplyClick(comment) },
                    onAddMeowToFeed = onAddMeowToFeed
                )
                if (index != state.feedComments.lastIndex) {
                    HorizontalDivider()
                }
            }
        }

        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
