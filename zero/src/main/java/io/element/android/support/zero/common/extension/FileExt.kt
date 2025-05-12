/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.extension

import android.webkit.MimeTypeMap
import java.io.File
import java.util.Locale

fun File.mimeType(): String {
    val extension = this.extension.lowercase(Locale.getDefault())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/png"
}
