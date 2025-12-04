/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.roomlist

import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.roomlist.LatestEventValue
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.impl.room.RoomInfoMapper
import io.element.android.libraries.matrix.impl.timeline.item.event.TimelineEventContentMapper
import io.element.android.libraries.matrix.impl.timeline.item.event.map
import io.element.android.support.zero.data.repository.UserRepository
import org.matrix.rustcomponents.sdk.Room
import org.matrix.rustcomponents.sdk.use
import org.matrix.rustcomponents.sdk.LatestEventValue as RustLatestEventValue

class RoomSummaryFactory(
    private val contentMapper: TimelineEventContentMapper = TimelineEventContentMapper(),
    private val roomInfoMapper: RoomInfoMapper = RoomInfoMapper(),
    private val zeroUserRepository: UserRepository?,
) {
    suspend fun create(room: Room): RoomSummary {
        val roomInfo = room.roomInfo().let(roomInfoMapper::map)
        val latestEvent = room.newLatestEvent().use { event ->
            when (event) {
                is RustLatestEventValue.None -> LatestEventValue.None
                is RustLatestEventValue.Local -> {
                    val cachedUserProfile = zeroUserRepository?.getUserFromCache(event.sender)
                    LatestEventValue.Local(
                        timestamp = event.timestamp.toLong(),
                        content = contentMapper.map(event.content),
                        isSending = event.isSending,
                        senderId = UserId(event.sender),
                        senderProfile = event.profile.map(cachedUser = cachedUserProfile),
                    )
                }
                is RustLatestEventValue.Remote -> {
                    val cachedUserProfile = zeroUserRepository?.getUserFromCache(event.sender)
                    LatestEventValue.Remote(
                        timestamp = event.timestamp.toLong(),
                        content = contentMapper.map(event.content),
                        senderId = UserId(event.sender),
                        senderProfile = event.profile.map(cachedUser = cachedUserProfile),
                        isOwn = event.isOwn,
                    )
                }
            }
        }
        return RoomSummary(
            info = roomInfo,
            latestEvent = latestEvent,
        )
    }
}
