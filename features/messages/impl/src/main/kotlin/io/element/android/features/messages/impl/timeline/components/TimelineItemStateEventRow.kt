/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.messages.impl.timeline.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.element.android.features.messages.impl.timeline.TimelineEvent
import io.element.android.features.messages.impl.timeline.aTimelineItemEvent
import io.element.android.features.messages.impl.timeline.components.event.TimelineItemEventContentView
import io.element.android.features.messages.impl.timeline.components.receipt.ReadReceiptViewState
import io.element.android.features.messages.impl.timeline.components.receipt.TimelineItemReadReceiptView
import io.element.android.features.messages.impl.timeline.components.receipt.aReadReceiptData
import io.element.android.features.messages.impl.timeline.model.TimelineItem
import io.element.android.features.messages.impl.timeline.model.TimelineItemGroupPosition
import io.element.android.features.messages.impl.timeline.model.TimelineItemReadReceipts
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemEventContent
import io.element.android.features.messages.impl.timeline.model.event.TimelineItemStateEventContent
import io.element.android.features.messages.impl.timeline.model.event.aTimelineItemStateEventContent
import io.element.android.features.messages.impl.timeline.util.defaultTimelineContentPadding
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.room.RoomMember
import kotlinx.collections.immutable.persistentListOf

@Composable
fun TimelineItemStateEventRow(
    event: TimelineItem.Event,
    roomMembers: List<RoomMember>,
    renderReadReceipts: Boolean,
    isLastOutgoingMessage: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onReadReceiptsClick: (event: TimelineItem.Event) -> Unit,
    eventSink: (TimelineEvent.TimelineItemEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 2.dp)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            MessageStateEventContainer(
                interactionSource = interactionSource,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = Modifier
                    .zIndex(-1f)
                    .widthIn(max = 320.dp)
            ) {
                TimelineItemEventContentView(
                    content = event.content.updatedContent(roomMembers),
                    isMyMessage = false,
                    isRoomEncrypted = false,
                    onLinkClick = {},
                    onLinkLongClick = {},
                    hideMediaContent = false,
                    onShowContentClick = {},
                    eventSink = eventSink,
                    onContentClick = null,
                    onLongClick = null,
                    modifier = Modifier.defaultTimelineContentPadding()
                )
            }
        }
        TimelineItemReadReceiptView(
            state = ReadReceiptViewState(
                sendState = event.localSendState,
                isLastOutgoingMessage = isLastOutgoingMessage,
                receipts = event.readReceiptState.receipts,
            ),
            renderReadReceipts = renderReadReceipts,
            onReadReceiptsClick = { onReadReceiptsClick(event) },
        )
    }
}

private fun TimelineItemEventContent.updatedContent(roomMembers: List<RoomMember>): TimelineItemEventContent {
    return (this as? TimelineItemStateEventContent)?.let { event ->
        val regex = Regex("@[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}:[\\w.-]+")
        val text = regex.replace(event.body) {
            val matchedUserId = it.value
            val user = roomMembers.firstOrNull { member ->
                member.userId.value == matchedUserId
            }
            (user?.displayName ?: matchedUserId).trim()
        }
        event.copy(body = text)
    } ?: this
}

@PreviewsDayNight
@Composable
internal fun TimelineItemStateEventRowPreview() = ElementPreview {
    TimelineItemStateEventRow(
        event = aTimelineItemEvent(
            isMine = false,
            content = aTimelineItemStateEventContent(),
            groupPosition = TimelineItemGroupPosition.None,
            readReceiptState = TimelineItemReadReceipts(
                receipts = persistentListOf(aReadReceiptData(0)),
            )
        ),
        roomMembers = emptyList(),
        renderReadReceipts = true,
        isLastOutgoingMessage = false,
        onClick = {},
        onLongClick = {},
        onReadReceiptsClick = {},
        eventSink = {}
    )
}
