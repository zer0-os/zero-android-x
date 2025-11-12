/*
 * Copyright (c) 2025 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.channel

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.model.HomeScreenChannel
import io.element.android.libraries.matrix.api.core.RoomId
import kotlinx.collections.immutable.ImmutableList

data class ChannelListState(
    val resolvedChannelRoom: RoomId?,
    val contentState: ChannelListContentState,
    val eventSink: (ChannelListEvents) -> Unit
)

@Immutable
sealed interface ChannelListContentState {
    data class Skeleton(val count: Int) : ChannelListContentState
    data object Empty : ChannelListContentState
    data class Channels(
        val channels: ImmutableList<HomeScreenChannel>
    ) : ChannelListContentState
}
