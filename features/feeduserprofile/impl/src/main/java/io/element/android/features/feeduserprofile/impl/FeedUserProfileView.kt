/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.features.feeduserprofile.impl.components.UserFeedsListView
import io.element.android.features.feeduserprofile.impl.components.UserProfileHeaderView
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.ui.strings.CommonStrings

@Composable
fun FeedUserProfileView(
    modifier: Modifier = Modifier,
    state: FeedUserProfileState,
    onBackClick: () -> Unit = {},
    onUserFeedClick: (feed: ZeroFeed) -> Unit = {}
) {
    Scaffold(
        modifier = modifier
    ) { padding ->
        FeedUserProfile(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            state = state,
            onBackClick = onBackClick,
            onUserFeedClick = onUserFeedClick
        )

        if (state.genericActionState is AsyncData.Loading) {
            ProgressDialog()
        }

        if (state.genericActionState is AsyncData.Failure) {
            ErrorDialog(
                content = state.genericActionState.error.message ?: stringResource(CommonStrings.error_unknown),
                onSubmit = { state.eventSink(FeedUserProfileEvents.HideError) }
            )
        }
    }
}

@Composable
private fun FeedUserProfile(
    modifier: Modifier = Modifier,
    state: FeedUserProfileState,
    onBackClick: () -> Unit = {},
    onUserFeedClick: (feed: ZeroFeed) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        UserProfileHeaderView(
            state = state,
            onBackClick = onBackClick
        )
        UserFeedsListView(
            state = state,
            onUserFeedClick = onUserFeedClick
        )
    }
}

@PreviewsDayNight
@Composable
fun FeedUserProfileViewPreview(
    @PreviewParameter(FeedUserProfileStateProvider::class) state: FeedUserProfileState
) = ElementPreview {
    FeedUserProfileView(state = state)
}
