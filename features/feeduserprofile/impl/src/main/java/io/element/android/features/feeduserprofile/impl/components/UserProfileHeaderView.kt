/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeduserprofile.impl.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.feeduserprofile.impl.FeedUserProfileEvents
import io.element.android.features.feeduserprofile.impl.FeedUserProfileState
import io.element.android.features.feeduserprofile.impl.FeedUserProfileStateProvider
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.CompositeAvatar
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Button
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.zIdOrWalletAddressDisplay
import io.element.android.support.zero.R

@Composable
fun UserProfileHeaderView(
    state: FeedUserProfileState,
    onBackClick: () -> Unit = {},
) {
    if (state.userProfile != null) {
        Column {
            Box {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(id = R.drawable.img_zero_account_backup_header),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                BackButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(ElementTheme.colors.bgCanvasDefaultLevel1, CircleShape),
                    onClick = onBackClick
                )
            }
            Column(
                modifier = Modifier
                    .offset(y = (-48).dp)
                    .padding(horizontal = 24.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CompositeAvatar(avatarData = state.userProfile.avatarData())
                    IconButton(
                        modifier = Modifier
                            .offset(y = 16.dp)
                            .background(ElementTheme.colors.bgCanvasDefaultLevel1, CircleShape)
                            .size(64.dp),
                        onClick = {
                            state.eventSink(FeedUserProfileEvents.StartDM)
                        }
                    ) {
                        Icon(CompoundIcons.Chat(),
                            contentDescription = "StartDMAction",
                            tint = ElementTheme.colors.zeroBrandColor)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.userProfile.firstName,
                            style = ElementTheme.typography.fontHeadingSmMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subHeadingText = state.userProfile.zIdOrWalletAddressDisplay
                        if (subHeadingText != null) {
                            Text(
                                modifier = Modifier.padding(top = 2.dp),
                                text = subHeadingText,
                                style = ElementTheme.typography.fontBodyLgRegular,
                                color = ElementTheme.colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    if (state.shouldShowFollowButton) {
                        val buttonText = if (state.isUserFollowed == true) "Unfollow" else "Follow"
                        Button(
                            text = buttonText,
                            onClick = {
                                state.eventSink(FeedUserProfileEvents.ToggleFollowUser)
                            }
                        )
                    }
                }
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (state.userProfile.followersCount != null) {
                        UserFollowInfoText(
                            count = state.userProfile.followersCount!!,
                            text = "Followers"
                        )
                    }
                    if (state.userProfile.followingCount != null) {
                        UserFollowInfoText(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            count = state.userProfile.followingCount!!,
                            text = "Following"
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.offset(y = (-48).dp))
        }
    }
}

@Composable
fun UserFollowInfoText(
    modifier: Modifier = Modifier,
    count: String,
    text: String
) {
    Row(modifier = modifier) {
        Text(
            text = count,
            style = ElementTheme.typography.fontBodyLgMedium,
            color = ElementTheme.colors.textPrimary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = text,
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary
        )
    }
}

private fun FeedUserProfileView.avatarData() = AvatarData(
    id = userId,
    name = firstName,
    url = profileImage,
    size = AvatarSize.UserHeader
)

@PreviewsDayNight
@Composable
private fun UserProfileHeaderViewPreview(
    @PreviewParameter(FeedUserProfileStateProvider::class) state: FeedUserProfileState
) = ElementPreview {
    UserProfileHeaderView(
        state = state
    )
}
