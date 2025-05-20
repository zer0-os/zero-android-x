/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.support.zero.common.util.FeedItemMediaCache
import io.element.android.support.zero.common.util.YoutubeLinkHelperUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

private const val USER_FEED_PAGE_SIZE = 15

class FeedUserProfilePresenter @AssistedInject constructor(
    private val client: MatrixClient,
    @Assisted private val userProfile: FeedUserProfileView,
) : Presenter<FeedUserProfileState> {

    @AssistedFactory
    interface Factory {
        fun create(userProfile: FeedUserProfileView): FeedUserProfilePresenter
    }

    private val _userFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    override fun present(): FeedUserProfileState {
        val coroutineScope = rememberCoroutineScope()
        val genericActionState: MutableState<AsyncData<Unit>> = remember { mutableStateOf(AsyncData.Uninitialized) }

        val userProfileFlow: MutableState<FeedUserProfileView> = remember { mutableStateOf(userProfile) }
        val userProfileFollowingFlow: MutableState<Boolean?> = remember { mutableStateOf(null) }
        val userFeedsFlow: MutableState<List<ZeroFeed>> = remember { mutableStateOf(emptyList()) }
        val userFeedsMediaMap = remember { mutableStateMapOf<String, FeedMedia>() }
        val userFeedsLinkMetaDataMap = remember { mutableStateMapOf<String, ZeroLinkPreview>() }

        val userRewards = client.userRewards.collectAsState()

        fun handleEvents(event: FeedUserProfileEvents) {
            when (event) {
                is FeedUserProfileEvents.AddMeowToFeed ->
                    coroutineScope.addMeowToFeed(event.feed, event.meowCount)
                FeedUserProfileEvents.LoadMoreUserFeeds ->
                    coroutineScope.loadMoreUserFeeds(userProfile.primaryZid, userFeedsFlow)
                FeedUserProfileEvents.ToggleFollowUser ->
                    coroutineScope.toggleFollowUser(userProfileFlow, userProfileFollowingFlow, userFeedsFlow, genericActionState)
                FeedUserProfileEvents.HideError ->
                    genericActionState.value = AsyncData.Uninitialized
            }
        }

        LaunchedEffect(Unit) {
            userFeedsMediaMap.putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
            userFeedsLinkMetaDataMap.putAll(FeedItemMediaCache.getCachedFeedItemLinkMetaDataMap())
            coroutineScope.fetchUserProfileData(userProfileFlow, userProfileFollowingFlow, userFeedsFlow)
        }

        coroutineScope.fetchUserFeedsMedia(userFeedsFlow.value, userFeedsMediaMap)
        coroutineScope.fetchUserFeedsLinkMetaData(userFeedsFlow.value, userFeedsLinkMetaDataMap)

        return FeedUserProfileState(
            userProfile = userProfileFlow.value,
            userRewards = userRewards.value,
            userFeeds = userFeedsFlow.value,
            userFeedsMediaMap = userFeedsMediaMap,
            userFeedsLinkMetaDataMap = userFeedsLinkMetaDataMap,
            isUserFollowed = userProfileFollowingFlow.value,
            isMyOwnProfile = client.sessionId.extractedDisplayName == userProfile.userId,
            eventSink = ::handleEvents,
            genericActionState = genericActionState.value
        )
    }

    private fun CoroutineScope.fetchUserProfileData(
        userProfileFlow: MutableState<FeedUserProfileView>,
        userProfileFollowingFlow: MutableState<Boolean?>,
        userFeedsFlow: MutableState<List<ZeroFeed>>
    ) = launch {
        val results = awaitAll(
            async { client.fetchFeedUserProfile(userProfile.primaryZid) },
            async { client.fetchUserFollowingStatus(userProfile.userId) },
            async { client.fetchAllUserFeeds(userProfile.userId, USER_FEED_PAGE_SIZE, 0) },
        )
        (results[0] as? Result<FeedUserProfileView?>)?.getOrNull()?.let {
            userProfileFlow.value = it
        }
        (results[1] as? Result<Boolean>)?.getOrNull()?.let {
            userProfileFollowingFlow.value = it
        }
        (results[2] as? Result<List<ZeroFeed>>)?.getOrNull()?.let {
            _userFeeds.addAll(it)
            userFeedsFlow.value = it
        }
    }

    private fun CoroutineScope.toggleFollowUser(
        userProfileFlow: MutableState<FeedUserProfileView>,
        userProfileFollowingFlow: MutableState<Boolean?>,
        userFeedsFlow: MutableState<List<ZeroFeed>>,
        genericActionState: MutableState<AsyncData<Unit>>,
    ) = launch {
        genericActionState.value = AsyncData.Loading()
        val isFollowing = userProfileFollowingFlow.value ?: false
        val call = if (isFollowing) {
            client.unFollowUser(userProfile.userId)
        } else {
            client.followUser(userProfile.userId)
        }
        call.onSuccess {
            genericActionState.value = AsyncData.Success(Unit)
            fetchUserProfileData(userProfileFlow, userProfileFollowingFlow, userFeedsFlow)
        }.onFailure { error ->
            genericActionState.value = AsyncData.Failure(error)
        }
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int) = launch {
        val result = client.addMeowToFeed(feed, meowCount)
        result.getOrNull()?.let {
            //feedDetailsFlow.value = it
        }
    }

    private fun CoroutineScope.loadMoreUserFeeds(userZId: String, userFeedsFlow: MutableState<List<ZeroFeed>>) = launch {
        if (_userFeeds.isNotEmpty()) {
            val skip = _userFeeds.size
            val result = client.fetchAllUserFeeds(userZId, limit = USER_FEED_PAGE_SIZE, skip = skip)
            result.getOrNull()?.let { newReplies ->
                _userFeeds.addAll(newReplies)
                userFeedsFlow.value = _userFeeds
                    .distinctBy { it.id }
            }
        }
    }

    private fun CoroutineScope.fetchUserFeedsMedia(
        replies: List<ZeroFeed>,
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

    private fun CoroutineScope.fetchUserFeedsLinkMetaData(
        replies: List<ZeroFeed>,
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
}
