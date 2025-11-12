/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.features.home.impl.model.channelId
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.architecture.Presenter
import io.element.android.libraries.matrix.api.MatrixClient
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.core.toRoomIdOrAlias
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.support.zero.common.extension.withScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.jvm.optionals.getOrNull

@Inject
class ChannelListPresenter(
    private val client: MatrixClient,
) : Presenter<ChannelListState> {

    private val channelRoomMap: MutableMap<String, RoomSummary> = mutableMapOf()

    @Composable
    override fun present(): ChannelListState {
        val coroutineState = rememberCoroutineScope()

        val genericActionState: MutableState<AsyncAction<Unit>> = remember { mutableStateOf(AsyncAction.Uninitialized) }
        val resolvedChannelRoomId: MutableState<RoomId?> = remember { mutableStateOf(null) }

        LaunchedEffect(Unit) {
            client.getUserZIds()
        }

        fun handleEvent(event: ChannelListEvents) {
            when (event) {
                is ChannelListEvents.OpenChannel -> coroutineState.openChannel(event.channel, resolvedChannelRoomId, genericActionState)
                ChannelListEvents.ChannelRoomOpened -> resolvedChannelRoomId.value = null
            }
        }

        val channelContentState = channelListContentState()
        createChannelRoomMap(
            (channelContentState as? ChannelListContentState.Channels)?.channels.orEmpty()
        )

        return ChannelListState(
            resolvedChannelRoom = resolvedChannelRoomId.value,
            contentState = channelListContentState(),
            eventSink = ::handleEvent
        )
    }

    @Composable
    private fun channelListContentState(): ChannelListContentState {
        val homeChannelsState by produceState(initialValue = AsyncData.Loading()) {
            client.userZIds.collect {
                value = AsyncData.Success(it)
            }
        }
        val showEmpty by remember {
            derivedStateOf {
                (homeChannelsState as? AsyncData.Success)?.data?.isEmpty() == true
            }
        }
        val showSkeleton by remember {
            derivedStateOf {
                homeChannelsState is AsyncData.Loading
            }
        }
        return when {
            showEmpty -> ChannelListContentState.Empty
            showSkeleton -> ChannelListContentState.Skeleton(20)
            else -> {
                val mappedChannels = homeChannelsState.dataOrNull()
                    .orEmpty()
                    .sorted()
                    .map { HomeScreenChannel(channelFullName = it) }
                    .distinctBy { it.channelId() }
                    .toPersistentList()
                ChannelListContentState.Channels(mappedChannels)
            }
        }
    }

    private fun createChannelRoomMap(channels: List<HomeScreenChannel>) = withScope(Dispatchers.IO) {
        for (channel in channels) {
            channel.channelId()?.let { channelId ->
                val roomSummary = client.getRoomSummaryFlow(RoomAlias(channelId).toRoomIdOrAlias())
                    .firstOrNull()
                    ?.getOrNull()
                roomSummary?.let { summary ->
                    channel.notificationsCount = summary.info.numUnreadMessages.toInt()
                    channelRoomMap.put(channelId, summary)
                }
            }
        }
    }

    private fun CoroutineScope.openChannel(
        channel: HomeScreenChannel,
        resolvedChannelRoomId: MutableState<RoomId?>,
        genericActionState: MutableState<AsyncAction<Unit>>
    ) = launch {
        genericActionState.value = AsyncAction.Uninitialized
        val channelId = channel.channelId() ?: return@launch
        channelRoomMap[channelId]?.let { roomSummary ->
            resolvedChannelRoomId.value = roomSummary.roomId
            return@launch
        }
        genericActionState.value = AsyncAction.Loading
        val channelRoomId = client.resolveRoomAlias(RoomAlias(channelId))
            .getOrNull()?.getOrNull()?.roomId
        if (channelRoomId != null) {
            resolvedChannelRoomId.value = channelRoomId
            genericActionState.value = AsyncAction.Success(Unit)
        } else {
            client.joinZeroChannel(channelId)
                .onSuccess { roomId ->
                    roomId?.let {
                        genericActionState.value = AsyncAction.Success(Unit)
                        resolvedChannelRoomId.value = RoomId(roomId)
                    } ?: run {
                        genericActionState.value = AsyncAction.Failure(Throwable("RoomId not found"))
                    }
                }
                .onFailure { failure ->
                    genericActionState.value = AsyncAction.Failure(failure)
                }
        }
    }
}
