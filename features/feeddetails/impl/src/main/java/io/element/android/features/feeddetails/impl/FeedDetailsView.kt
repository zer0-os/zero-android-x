/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.feeddetails.impl.components.FeedDetailsWithReplies
import io.element.android.features.feeddetails.impl.components.FeedReplyComposer
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.feed.FeedMediaPreview

@Composable
fun FeedDetailsView(
    modifier: Modifier = Modifier,
    state: FeedDetailsState,
    onBackClick: () -> Unit = {},
    onFeedReplyClick: (reply: ZeroFeed) -> Unit = {},
    onFeedUserClick: (user: FeedUserProfileView) -> Unit = {},
) {
    val showFeedMediaPreview by remember(state.feedMediaPreviewState) {
        mutableStateOf(state.feedMediaPreviewState != AsyncAction.Uninitialized)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FeedDetailsTopBar(goBack = onBackClick)
        },
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
        ) {
            FeedDetailsWithReplies(
                modifier = Modifier.weight(1f),
                state = state,
                onReplyClick = onFeedReplyClick,
                onFeedUserClick = onFeedUserClick,
                onAddMeowToFeed = { feed, meowCount ->
                    state.eventSink(FeedDetailsEvents.AddMeowToFeed(feed, meowCount))
                },
                onLoadFeedMedia = { mediaId ->
                    state.eventSink(FeedDetailsEvents.LoadFeedMedia(mediaId))
                }
            )
            FeedReplyComposer(state)
        }

        if (state.genericActionState is AsyncAction.Loading) {
            ProgressDialog(text = "Posting...")
        }

        if (state.genericActionState is AsyncAction.Failure) {
            ErrorDialog(
                content = state.genericActionState.error.message ?: stringResource(CommonStrings.error_unknown),
                onSubmit = { state.eventSink(FeedDetailsEvents.HideError) }
            )
        }
    }

    if (showFeedMediaPreview) {
        FeedMediaPreview(state.feedMediaPreviewState, onDismiss = {
            state.eventSink(FeedDetailsEvents.DismissFeedMedia)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedDetailsTopBar(
    goBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("Post") },
        navigationIcon = { BackButton(onClick = goBack) }
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
