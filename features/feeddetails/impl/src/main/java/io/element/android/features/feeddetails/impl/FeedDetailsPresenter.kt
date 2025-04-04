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

    @Composable
    override fun present(): FeedDetailsState {
        val coroutineScope = rememberCoroutineScope()

        val feedDetailsFlow: MutableState<ZeroFeed> = remember { mutableStateOf(feed) }
        val feedRepliesFlow: MutableState<List<ZeroFeed>> = remember { mutableStateOf(emptyList()) }

        val userRewards = client.userRewards.collectAsState()

        LaunchedEffect(Unit) {
            coroutineScope.refreshFeed(feed.id, feedDetailsFlow, feedRepliesFlow)
        }

        return FeedDetailsState(
            zeroFeed = feedDetailsFlow.value,
            userRewards = userRewards.value,
            loggedInUserId = client.sessionId.extractedDisplayName,
            feedComments = feedRepliesFlow.value
        )
    }

    private fun CoroutineScope.refreshFeed(feedId: String,
                                           feedDetailsFlow: MutableState<ZeroFeed>,
                                           feedRepliesFlow: MutableState<List<ZeroFeed>>
    ) = launch {
        val results = awaitAll(
            async { client.fetchFeedDetails(feedId) },
            async { client.fetchFeedReplies(feedId, limit = FEED_DETAILS_COMMENTS_PAGE_SIZE, skip = 0) }
        )
        (results.first() as? Result<ZeroFeed?>)?.getOrNull()?.let {
            feedDetailsFlow.value = it
        }
        (results[1] as? Result<List<ZeroFeed>>)?.getOrNull()?.let {
            feedRepliesFlow.value = it
        }
    }
}
