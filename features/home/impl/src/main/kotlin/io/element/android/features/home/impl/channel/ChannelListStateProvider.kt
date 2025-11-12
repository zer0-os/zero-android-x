/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.toPersistentList

open class ChannelListStateProvider : PreviewParameterProvider<ChannelListState> {
    override val values: Sequence<ChannelListState>
        get() = sequenceOf(
            aChannelListState(),
            aChannelListState(channelListContentState = aSkeletonChannelListContentState()),
            aChannelListState(channelListContentState = anEmptyChannelListContentState())
        )
}

internal fun aChannelListState(
    resolvedChannelRoom: RoomId? = null,
    channelListContentState: ChannelListContentState = aPlaceholderChannelListContentState()
) = ChannelListState(
    resolvedChannelRoom = resolvedChannelRoom,
    contentState = channelListContentState,
    eventSink = {}
)

private fun placeholderChannels(): List<HomeScreenChannel> {
    val list = mutableListOf<HomeScreenChannel>()
    for (i in 0..5) {
        list.add(HomeScreenChannel.placeHolder)
    }
    return list
}

internal fun aPlaceholderChannelListContentState() =
    ChannelListContentState.Channels(placeholderChannels().toPersistentList())

internal fun aChannelListContentState(
    channels: List<HomeScreenChannel>
) = ChannelListContentState.Channels(channels.toPersistentList())

internal fun aSkeletonChannelListContentState() = ChannelListContentState.Skeleton(20)

internal fun anEmptyChannelListContentState() = ChannelListContentState.Empty
