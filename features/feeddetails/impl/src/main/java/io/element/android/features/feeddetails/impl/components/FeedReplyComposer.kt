/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.feeddetails.impl.FeedDetailsEvents
import io.element.android.features.feeddetails.impl.FeedDetailsState
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha15
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.support.zero.common.ui.component.feed.FeedMediaImageView
import java.io.File

@Composable
fun FeedReplyComposer(
    state: FeedDetailsState,
    modifier: Modifier = Modifier,
) {
    val hasPostReplyAttachment = state.postReplyAttachment != null
    val columnBg = if (hasPostReplyAttachment) Color.Black else Color.Transparent

    Column(
        modifier = modifier
            .background(columnBg)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        if (hasPostReplyAttachment) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                FeedReplyComposerAttachment(
                    media = state.postReplyAttachment!!.media,
                    onRemoveMedia = {
                        state.eventSink(FeedDetailsEvents.RemoveMedia)
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                modifier = Modifier
                    .padding(vertical = 12.dp),
                avatarData = state.matrixUser.getAvatarData(size = AvatarSize.UserListItem),
                avatarType = AvatarType.User
            )
            FeedReplyComposerTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                text = state.postReplyText,
                onTextChanged = { text ->
                    state.eventSink(FeedDetailsEvents.PostReplyTextChanged(text))
                },
                onAttachMedia = {
                    state.eventSink(FeedDetailsEvents.SelectMedia)
                }
            )
            FeedReplyComposerActionButton(
                actionIcon = CompoundIcons.SendSolid(),
                enabled = state.canPostReply,
                onClick = {
                    state.eventSink(FeedDetailsEvents.PostReply)
                }
            )
        }
    }
}

@Composable
private fun FeedReplyComposerAttachment(
    media: File,
    onRemoveMedia: () -> Unit,
) {
    Box {
        FeedMediaImageView(
            file = media,
            modifier = Modifier
                .size(75.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.Center)
                .padding(12.dp),
            onTap = {}
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.DarkGray, CircleShape)
                .size(24.dp),
            onClick = onRemoveMedia,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = CompoundIcons.Close(),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun FeedReplyComposerTextField(
    modifier: Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onAttachMedia: () -> Unit,
) {
    Box(
        modifier = modifier.background(
            ElementTheme.colors.bgCanvasDefaultLevel1,
            RoundedCornerShape(12.dp)
        )
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            placeholder = { Text("Post your reply") },
            singleLine = false,
            maxLines = 5,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(onClick = onAttachMedia) {
                    Icon(CompoundIcons.Attachment(), contentDescription = null)
                }
            }
        )
    }
}

@Composable
private fun FeedReplyComposerActionButton(
    actionIcon: ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier.size(48.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                    if (enabled)
                        ElementTheme.colors.zeroBrandColorAlpha15
                    else
                        Color.Transparent
                )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center),
                imageVector = actionIcon,
                contentDescription = null,
                tint = if (enabled) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.iconDisabled
            )
        }
    }
}
