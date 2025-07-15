/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.components.FeedActionButton
import io.element.android.features.home.impl.components.FeedMeowActionButton
import io.element.android.features.home.impl.components.annotatedText
import io.element.android.features.home.impl.components.arweaveLink
import io.element.android.features.home.impl.components.avatarData
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.aspectRatio
import io.element.android.libraries.matrix.api.zero.feed.isVideo
import io.element.android.libraries.matrix.api.zero.feed.totalMeowCount
import io.element.android.libraries.matrix.api.zero.feed.userProfile
import io.element.android.libraries.matrix.api.zero.metadata.aspectRatio
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.ui.component.feed.FeedLinkPreviewView
import io.element.android.support.zero.common.ui.component.feed.FeedMediaImageView
import io.element.android.support.zero.common.ui.component.feed.FeedMediaVideoView

@Composable
fun FeedDetailsCell(
    modifier: Modifier = Modifier,
    feed: ZeroFeed,
    zeroUserRewards: ZeroUserRewards,
    isMyOwnFeed: Boolean = false,
    onAddMeowToFeed: (Int) -> Unit,
    onFeedUserClick: (FeedUserProfileView) -> Unit,
    onMediaTapped: (String) -> Unit,
) {
    val context = LocalContext.current
    val openExternalLink: (Uri) -> Unit = { uri ->
        context.startActivity(
            Intent(Intent.ACTION_VIEW, uri)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            Avatar(
                modifier = Modifier.clickable {
                    onFeedUserClick(feed.userProfile)
                },
                avatarData = feed.user.avatarData(),
                avatarType = AvatarType.User
            )
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Row {
                    Text(
                        modifier = Modifier.clickable {
                            onFeedUserClick(feed.userProfile)
                        },
                        text = feed.user.profileSummary.name,
                        style = ElementTheme.typography.fontBodyMdMedium,
                        color = ElementTheme.colors.textPrimary,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(8.dp))
                    if (!feed.worldZid.isNullOrBlank() && feed.worldZid != feed.zid) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "$ZERO_CHANNEL_PREFIX${feed.worldZid ?: "{world_id_here}"}",
                            style = ElementTheme.typography.fontBodyMdRegular,
                            color = ElementTheme.colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$ZERO_CHANNEL_PREFIX${feed.zid}",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = feed.annotatedText(
                highlightColor = ElementTheme.colors.zeroBrandColor,
                onLinkTapped = { url -> context.openExternalUri(url) }
            ),
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textPrimary
        )
        if (feed.linkMetaData != null) {
            val linkMetaData = feed.linkMetaData!!
            Box(modifier = Modifier
                .padding(vertical = 8.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
            ) {
                val linkPreviewModifier = if (linkMetaData.thumbnail?.aspectRatio != null) {
                    Modifier
                        .aspectRatio(linkMetaData.thumbnail?.aspectRatio!!)
                        .clip(RoundedCornerShape(4.dp))
                } else {
                    Modifier.clip(RoundedCornerShape(4.dp))
                }
                FeedLinkPreviewView(
                    modifier = linkPreviewModifier,
                    thumbnailUrl = linkMetaData.thumbnailUrl.orEmpty(),
                    title = linkMetaData.title,
                    description = linkMetaData.description,
                    onLinkPreviewClick = {
                        openExternalLink(linkMetaData.url.toUri())
                    }
                )
            }
        }
        if (feed.media != null) {
            val media = feed.media!!
            Box(modifier = Modifier
                .padding(vertical = 8.dp)
                .background(Color.Black, RoundedCornerShape(4.dp))
            ) {
                if (media.isVideo) {
                    FeedMediaVideoView(
                        videoUrl = media.url.orEmpty(),
                        onTap = { onMediaTapped(media.id) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    FeedMediaImageView(
                        url = media.url.orEmpty(),
                        onTap = { onMediaTapped(media.id) },
                        modifier = Modifier
                            .aspectRatio(media.aspectRatio)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = feed.completeDateAndTime(),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
            maxLines = 1
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FeedActionButton(
                iconResId = R.drawable.ic_post_reply,
                supportingText = (feed.replies?.count() ?: 0).toString(),
                enabled = false
            )
            FeedMeowActionButton(
                meowCount = feed.totalMeowCount(zeroUserRewards.decimals),
                highlighted = !feed.meows.isNullOrEmpty(),
                enabled = !isMyOwnFeed,
                onAddMeowToFeed = onAddMeowToFeed
            )
            FeedActionButton(
                iconResId = R.drawable.ic_post_arweave,
                onClick = { openExternalLink(feed.arweaveLink.toUri()) }
            )
        }
    }
}

@PreviewsDayNight
@Composable
internal fun FeedDetailsCellPreview() = ElementPreview {
    FeedDetailsCell(
        feed = ZeroFeed.placeholder,
        zeroUserRewards = ZeroUserRewards.empty(),
        onAddMeowToFeed = {},
        onFeedUserClick = {},
        onMediaTapped = {},
    )
}
