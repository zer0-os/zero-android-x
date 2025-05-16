/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.metadata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroLinkPreview(
    val url: String,
    val title: String?,
    val description: String?,
    val author: String?,
    val thumbnail: ZeroLinkPreviewThumbnail?,
): Parcelable {
    val thumbnailUrl: String?
        get() = thumbnail?.url
}

@Parcelize
data class ZeroLinkPreviewThumbnail(
    val url: String,
    val width: Float,
    val height: Float,
): Parcelable

val ZeroLinkPreviewThumbnail.aspectRatio
    get() = width.div(height)
