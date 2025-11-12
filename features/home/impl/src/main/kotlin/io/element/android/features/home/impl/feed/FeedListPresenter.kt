/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dev.zacsweers.metro.Inject
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.support.zero.common.extension.withIOScope
import io.element.android.support.zero.common.util.FeedItemMediaCache
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val HOME_FEED_PAGE_SIZE = 15

@Inject
class FeedListPresenter(
    private val client: MatrixClient,
) : Presenter<FeedListState> {

    private val _allFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): FeedListState {
        val coroutineState = rememberCoroutineScope()

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }

        fun handleEvent(event: FeedListEvents) {
            when (event) {
                is FeedListEvents.LoadMoreFeeds -> {
                    _allFeeds.apply {
                        clear()
                        addAll(event.currentFeeds)
                    }
                    coroutineState.loadMoreHomeFeeds(event.followingFeeds, event.currentFeeds.size)
                }
                is FeedListEvents.RefreshFeeds -> coroutineState.forceRefreshHomeFeeds(event.followingFeeds)
                is FeedListEvents.AddMeowToFeed -> GlobalScope.addMeowToFeed(event.feed, event.meowCount, genericActionState)
                is FeedListEvents.LoadFeedMedia -> {
                    coroutineState.loadFeedMediaPreview(event.mediaId, feedMediaPreviewActionState)
                }
                FeedListEvents.DismissFeedMedia -> feedMediaPreviewActionState.value = AsyncAction.Uninitialized
            }
        }

        LaunchedEffect(Unit) {
            client.fetchAllFeeds(followingFeeds = true, limit = HOME_FEED_PAGE_SIZE, skip = 0)
        }

        val allFeedsContentState = allFeedsListContentState()

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
        val allCombinedFeeds = extractFeedsToFetchData(allFeedsContentState)
        fetchFeedMediaIfRequired(allCombinedFeeds, feedMediaMap)
        fetchLinksMetaDataIfRequired(allCombinedFeeds, feedLinkMetaDataMap)

        return FeedListState(
            genericActionState = genericActionState.value,
            contentState = allFeedsContentState,
            feedMediaMap = feedMediaMap,
            feedLinkMetaDataMap = feedLinkMetaDataMap,
            feedMediaPreviewState = feedMediaPreviewActionState.value,
            eventSink = ::handleEvent
        )
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

    private fun extractFeedsToFetchData(
        allFeedsContentState: FeedListContentState
    ): List<ZeroFeed> {
        val allFeeds = (allFeedsContentState as? FeedListContentState.Feeds)?.feeds ?: emptyList()
        return allFeeds.distinctBy { it.id }
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

    private fun CoroutineScope.loadMoreHomeFeeds(followingFeedsOnly: Boolean, skip: Int) = launch {
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = skip)
    }

    private fun CoroutineScope.forceRefreshHomeFeeds(followingFeedsOnly: Boolean) = launch {
        _allFeeds.clear()
        client.fetchAllFeeds(followingFeeds = followingFeedsOnly, limit = HOME_FEED_PAGE_SIZE, skip = 0)
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int, genericActionState: MutableState<AsyncAction<Unit>>) = launch {
        client.addMeowToFeed(feed, meowCount)
            .onFailure {
                genericActionState.value = AsyncAction.Failure(it)
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
}
