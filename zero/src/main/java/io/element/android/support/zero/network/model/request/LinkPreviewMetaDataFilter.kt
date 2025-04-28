/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.request

import io.element.android.support.zero.datastore.converter.AppJson.toJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinkPreviewMetaDataFilter(
    @SerialName("url") val url: String = ""
) {
    override fun toString() = toJson()

    companion object {
        fun newFilter(url: String) = LinkPreviewMetaDataFilter(url).toString()

        fun emptyFilter() = LinkPreviewMetaDataFilter().toString()
    }
}
