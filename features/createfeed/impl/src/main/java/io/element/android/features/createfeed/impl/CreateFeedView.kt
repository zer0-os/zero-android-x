/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight

@Composable
fun CreateFeedView(
    modifier: Modifier = Modifier,
    state: CreateFeedState,
    onBackClick: () -> Unit = {}
) {
}

@PreviewsDayNight
@Composable
fun CreateFeedViewPreview() = ElementPreview {
    CreateFeedView(
        state = CreateFeedState(
            feedText = "",
            eventSink = {}
        )
    )
}
