/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

sealed interface CreateFeedEvents {
    data class PostTextChanged(val text: String) : CreateFeedEvents
    data object CreatePost : CreateFeedEvents
    data object SelectMedia : CreateFeedEvents
    data object RemoveMedia : CreateFeedEvents

    data object HideError : CreateFeedEvents
}
