/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.common.MatrixSessionCommon
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HomeScreenChannel(
    val channelFullName: String
) {
    val displayTitle: String = channelFullName
        .replace(ZERO_CHANNEL_PREFIX, "")
        .split(".")
        .firstOrNull()
        .orEmpty()

    var notificationsCount = 0

    companion object {
        val placeHolder: HomeScreenChannel = HomeScreenChannel(
            "0://dummychannel.xyz"
        )
    }
}

fun HomeScreenChannel.channelId(): String? {
    return displayTitle.let {
        "#${it}:${MatrixSessionCommon.getHomeServerPostfix()}"
    }
}

fun HomeScreenChannel.toRoomSummary() = RoomListRoomSummary(
    id = channelFullName,
    roomId = RoomId("!room_id:domain"), // Placeholder for channel as we won't be using it for channels.
    name = buildString {
        append(ZERO_CHANNEL_PREFIX)
        append(displayTitle)
    },
    numberOfUnreadMessages = 0,
    numberOfUnreadMentions = 0,
    numberOfUnreadNotifications = 0,
    isMarkedUnread = false,
    timestamp = null,
    avatarData = AvatarData(id = channelFullName, name = displayTitle, url = null, size = AvatarSize.RoomListItem),
    userDefinedNotificationMode = null,
    hasRoomCall = false,
    isDirect = false,
    isDm = false,
    isFavorite = false,
    inviteSender = null,
    displayType = RoomSummaryDisplayType.ROOM,
    canonicalAlias = null,
    heroes = persistentListOf(),
    isTombstoned = false,
    isSpace = false,
    isEncrypted = false,
    latestEvent = null,
    isDiscoverable = false,
    isDeadRoom = false,
    deadRoomUserId = null
)
