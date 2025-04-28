/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import io.element.android.support.zero.network.model.response.ApiLinkPreview

fun ApiLinkPreview.toModel() = ZeroLinkPreview(
    url = url,
    title = title,
    description = description,
    thumbnailUrl = thumbnailURL
)
