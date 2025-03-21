/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl

import androidx.compose.runtime.Immutable
import io.element.android.features.roomlist.impl.model.HomeScreenChannel
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed interface ChannelListContentState {
    data class Skeleton(val count: Int) : ChannelListContentState
    data object Empty : ChannelListContentState
    data class Channels(
        val channels: ImmutableList<HomeScreenChannel>
    ) : ChannelListContentState
}
