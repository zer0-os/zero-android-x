/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.JoinChannelRequest
import io.element.android.support.zero.network.model.response.ApiJoinChannel
import retrofit2.http.Body
import retrofit2.http.POST

interface ZeroChannelService {

    @POST(value = "matrix/room/join")
    suspend fun joinChannel(
        @Body request: JoinChannelRequest
    ): ApiJoinChannel
}
