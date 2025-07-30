/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import io.element.android.features.feeduserprofile.impl.FeedUserProfileEvents
import io.element.android.features.feeduserprofile.impl.FeedUserProfileState
import io.element.android.features.feeduserprofile.impl.FeedUserProfileStateProvider
import io.element.android.features.home.impl.feed.HomeFeedRow
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@Composable
fun UserFeedsListView(
    state: FeedUserProfileState,
    onUserFeedClick: (ZeroFeed) -> Unit = {}
) {
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.userFeeds.lastIndex + 1
        }
    }

    // Load more comments when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems) {
            isLoadingMoreItems = true
            state.eventSink(FeedUserProfileEvents.LoadMoreUserFeeds)
        }
    }

    Box(modifier = Modifier
        .offset(y = (-48).dp)
        .fillMaxSize()
        .padding(horizontal = 8.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = state.userFeeds
            ) { index, feed ->
                val media = state.userFeedsMediaMap[feed.id]
                val linkMetaData = state.userFeedsLinkMetaDataMap[feed.id]

                HomeFeedRow(
                    feed = feed.copy(media = media, linkMetaData = linkMetaData),
                    zeroUserRewards = state.userRewards,
                    isMyOwnFeed = state.isMyOwnProfile,
                    showThreadLine = false,
                    onFeedClick = { onUserFeedClick(
                        feed.copy(media = media, linkMetaData = linkMetaData)
                    ) },
                    onFeedUserClick = {},
                    onAddMeowToFeed = { meowCount ->
                        state.eventSink(FeedUserProfileEvents.AddMeowToFeed(feed, meowCount))
                    },
                    onMediaTapped = { mediaId ->
                        state.eventSink(FeedUserProfileEvents.LoadFeedMedia(mediaId))
                    }
                )
                if (index != state.userFeeds.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
private fun UserFeedsListViewPreview(
    @PreviewParameter(FeedUserProfileStateProvider::class) state: FeedUserProfileState
) = ElementPreview {
    UserFeedsListView(
        state = state
    )
}
