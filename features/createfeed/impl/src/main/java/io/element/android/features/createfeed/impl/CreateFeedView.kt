/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.features.createfeed.impl.components.FullScreenTextField
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.matrix.ui.model.getAvatarData

@Composable
fun CreateFeedView(
    modifier: Modifier = Modifier,
    state: CreateFeedState,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CreateFeedTopBar(
                state = state,
                goBack = onBackClick
            )
        },
    ) { padding ->
        CreateFeedContent(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            state = state
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateFeedTopBar(
    state: CreateFeedState,
    goBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = { Text("New Post") },
        navigationIcon = { BackButton(onClick = goBack) },
        actions = {
            TextButton(
                text = "Post",
                onClick = {
                    state.eventSink(CreateFeedEvents.CreatePost)
                },
                enabled = state.canSendPost,
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            actionIconContentColor = Color.White
        )
    )
}

@Composable
private fun CreateFeedContent(
    modifier: Modifier = Modifier,
    state: CreateFeedState
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Avatar(
            modifier = Modifier
                .padding(vertical = 12.dp),
            avatarData = state.matrixUser.getAvatarData(size = AvatarSize.UserListItem),
        )
        FullScreenTextField(
            text = state.feedText,
            placeholderText = "What's happening...",
            onTextChange = { text ->
                state.eventSink(CreateFeedEvents.PostTextChanged(text))
            }
        )
    }
}

@PreviewsDayNight
@Composable
fun CreateFeedViewPreview(
    @PreviewParameter(CreateFeedStateProvider::class) state: CreateFeedState
) = ElementPreview {
    CreateFeedView(
        state = state
    )
}
