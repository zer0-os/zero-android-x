/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
internal fun UserRow(
    avatarData: AvatarData,
    name: String,
    subtext: String?,
    enabled: Boolean = true,
    showProSubscriberBadge: Boolean,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarData = avatarData,
            avatarType = AvatarType.User,
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f),
        ) {
            // Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.clipToBounds(),
                    text = name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (enabled) ElementTheme.colors.textPrimary else ElementTheme.colors.textDisabled,
                    style = ElementTheme.zeroTypography.fontBodyLgRegular,
                )
                AnimatedVisibility(showProSubscriberBadge) {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(16.dp),
                        imageVector = CompoundIcons.Verified(),
                        contentDescription = stringResource(CommonStrings.common_verified),
                        tint = ElementTheme.colors.zeroBrandColor
                    )
                }
            }
            // Id
            subtext?.let {
                Text(
                    text = subtext,
                    color = if (enabled) ElementTheme.colors.textSecondary else ElementTheme.colors.textDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = ElementTheme.zeroTypography.fontBodySmRegular,
                )
            }
        }
        trailingContent?.invoke()
    }
}
