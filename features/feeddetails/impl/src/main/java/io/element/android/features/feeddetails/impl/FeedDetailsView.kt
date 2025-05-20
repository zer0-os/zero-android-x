/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.feeddetails.impl.components.FeedDetailsWithCommentsView
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed

@Composable
fun FeedDetailsView(
    modifier: Modifier = Modifier,
    state: FeedDetailsState,
    onBackClick: () -> Unit = {},
    onFeedReplyClick: (reply: ZeroFeed) -> Unit = {},
    onFeedUserClick: (user: FeedUserProfileView) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            FeedDetailsTopBar(goBack = onBackClick)
        },
    ) { padding ->
        FeedDetailsWithCommentsView(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            state = state,
            onReplyClick = onFeedReplyClick,
            onFeedUserClick = onFeedUserClick,
            onAddMeowToFeed = { feed, meowCount ->
                state.eventSink(FeedDetailsEvents.AddMeowToFeed(feed, meowCount))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedDetailsTopBar(
    goBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("Post") },
        navigationIcon = { BackButton(onClick = goBack) },
    )
}

@PreviewsDayNight
@Composable
fun FeedDetailsViewPreview(
    @PreviewParameter(FeedDetailsStateProvider::class) state: FeedDetailsState
) = ElementPreview {
    FeedDetailsView(
        state = state
    )
}
