/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val FEED_DETAILS_COMMENTS_PAGE_SIZE = 15

class FeedDetailsPresenter @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted private val feed: ZeroFeed
) : Presenter<FeedDetailsState> {

    @AssistedFactory
    interface Factory {
        fun create(feed: ZeroFeed): FeedDetailsPresenter
    }

    private val _feedReplies: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): FeedDetailsState {
        val coroutineScope = rememberCoroutineScope()
        val matrixUser = client.userProfile.collectAsState()

        val feedDetailsFlow: MutableState<ZeroFeed> = remember { mutableStateOf(feed) }
        val feedRepliesFlow: MutableState<List<ZeroFeed>> = remember { mutableStateOf(emptyList()) }

        val postReplyText: MutableState<String> = remember { mutableStateOf("") }

        val userRewards = client.userRewards.collectAsState()

        fun handleEvents(event: FeedDetailsEvents) {
            when (event) {
                FeedDetailsEvents.RefreshFeed -> coroutineScope.refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
                is FeedDetailsEvents.AddMeowToFeed -> coroutineScope.addMeowToFeed(event.feed, event.meowCount, feedDetailsFlow)
                FeedDetailsEvents.LoadMoreReplies -> coroutineScope.loadMoreReplies(feed.id, feedRepliesFlow)
                is FeedDetailsEvents.PostReplyTextChanged -> postReplyText.value = event.text
                FeedDetailsEvents.PostReply -> {
                }
            }
        }

        LaunchedEffect(Unit) {
            coroutineScope.refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
        }

        return FeedDetailsState(
            zeroFeed = feedDetailsFlow.value,
            userRewards = userRewards.value,
            matrixUser = matrixUser.value,
            loggedInUserId = client.sessionId.extractedDisplayName,
            feedComments = feedRepliesFlow.value,
            postReplyText = postReplyText.value,
            eventSink = ::handleEvents
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
            feedDetailsFlow.value = it
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
}
