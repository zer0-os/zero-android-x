/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.feed

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeedAuthor
import io.element.android.libraries.matrix.api.zero.feed.aspectRatio
import io.element.android.libraries.matrix.api.zero.feed.isVideo
import io.element.android.libraries.matrix.api.zero.metadata.aspectRatio
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.ui.component.feed.FeedLinkPreviewView
import io.element.android.support.zero.common.ui.component.feed.FeedMediaImageView
import io.element.android.support.zero.common.ui.component.feed.FeedMediaVideoView
import io.element.android.support.zero.config.ZeroConfig

@Composable
fun HomeFeedRow(
    modifier: Modifier = Modifier,
    feed: ZeroFeed,
    zeroUserRewards: ZeroUserRewards,
    isMyOwnFeed: Boolean = false,
    showThreadLine: Boolean = false,
    onFeedClick: () -> Unit,
    onFeedUserClick: () -> Unit,
    onAddMeowToFeed: (Int) -> Unit,
    onMediaTapped: (String) -> Unit,
) {
    val context = LocalContext.current
    val openExternalLink: (Uri) -> Unit = { uri ->
        context.startActivity(
            Intent(Intent.ACTION_VIEW, uri)
        )
    }

    val rowModifier = modifier
        .fillMaxWidth()
        .clickable { onFeedClick() }
        .padding(16.dp)
    Row(
        modifier = if (feed.media != null) {
            rowModifier
        } else rowModifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(
                modifier = Modifier.clickable { onFeedUserClick() },
                avatarData = feed.user.avatarData(),
                avatarType = AvatarType.User
            )
            if (showThreadLine) {
                VerticalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        }
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.clickable { onFeedUserClick() },
                    text = feed.user.profileSummary.name,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    color = ElementTheme.colors.textPrimary,
                    maxLines = 1
                )
                if (feed.userProfileView?.isZeroProSubscriber == true) {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(16.dp),
                        imageVector = CompoundIcons.Verified(),
                        contentDescription = stringResource(CommonStrings.common_verified),
                        tint = ElementTheme.colors.zeroBrandColor
                    )
                }
                Text(
                    text = " • ",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1
                )
                Text(
                    text = feed.updatedAtTimeAgo(),
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
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
            if (feed.zIDOrAddress != null) {
                Text(
                    text = feed.zIDOrAddress!!,
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = feed.annotatedText(
                    highlightColor = ElementTheme.colors.zeroBrandColor,
                    onLinkTapped = { url -> context.openExternalUri(url) }
                ),
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textPrimary,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FeedActionButton(
                    iconResId = R.drawable.ic_post_reply,
                    supportingText = (feed.replies?.count() ?: 0).toString(),
                    onClick = onFeedClick
                )

                FeedMeowActionButton(
                    meowCount = feed.totalMeowCount.toString(),
                    highlighted = feed.isMeowedByMe,
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
}

@Composable
fun FeedActionButton(
    @DrawableRes iconResId: Int,
    supportingText: String? = null,
    highlighted: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val tint = if (highlighted) ElementTheme.colors.zeroBrandColor
    else ElementTheme.colors.textSecondary
    val modifier = if (enabled) Modifier.clickable { onClick() }
    else Modifier
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = tint
        )
        if (supportingText != null) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = supportingText,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = tint
            )
        }
    }
}

fun ZeroFeedAuthor.avatarData() = AvatarData(
    id = id,
    name = profileSummary.name,
    url = profileSummary.profileImage,
    size = AvatarSize.RoomDirectoryItem
)

fun ZeroFeed.annotatedText(
    highlightColor: Color,
    onLinkTapped: (String) -> Unit
): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        // Regex to match @mentions, #hashtags, and links (http, https, www)
        val regex = """(@\w+|#\w+|\b(?:https?://|www\.)\S+)""".toRegex()
        regex.findAll(text).forEach { match ->
            val isLink = match.value.startsWith("http") || match.value.startsWith("www")
            addStyle(
                style = SpanStyle(
                    color = highlightColor,
                    textDecoration = if (isLink) TextDecoration.Underline else TextDecoration.None
                ),
                start = match.range.first,
                end = match.range.last + 1
            )

            // Add URL annotation for links to make them clickable
            if (isLink) {
                val url = if (match.value.startsWith("www")) {
                    "https://${match.value}"
                } else {
                    match.value
                }

                addLink(
                    url = LinkAnnotation.Url(url, linkInteractionListener = { onLinkTapped(url) }),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }
        }
    }
}

val ZeroFeed.arweaveLink: String
    get() = buildString {
        append(ZeroConfig.ARWEAVE_BASE_URL)
        append(arweaveId)
    }

@PreviewsDayNight
@Composable
internal fun HomeFeedRowPreview() = ElementPreview {
    HomeFeedRow(
        feed = ZeroFeed.placeholder,
        zeroUserRewards = ZeroUserRewards.empty(),
        onFeedClick = {},
        onFeedUserClick = {},
        onAddMeowToFeed = {},
        onMediaTapped = {}
    )
}
