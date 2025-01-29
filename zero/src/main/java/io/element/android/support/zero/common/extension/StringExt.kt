/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.support.zero.common.extension

/**
 * Ensure that the string does not contain any new line characters, which can happen when pasting values.
 */
fun String.sanitize(): String {
    return replace("\n", "")
        .removeDuplicateWhiteSpaces()
}

private fun String.removeDuplicateWhiteSpaces(): String {
    return this.replace("\\s+".toRegex(), " ")
}
