/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2022-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.model

import androidx.compose.runtime.Immutable
import io.element.android.features.invite.api.InviteData
import io.element.android.features.roomdirectory.api.RoomDescription
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.matrix.api.core.RoomAlias
import io.element.android.libraries.matrix.api.core.RoomId
import io.element.android.libraries.matrix.api.room.RoomNotificationMode
import io.element.android.libraries.matrix.ui.model.InviteSender
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RoomListRoomSummary(
    val id: String,
    val displayType: RoomSummaryDisplayType,
    val roomId: RoomId,
    val name: String?,
    val canonicalAlias: RoomAlias?,
    val numberOfUnreadMessages: Long,
    val numberOfUnreadMentions: Long,
    val numberOfUnreadNotifications: Long,
    val isMarkedUnread: Boolean,
    val timestamp: String?,
    val latestEvent: LatestEvent,
    val avatarData: AvatarData,
    val userDefinedNotificationMode: RoomNotificationMode?,
    val hasRoomCall: Boolean,
    val isDirect: Boolean,
    val isDm: Boolean,
    val isFavorite: Boolean,
    val inviteSender: InviteSender?,
    val isTombstoned: Boolean,
    val heroes: ImmutableList<AvatarData>,
    val isSpace: Boolean,
    val isEncrypted: Boolean,
    val isDiscoverable: Boolean = false,

    val isDeadRoom: Boolean = false,
    val deadRoomUserId: String? = null
) {
    val isHighlighted = userDefinedNotificationMode != RoomNotificationMode.MUTE &&
        (numberOfUnreadNotifications > 0 || numberOfUnreadMentions > 0) ||
        isMarkedUnread

    val hasNewContent = numberOfUnreadMessages > 0 ||
        numberOfUnreadMentions > 0 ||
        numberOfUnreadNotifications > 0 ||
        isMarkedUnread

    val hasUnreadMentions = numberOfUnreadMentions > 0

    val isAChannel = name?.startsWith(ZERO_CHANNEL_PREFIX) == true

    val isMuted = !isAChannel && userDefinedNotificationMode == RoomNotificationMode.MUTE

    val isPrimary: Boolean
        get() = !isAChannel && !isMuted && isEncrypted

    val isSecondary: Boolean
        get() = !isAChannel && !isMuted && !isEncrypted

    fun toInviteData() = InviteData(
        roomId = roomId,
        roomName = name ?: roomId.value,
        isDm = isDm,
    )
}

fun RoomDescription.toRoomSummary() = RoomListRoomSummary(
    id = roomId.value,
    roomId = roomId,
    name = name,
    numberOfUnreadMessages = 0,
    numberOfUnreadMentions = 0,
    numberOfUnreadNotifications = 0,
    isMarkedUnread = false,
    timestamp = null,
    avatarData = this.avatarData(AvatarSize.RoomListItem),
    userDefinedNotificationMode = null,
    hasRoomCall = false,
    isDirect = false,
    isDm = false,
    isFavorite = false,
    inviteSender = null,
    displayType = RoomSummaryDisplayType.ROOM,
    canonicalAlias = alias,
    heroes = persistentListOf(),
    isTombstoned = false,
    isSpace = false,
    isEncrypted = false,
    isDiscoverable = true,
    latestEvent = LatestEvent.None,
    isDeadRoom = false,
    deadRoomUserId = null
)
