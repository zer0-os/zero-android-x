/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.placeholderBackground

@Composable
fun HomeFeedPlaceholderRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(AvatarSize.RoomListItem.dp)
                .background(color = ElementTheme.colors.placeholderBackground, shape = CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Row {
                PlaceholderAtom(width = 200.dp, height = 7.dp)
                Spacer(Modifier.width(8.dp))
                PlaceholderAtom(width = 200.dp, height = 7.dp)
            }
            Spacer(Modifier.height(8.dp))
            PlaceholderAtom(width = 200.dp, height = 7.dp)
            Spacer(Modifier.height(16.dp))
            PlaceholderAtom(width = 400.dp, height = 7.dp)
            Spacer(Modifier.height(6.dp))
            PlaceholderAtom(width = 400.dp, height = 7.dp)
            Spacer(Modifier.height(6.dp))
            PlaceholderAtom(width = 100.dp, height = 7.dp)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PlaceholderAtom(width = 24.dp, height = 24.dp)
                PlaceholderAtom(width = 24.dp, height = 24.dp)
                PlaceholderAtom(width = 24.dp, height = 24.dp)
            }
        }
    }
}

@PreviewsDayNight
@Composable
internal fun HomeFeedPlaceholderRowPreview() = ElementPreview {
    HomeFeedPlaceholderRow()
}
