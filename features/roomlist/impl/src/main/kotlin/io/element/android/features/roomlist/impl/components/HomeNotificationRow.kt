/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.roomlist.impl.model.RoomListRoomSummary
import io.element.android.features.roomlist.impl.model.RoomListRoomSummaryProvider
import io.element.android.features.roomlist.impl.model.RoomSummaryDisplayType
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.CompositeAvatar
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography

@Composable
fun HomeNotificationRow(
    room: RoomListRoomSummary,
    onClick: (RoomListRoomSummary) -> Unit,
) {
    fun notificationText(): AnnotatedString {
        return buildAnnotatedString {
            val isRoomDM = room.isDm

            when (room.displayType) {
                RoomSummaryDisplayType.INVITE -> {
                    if (isRoomDM) {
                        appendNotificationContent(room.name)
                        append(" invited you to chat.")
                    } else {
                        append("You are invited to join ")
                        appendNotificationContent(room.name)
                        append(".")
                    }
                }
                else -> {
                    appendNotificationContent(room.numberOfUnreadMessages.toString())
                    append(" message")
                    if (room.numberOfUnreadMessages > 1) append("s")
                    append(" in ")
                    appendNotificationContent(room.name)
                    append(".")
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .clickable { onClick(room) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositeAvatar(
            avatarData = room.avatarData.copy(
                size = AvatarSize.RoomDirectoryItem
            ),
            heroes = room.heroes,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = notificationText(),
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary
        )
    }
}

private fun AnnotatedString.Builder.appendNotificationContent(text: String?) {
    withStyle(SpanStyle(color = Color.White)) {
        append(text ?: "")
    }
}

@PreviewsDayNight
@Composable
fun HomeNotificationRowPreview(@PreviewParameter(RoomListRoomSummaryProvider::class) data: RoomListRoomSummary) = ElementPreview {
    HomeNotificationRow(
        room = data,
        onClick = {}
    )
}
