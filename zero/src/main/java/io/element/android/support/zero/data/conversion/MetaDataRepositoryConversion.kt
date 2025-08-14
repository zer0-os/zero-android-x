/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreviewThumbnail
import io.element.android.support.zero.network.model.response.metadata.ApiLinkPreview
import io.element.android.support.zero.network.model.response.metadata.ApiLinkPreviewThumbnail
import io.element.android.support.zero.network.model.response.metadata.ApiYoutubeLinkPreview

fun ApiLinkPreview.toModel() = ZeroLinkPreview(
    url = url,
    title = title,
    description = description,
    author = authorName,
    thumbnail = thumbnail?.toModel(),
)

fun ApiLinkPreviewThumbnail.toModel() = ZeroLinkPreviewThumbnail(
    url = url,
    width = width,
    height = height
)

fun ApiYoutubeLinkPreview.toModel(url: String) = ZeroLinkPreview(
    url = url,
    title = title,
    description = "$authorName $authorURL",
    author = authorName,
    thumbnail = ZeroLinkPreviewThumbnail(
        url = thumbnailURL,
        width = thumbnailWidth,
        height = thumbnailHeight
    )
)
