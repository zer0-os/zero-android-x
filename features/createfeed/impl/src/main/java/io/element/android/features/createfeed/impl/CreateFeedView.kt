/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.createfeed.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.createfeed.impl.components.FullScreenTextField
import io.element.android.features.roomlist.impl.R
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.FloatingActionButton
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.feed.FeedMediaImageView
import java.io.File

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
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(vertical = 24.dp),
                containerColor = ElementTheme.colors.iconPrimary,
                onClick = {
                    state.eventSink(CreateFeedEvents.SelectMedia)
                }
            ) {
                Icon(
                    imageVector = CompoundIcons.Attachment(),
                    contentDescription = stringResource(id = R.string.screen_roomlist_a11y_create_message),
                    tint = ElementTheme.colors.iconOnSolidPrimary,
                )
            }
        }
    ) { padding ->
        CreateFeedContent(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            state = state
        )

        if (state.genericActionState is AsyncAction.Success) {
            onBackClick()
        }
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
    Box {
        Column {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
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
            if (state.mediaAttachment != null) {
                Row(modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                ) {
                    FeedAttachmentPreview(
                        media = state.mediaAttachment.media,
                        onRemoveMedia = {
                            state.eventSink(CreateFeedEvents.RemoveMedia)
                        }
                    )
                }
            }
        }

        if (state.genericActionState is AsyncAction.Loading) {
            ProgressDialog(text = "Posting...")
        }

        if (state.genericActionState is AsyncAction.Failure) {
            ErrorDialog(
                content = state.genericActionState.error.message ?: stringResource(CommonStrings.error_unknown),
                onSubmit = { state.eventSink(CreateFeedEvents.HideError) }
            )
        }
    }
}

@Composable
fun FeedAttachmentPreview(
    media: File,
    onRemoveMedia: () -> Unit,
) {
    Box {
        FeedMediaImageView(
            file = media,
            modifier = Modifier
                .size(80.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp))
                .align(Alignment.Center)
                .padding(12.dp)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.DarkGray, CircleShape)
                .size(24.dp),
            onClick = onRemoveMedia,
        ) {
            Icon(modifier = Modifier.fillMaxSize(),
                imageVector = CompoundIcons.Close(),
                contentDescription = null)
        }
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
