/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui.component.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.theme.components.Text

@Composable
fun FeedLinkPreviewView(
    modifier: Modifier = Modifier,
    thumbnailUrl: String,
    isYoutubeMetaData: Boolean = true,
    title: String?,
    description: String?,
    onLinkPreviewClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()
        .clickable { onLinkPreviewClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                modifier = modifier,
                model = thumbnailUrl,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                contentDescription = null,
            )
            if (isYoutubeMetaData) {
                Image(
                    modifier = Modifier
                        .background(Color.Black, RoundedCornerShape(4.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    imageVector = CompoundIcons.PlaySolid(),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (description != null) {
                Text(
                    text = description,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
