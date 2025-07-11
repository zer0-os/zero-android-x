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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.element.android.features.createroom.api.StartDMAction
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.primaryZIdOrWalletAddress
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.primaryZIdOrWalletAddress
import io.element.android.libraries.matrix.api.zero.feed.toZeroProfile
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
    private val startDMAction: StartDMAction,
    @Assisted private val userId: UserId,
    @Assisted private val userProfile: FeedUserProfileView?,
) : Presenter<FeedUserProfileState> {

    @AssistedFactory
    interface Factory {
        fun create(userId: UserId, userProfile: FeedUserProfileView?): FeedUserProfilePresenter
    }

    private val _userFeeds: MutableList<ZeroFeed> = mutableListOf()

    @Composable
    private fun getDmRoomId(): State<RoomId?> {
        return produceState<RoomId?>(initialValue = null) {
            value = client.findDM(userId).getOrNull()
        }
    }

    @Composable
    override fun present(): FeedUserProfileState {
        val coroutineScope = rememberCoroutineScope()
        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }

        val userProfileFlow: MutableState<FeedUserProfileView?> = rememberSaveable { mutableStateOf(userProfile) }
        val userProfileFollowingFlow: MutableState<Boolean?> = remember { mutableStateOf(null) }
        val userFeedsFlow: MutableState<List<ZeroFeed>> = remember { mutableStateOf(emptyList()) }
        val userFeedsMediaMap = remember { mutableStateMapOf<String, FeedMedia>() }
        val userFeedsLinkMetaDataMap = remember { mutableStateMapOf<String, ZeroLinkPreview>() }

        val feedMediaPreviewActionState: MutableState<AsyncAction<FeedMedia>> =
            remember { mutableStateOf(AsyncAction.Uninitialized) }

        val userRewards = client.userRewards.collectAsState()

        val startDmActionState: MutableState<AsyncAction<RoomId>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val dmRoomId by getDmRoomId()

        fun getMatrixUser(profileView: FeedUserProfileView?): MatrixUser {
            return MatrixUser(
                userId = userId,
                displayName = profileView?.firstName,
                avatarUrl = profileView?.profileImage,
                primaryZeroId = profileView?.primaryZid
            )
        }

        fun handleEvents(event: FeedUserProfileEvents) {
            when (event) {
                is FeedUserProfileEvents.AddMeowToFeed ->
                    coroutineScope.addMeowToFeed(event.feed, event.meowCount, userFeedsFlow)
                FeedUserProfileEvents.LoadMoreUserFeeds -> {
                    val primaryZId = userProfileFlow.value?.primaryZid ?: return
                    coroutineScope.loadMoreUserFeeds(primaryZId, userFeedsFlow)
                }
                FeedUserProfileEvents.ToggleFollowUser ->
                    coroutineScope.toggleFollowUser(userProfileFlow, userProfileFollowingFlow, userFeedsFlow, genericActionState)
                FeedUserProfileEvents.HideError ->
                    genericActionState.value = AsyncAction.Uninitialized
                FeedUserProfileEvents.StartDM -> {
                    coroutineScope.launch {
                        startDMAction.execute(
                            matrixUser = getMatrixUser(userProfileFlow.value),
                            createIfDmDoesNotExist = startDmActionState.value is AsyncAction.Confirming,
                            actionState = startDmActionState,
                        )
                    }
                }
                FeedUserProfileEvents.ClearStartDMState -> {
                    startDmActionState.value = AsyncAction.Uninitialized
                }
                is FeedUserProfileEvents.LoadFeedMedia -> {
                    coroutineScope.loadFeedMediaPreview(event.mediaId, feedMediaPreviewActionState)
                }
                FeedUserProfileEvents.DismissFeedMedia -> feedMediaPreviewActionState.value = AsyncAction.Uninitialized
            }
        }

        LaunchedEffect(Unit) {
            userFeedsMediaMap.putAll(FeedItemMediaCache.getCachedFeedItemMediaMap())
            userFeedsLinkMetaDataMap.putAll(FeedItemMediaCache.getCachedFeedItemLinkMetaDataMap())
            if (userProfileFlow.value == null) {
                coroutineScope.fetchUserProfileById(userId, userProfileFlow, userProfileFollowingFlow, userFeedsFlow, genericActionState)
            } else {
                coroutineScope.fetchUserProfileData(userProfileFlow, userProfileFollowingFlow, userFeedsFlow)
            }
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
            isMyOwnProfile = client.sessionId.extractedDisplayName == userProfileFlow.value?.userId,
            dmRoomId = dmRoomId,
            startDmActionState = startDmActionState.value,
            feedMediaPreviewState = feedMediaPreviewActionState.value,
            eventSink = ::handleEvents,
            genericActionState = genericActionState.value
        )
    }

    private fun CoroutineScope.fetchUserProfileData(
        userProfileFlow: MutableState<FeedUserProfileView?>,
        userProfileFollowingFlow: MutableState<Boolean?>,
        userFeedsFlow: MutableState<List<ZeroFeed>>
    ) = launch {
        val userProfile = userProfileFlow.value ?: return@launch
        val results = awaitAll(
            async { client.fetchFeedUserProfile(userProfile.primaryZIdOrWalletAddress ?: userProfile.userId) },
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
        userProfileFlow: MutableState<FeedUserProfileView?>,
        userProfileFollowingFlow: MutableState<Boolean?>,
        userFeedsFlow: MutableState<List<ZeroFeed>>,
        genericActionState: MutableState<AsyncAction<Unit>>,
    ) = launch {
        val userProfile = userProfileFlow.value ?: return@launch
        genericActionState.value = AsyncAction.Loading
        val isFollowing = userProfileFollowingFlow.value ?: false
        val call = if (isFollowing) {
            client.unFollowUser(userProfile.userId)
        } else {
            client.followUser(userProfile.userId)
        }
        call.onSuccess {
            genericActionState.value = AsyncAction.Success(Unit)
            fetchUserProfileData(userProfileFlow, userProfileFollowingFlow, userFeedsFlow)
        }.onFailure { error ->
            genericActionState.value = AsyncAction.Failure(error)
        }
    }

    private fun CoroutineScope.addMeowToFeed(feed: ZeroFeed, meowCount: Int, userFeedsFlow: MutableState<List<ZeroFeed>>) = launch {
        val result = client.addMeowToFeed(feed, meowCount)
        result.getOrNull()?.let { updatedFeed ->
            val allFeeds = userFeedsFlow.value.toMutableList()
            allFeeds.indexOfFirst { it.id == updatedFeed.id }
                .takeIf { it >= 0 }
                ?.let { index ->
                    allFeeds[index] = updatedFeed
                    _userFeeds.clear()
                    _userFeeds.addAll(allFeeds)
                    userFeedsFlow.value = allFeeds
                }
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

    private fun CoroutineScope.fetchUserProfileById(
        userId: UserId,
        userProfileFlow: MutableState<FeedUserProfileView?>,
        userProfileFollowingFlow: MutableState<Boolean?>,
        userFeedsFlow: MutableState<List<ZeroFeed>>,
        genericActionState: MutableState<AsyncAction<Unit>>,
    ) = launch {
        genericActionState.value = AsyncAction.Loading
        client.getProfile(userId)
            .onSuccess { matrixProfile ->
                genericActionState.value = AsyncAction.Success(Unit)
                val key = matrixProfile.primaryZIdOrWalletAddress
                if (key != null) {
                    client.fetchFeedUserProfile(key)
                        .onSuccess { zeroProfile ->
                            userProfileFlow.value = zeroProfile
                            fetchUserProfileData(userProfileFlow, userProfileFollowingFlow, userFeedsFlow)
                        }
                        .onFailure { error ->
                            genericActionState.value = AsyncAction.Failure(error)
                        }
                } else {
                    userProfileFlow.value = matrixProfile.toZeroProfile()
                }
            }
            .onFailure { error ->
                genericActionState.value = AsyncAction.Failure(error)
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
