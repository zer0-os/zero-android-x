/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.model.HomeScreenChannel
import kotlinx.collections.immutable.toPersistentList

open class ChannelListContentStateProvider : PreviewParameterProvider<ChannelListContentState> {
    override val values: Sequence<ChannelListContentState>
        get() = sequenceOf(
            aChannelListContentState(emptyList()),
            aPlaceholderChannelListContentState(),
            aSkeletonChannelListContentState(),
            anEmptyChannelListContentState()
        )
}

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
