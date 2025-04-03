/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.roomlist.impl.model.HomeScreenChannel
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX

@Composable
fun HomeChannelRow(
    channel: HomeScreenChannel,
    onChannelClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onChannelClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val channelDisplayName = buildAnnotatedString {
            append(ZERO_CHANNEL_PREFIX)
            withStyle(
                if (channel.notificationsCount > 0) {
                    SpanStyle(
                        color = ElementTheme.colors.textPrimary
                    )
                } else {
                    SpanStyle(
                        color = ElementTheme.colors.textSecondary
                    )
                }
            ) {
                append(channel.displayTitle ?: "")
            }
        }
        Text(
            modifier = Modifier.weight(1f),
            text = channelDisplayName,
            style = ElementTheme.zeroTypography.fontBodyLgMedium,
            color = ElementTheme.colors.textSecondary
        )

        if (channel.notificationsCount > 0) {
            Box(
                modifier = Modifier
                    .background(
                        color = ElementTheme.colors.zeroBrandColor,
                        shape = CircleShape
                    )
                    .width(6.dp)
                    .height(6.dp)
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun HomeChannelRowPreview() = ElementPreview {
    HomeChannelRow(
        channel = HomeScreenChannel.placeHolder,
        onChannelClick = {}
    )
}
