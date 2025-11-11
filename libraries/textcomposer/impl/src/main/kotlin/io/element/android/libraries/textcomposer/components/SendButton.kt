/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.textcomposer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha15
import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.timeline.item.event.toEventOrTransactionId
import io.element.android.libraries.textcomposer.model.MessageComposerMode

/**
 * Send button for the message composer.
 * Figma: https://www.figma.com/design/G1xy0HDZKJf5TCRFmKb5d5/Compound-Android-Components?node-id=1956-37575&node-type=frame&m=dev
 * Temporary Figma : https://www.figma.com/design/Ni6Ii8YKtmXCKYNE90cC67/Timeline-(new)?node-id=2274-39944&m=dev
 */
@Composable
internal fun SendButton(
    canSendMessage: Boolean,
    onClick: () -> Unit,
    composerMode: MessageComposerMode,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier
            .size(48.dp),
        onClick = onClick,
        enabled = canSendMessage,
    ) {
        val iconVector = when {
            composerMode.isEditing -> CompoundIcons.Check()
            else -> CompoundIcons.SendSolid()
        }
        val iconStartPadding = when {
            composerMode.isEditing -> 0.dp
            else -> 2.dp
        }
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(
                    if (canSendMessage)
                        ElementTheme.colors.zeroBrandColorAlpha15
                    else
                        Color.Transparent
                )
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = iconStartPadding)
                    .align(Alignment.Center),
                imageVector = iconVector,
                // Note: accessibility is managed in TextComposer.
                contentDescription = null,
                // Exception here, we use Color.White instead of ElementTheme.colors.iconOnSolidPrimary
                tint = if (canSendMessage) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.iconDisabled
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun SendButtonPreview() = ElementPreview {
    val normalMode = MessageComposerMode.Normal
    val editMode = MessageComposerMode.Edit(EventId("\$id").toEventOrTransactionId(), "")
    Row {
        SendButton(canSendMessage = true, onClick = {}, composerMode = normalMode)
        SendButton(canSendMessage = false, onClick = {}, composerMode = normalMode)
        SendButton(canSendMessage = true, onClick = {}, composerMode = editMode)
        SendButton(canSendMessage = false, onClick = {}, composerMode = editMode)
    }
}
