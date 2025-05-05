/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.ui.messages.linkpreview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.ui.components.AttachmentThumbnail
import io.element.android.libraries.matrix.ui.components.AttachmentThumbnailInfo
import io.element.android.libraries.matrix.ui.components.AttachmentThumbnailType
import io.element.android.support.zero.common.extension.openExternalUri

@Composable
fun LinkPreviewView(preview: ZeroLinkPreview) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { context.openExternalUri(preview.url) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val thumbnailModifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(4.dp))

        if (preview.thumbnailUrl.isNullOrBlank()) {
            AttachmentThumbnail(
                info = AttachmentThumbnailInfo(type = AttachmentThumbnailType.Link),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = thumbnailModifier
            )
        } else {
            AsyncImage(
                model = preview.thumbnailUrl,
                contentDescription = null,
                modifier = thumbnailModifier
            )
        }
        Column(modifier = Modifier.padding(8.dp)) {
            if (preview.title != null) {
                Text(
                    text = preview.title!!,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = preview.url,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun LinkPreviewViewPreview() = ElementPreview {
    val dummyPreview = ZeroLinkPreview(
        url = "https://dummyURL.com",
        title = "Dummy link preview title",
        description = null,
        thumbnailUrl = null
    )
    LinkPreviewView(dummyPreview)
}
