/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@Composable
fun FeedDetailsView(
    modifier: Modifier = Modifier,
    state: FeedDetailsState,
    onBackClick: () -> Unit = {}
) {
}

@PreviewsDayNight
@Composable
fun FeedDetailsViewPreview() = ElementPreview {
    FeedDetailsView(state = FeedDetailsState(ZeroFeed.placeholder))
}
