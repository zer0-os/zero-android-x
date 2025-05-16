/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.LocalContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.features.roomlist.impl.datasource.FeedItemMediaCache
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.CreateFeedMediaAttachment
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.mediapickers.api.PickerProvider
import io.element.android.support.zero.common.extension.localFile
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val FEED_DETAILS_COMMENTS_PAGE_SIZE = 15

class FeedDetailsPresenter @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted private val feed: ZeroFeed,
    private val mediaPickerProvider: PickerProvider,
) : Presenter<FeedDetailsState> {

    @AssistedFactory
    interface Factory {
        fun create(feed: ZeroFeed): FeedDetailsPresenter
    }

    private val _feedReplies: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): FeedDetailsState {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val matrixUser = client.userProfile.collectAsState()
        val genericActionState: MutableState<AsyncData<Unit>> = remember { mutableStateOf(AsyncData.Uninitialized) }

        val feedDetailsFlow: MutableState<ZeroFeed> = remember { mutableStateOf(feed) }
        val feedRepliesFlow: MutableState<List<ZeroFeed>> = remember { mutableStateOf(emptyList()) }
        val feedRepliesMediaMap = remember { mutableStateMapOf<String, FeedMedia>() }
        val feedRepliesLinkMetaDataMap = remember { mutableStateMapOf<String, ZeroLinkPreview>() }

        val postReplyText: MutableState<String> = remember { mutableStateOf("") }
        val newFeedAttachmentState: MutableState<CreateFeedMediaAttachment?> = remember { mutableStateOf(null) }

        val userRewards = client.userRewards.collectAsState()

        val galleryMediaPicker = mediaPickerProvider.registerGalleryPicker { uri, mimeType ->
            handlePickedMedia(context, newFeedAttachmentState, uri, mimeType)
        }

        fun handleEvents(event: FeedDetailsEvents) {
            when (event) {
                FeedDetailsEvents.RefreshFeed -> coroutineScope.refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
                is FeedDetailsEvents.AddMeowToFeed -> coroutineScope.addMeowToFeed(event.feed, event.meowCount, feedDetailsFlow)
                FeedDetailsEvents.LoadMoreReplies -> coroutineScope.loadMoreReplies(feed.id, feedRepliesFlow)
                is FeedDetailsEvents.PostReplyTextChanged -> postReplyText.value = event.text
                FeedDetailsEvents.PostReply -> coroutineScope.postMyReply(
                    postReplyText, newFeedAttachmentState, genericActionState, feedDetailsFlow, feedRepliesFlow
                )
                FeedDetailsEvents.SelectMedia -> {
                    coroutineScope.launch {
                        galleryMediaPicker.launch()
                    }
                }
                FeedDetailsEvents.RemoveMedia -> newFeedAttachmentState.value = null
                FeedDetailsEvents.HideError -> genericActionState.value = AsyncData.Uninitialized
            }
        }

        LaunchedEffect(Unit) {
            feedRepliesMediaMap.putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
            feedRepliesLinkMetaDataMap.putAll(FeedItemMediaCache.getCachedFeedItemLinkMetaDataMap())
            coroutineScope.refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
            client.getUserProfile()
        }

        coroutineScope.fetchFeedRepliesMedia(feedRepliesFlow.value, feedRepliesMediaMap)
        coroutineScope.fetchFeedRepliesLinkMetaData(feedRepliesFlow.value, feedRepliesLinkMetaDataMap)

        return FeedDetailsState(
            zeroFeed = feedDetailsFlow.value,
            userRewards = userRewards.value,
            matrixUser = matrixUser.value,
            loggedInUserId = client.sessionId.extractedDisplayName,
            feedComments = feedRepliesFlow.value,
            feedCommentsMediaMap = feedRepliesMediaMap,
            feedCommentsLinkMetaDataMap = feedRepliesLinkMetaDataMap,
            postReplyText = postReplyText.value,
            postReplyAttachment = newFeedAttachmentState.value,
            eventSink = ::handleEvents,
            genericActionState = genericActionState.value
        )
    }

    private fun CoroutineScope.refreshFeed(
        feedId: String,
        feedDetailsFlow: MutableState<ZeroFeed>,
        feedRepliesFlow: MutableState<List<ZeroFeed>>
    ) = launch {
        val results = awaitAll(
            async { client.fetchFeedDetails(feedId) },
            async { client.fetchFeedReplies(feedId, limit = FEED_DETAILS_COMMENTS_PAGE_SIZE, skip = 0) }
        )
        (results[0] as? Result<ZeroFeed?>)?.getOrNull()?.let {
            feedDetailsFlow.value = it.copy(media = feed.media, linkMetaData = feed.linkMetaData)
        }
        (results[1] as? Result<List<ZeroFeed>>)?.getOrNull()?.let {
            _feedReplies.addAll(it)
            feedRepliesFlow.value = it
        }
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int, feedDetailsFlow: MutableState<ZeroFeed>) = launch {
        val result = client.addMeowToFeed(feed, meowCount)
        result.getOrNull()?.let {
            feedDetailsFlow.value = it
        }
    }

    private fun CoroutineScope.loadMoreReplies(feedId: String, feedRepliesFlow: MutableState<List<ZeroFeed>>) = launch {
        if (_feedReplies.isNotEmpty()) {
            val skip = _feedReplies.size
            val result = client.fetchFeedReplies(feedId, limit = FEED_DETAILS_COMMENTS_PAGE_SIZE, skip = skip)
            result.getOrNull()?.let { newReplies ->
                _feedReplies.addAll(newReplies)
                feedRepliesFlow.value = _feedReplies
                    .distinctBy { it.id }
            }
        }
    }

    private fun CoroutineScope.postMyReply(
        replyText: MutableState<String>,
        feedAttachment: MutableState<CreateFeedMediaAttachment?>,
        genericActionState: MutableState<AsyncData<Unit>>,
        feedDetailsFlow: MutableState<ZeroFeed>,
        feedRepliesFlow: MutableState<List<ZeroFeed>>
    ) = launch {
        genericActionState.value = AsyncData.Loading()
        val postText = replyText.value
        val attachment = feedAttachment.value
        replyText.value = ""
        feedAttachment.value = null
        client.createNewFeed(postText, attachment, feed.id)
            .onSuccess {
                genericActionState.value = AsyncData.Success(Unit)
                refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
            }
            .onFailure { error ->
                genericActionState.value = AsyncData.Failure(error)
            }
    }

    private fun CoroutineScope.fetchFeedRepliesMedia(replies: List<ZeroFeed>,
                                                     feedRepliesMediaMap: SnapshotStateMap<String, FeedMedia>
    ) = launch {
        val feedsToFetch = replies.mapNotNull { feed ->
            val mediaId = feed.media?.id ?: return@mapNotNull null
            if (feedRepliesMediaMap.contains(feed.id) || FeedItemMediaCache.containsMedia(feed.id)) return@mapNotNull null
            else feed
        }
        val results = feedsToFetch.map { feed ->
            async { feed.id to client.fetchFeedMedia(feed.media!!.id) }
        }.awaitAll()
        results.forEach { (feedId, media) ->
            media.getOrNull()?.let { feedMedia ->
                FeedItemMediaCache.addFeedMedia(feedId, feedMedia)
                feedRepliesMediaMap[feedId] = feedMedia
            }
        }
    }

    private fun CoroutineScope.fetchFeedRepliesLinkMetaData(replies: List<ZeroFeed>,
                                                     feedRepliesLinkMetaDataMap: SnapshotStateMap<String, ZeroLinkPreview>
    ) = launch {
        val feedsToFetch = replies.mapNotNull { feed ->
            val availableYoutubeUrl = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text) ?: return@mapNotNull null
            if (feedRepliesLinkMetaDataMap.contains(feed.id) || FeedItemMediaCache.containsUrlMetaData(feed.id)) return@mapNotNull null
            else feed
        }
        val results = feedsToFetch.map { feed ->
            val url = YoutubeLinkHelperUtil.extractFirstAvailableYoutubeUrl(feed.text)!!
            async { feed.id to client.fetchUrlMetaData(url) }
        }.awaitAll()
        results.forEach { (feedId, urlMetaData) ->
            urlMetaData.getOrNull()?.let { metaData ->
                FeedItemMediaCache.addLinkMetaData(feedId, metaData)
                feedRepliesLinkMetaDataMap[feedId] = metaData
            }
        }
    }

    private fun handlePickedMedia(
        context: Context,
        newFeedAttachmentState: MutableState<CreateFeedMediaAttachment?>,
        uri: Uri?,
        mimeType: String? = null,
    ) {
        if (uri != null && mimeType != null) {
            val mediaFile = uri.localFile(context)
            mediaFile?.let {
                val media = CreateFeedMediaAttachment(it, mimeType)
                newFeedAttachmentState.value = media
            }
        }
    }
}
