/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.JoinChannelRequest
import io.element.android.support.zero.network.service.ZeroChannelService

class ChannelRepositoryImpl(
    private val zeroChannelService: ZeroChannelService
): ChannelRepository {

    override suspend fun joinChannel(channelId: String): String? {
        return runCatching {
            val result = zeroChannelService.joinChannel(JoinChannelRequest.newRequest(channelId))
            result.roomId
        }.getOrDefault(null)
    }
}
