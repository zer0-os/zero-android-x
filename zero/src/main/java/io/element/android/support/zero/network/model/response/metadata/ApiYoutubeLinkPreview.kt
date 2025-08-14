/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiYoutubeLinkPreview(
    val title: String,
    @SerialName("author_name")
    val authorName: String,
    @SerialName("author_url")
    val authorURL: String,
    @SerialName("thumbnail_height")
    val thumbnailHeight: Float,
    @SerialName("thumbnail_width")
    val thumbnailWidth: Float,
    @SerialName("thumbnail_url")
    val thumbnailURL: String
)
