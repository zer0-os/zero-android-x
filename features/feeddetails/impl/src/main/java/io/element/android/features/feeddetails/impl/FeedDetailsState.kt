/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@Immutable
data class FeedDetailsState(
    val zeroFeed: ZeroFeed
)
