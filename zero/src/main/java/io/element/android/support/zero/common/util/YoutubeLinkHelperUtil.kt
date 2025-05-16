/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.util

object YoutubeLinkHelperUtil {

    fun extractFirstAvailableYoutubeUrl(text: String): String? =
        text.let {
            Regex("""https://(?:www\.)?(?:youtube\.com/watch\?v=|youtu\.be/)[^&\s]+""")
                .find(it)
                ?.value
        }

    fun isUrlAYoutubeLink(url: String): Boolean = extractFirstAvailableYoutubeUrl(url) != null
}
