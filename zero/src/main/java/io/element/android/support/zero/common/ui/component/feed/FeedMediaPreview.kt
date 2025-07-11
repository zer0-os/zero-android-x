/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.common.ui.component.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.aspectRatio
import io.element.android.libraries.matrix.api.zero.feed.isVideo

@Composable
fun FeedMediaPreview(
    mediaState: AsyncAction<FeedMedia>,
    onDismiss: () -> Unit = {}
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            when (mediaState) {
                AsyncAction.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AsyncAction.Success -> {
                    val media = mediaState.data
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            modifier = Modifier.align(Alignment.TopStart)
                                .padding(8.dp),
                            onClick = onDismiss
                        ) {
                            Icon(imageVector = CompoundIcons.ArrowLeft(),
                                contentDescription = null)
                        }

                        if (media.isVideo) {
                            FeedMediaVideoView(
                                videoUrl = media.url.orEmpty(),
                                onTap = { },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .align(Alignment.Center)
                            )
                        } else {
                            FeedMediaImageView(
                                url = media.url.orEmpty(),
                                onTap = { },
                                modifier = Modifier
                                    .aspectRatio(media.aspectRatio)
                                    .clip(RoundedCornerShape(4.dp))
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
                is AsyncAction.Failure -> {
                    ErrorDialog(
                        content = mediaState.error.message ?: "Failed to load media",
                        onSubmit = onDismiss,
                    )
                }
                else -> {}
            }
        }
    }
}
