/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.designsystem.components.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementThemedPreview
import io.element.android.libraries.designsystem.preview.PreviewGroup
import io.element.android.libraries.designsystem.utils.CommonDrawables
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Avatar(
    avatarData: AvatarData,
    avatarType: AvatarType,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    // If not null, will be used instead of the size from avatarData
    forcedAvatarSize: Dp? = null,
    // If true, will show initials even if avatarData.url is not null
    hideImage: Boolean = false,
) {
    val commonModifier = modifier
        .size(forcedAvatarSize ?: avatarData.size.dp)
        .clip(CircleShape)
    if (avatarData.url.isNullOrBlank() || hideImage) {
        ZeroPlaceholderImage(
            avatarData = avatarData,
            forcedAvatarSize = forcedAvatarSize,
            modifier = commonModifier,
        )
    } else {
        ImageAvatar(
            avatarData = avatarData,
            forcedAvatarSize = forcedAvatarSize,
            modifier = commonModifier,
            contentDescription = contentDescription,
        )
    }
}

@Composable
private fun ImageAvatar(
    avatarData: AvatarData,
    forcedAvatarSize: Dp?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    SubcomposeAsyncImage(
        model = avatarData,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    ) {
        val collectedState by painter.state.collectAsState()
        when (val state = collectedState) {
            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
            is AsyncImagePainter.State.Error -> {
                /*SideEffect {
                    Timber.e(state.result.throwable, "Error loading avatar $state\n${state.result}")
                }*/
                ZeroPlaceholderImage(
                        avatarData = avatarData,
                        forcedAvatarSize = forcedAvatarSize,
                )
            }
            else -> ZeroPlaceholderImage(
                    avatarData = avatarData,
                    forcedAvatarSize = forcedAvatarSize,
            )
        }
    }
}

@Composable
private fun ZeroPlaceholderImage(
    avatarData: AvatarData,
    forcedAvatarSize: Dp?,
    modifier: Modifier = Modifier,
) {
    val size = forcedAvatarSize ?: avatarData.size.dp
    Image(
        painter = painterResource(id = R.drawable.ic_zero_avatar_default),
        contentDescription = "zero_placeholder_image",
        modifier = modifier.size(size)
            .background(Color(0xFF1A1B1F))
            .padding(8.dp)
    )
}

@Preview(group = PreviewGroup.Avatars)
@Composable
internal fun AvatarPreview() = ElementThemedPreview(
    drawableFallbackForImages = CommonDrawables.sample_background,
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        listOf(
            anAvatarData(size = AvatarSize.UserListItem),
            anAvatarData(size = AvatarSize.UserListItem, name = null),
            anAvatarData(size = AvatarSize.UserListItem, url = "aUrl"),
        ).forEach { avatarData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.User,
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(isTombstoned = false),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(
                        heroes = persistentListOf(
                            anAvatarData("@carol:server.org", "Carol", size = AvatarSize.UserListItem),
                            anAvatarData("@david:server.org", "David", size = AvatarSize.UserListItem),
                            anAvatarData("@eve:server.org", "Eve", size = AvatarSize.UserListItem),
                            anAvatarData("@justin:server.org", "Justin", size = AvatarSize.UserListItem),
                        )
                    )
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Room(isTombstoned = true),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Space(isTombstoned = false),
                )
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.Space(isTombstoned = true),
                )
            }
        }
    }
}
