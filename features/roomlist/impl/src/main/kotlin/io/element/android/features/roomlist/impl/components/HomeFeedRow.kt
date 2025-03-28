/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.roomlist.impl.components

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.CompositeAvatar
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeedAuthor
import io.element.android.libraries.matrix.api.zero.feed.totalMeowCount
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import io.element.android.support.zero.config.ZeroConfig

@Composable
fun HomeFeedRow(
    modifier: Modifier = Modifier,
    feed: ZeroFeed,
    zeroUserRewards: ZeroUserRewards,
    isProfileFeed: Boolean = false,
    onFeedClick: () -> Unit,
    onAddMeowToFeed: (Int) -> Unit,
) {
    val context = LocalContext.current
    val openArweaveLink: () -> Unit = {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, feed.arweaveLink.toUri())
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        CompositeAvatar(
            avatarData = feed.user.avatarData()
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Row {
                Text(
                    text = feed.user.profileSummary.name,
                    style = ElementTheme.typography.fontBodyMdMedium,
                    color = ElementTheme.colors.textPrimary,
                    maxLines = 1
                )
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
            Text(
                text = "$ZERO_CHANNEL_PREFIX${feed.zid}",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = feed.annotatedText(ElementTheme.colors.zeroBrandColor),
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textPrimary
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.weight(1f)) {
                    FeedActionButton(
                        iconResId = R.drawable.ic_post_reply,
                        supportingText = (feed.replies?.count() ?: 0).toString()
                    )
                    Spacer(Modifier.width(60.dp))
                    FeedMeowActionButton(
                        meowCount = feed.totalMeowCount(zeroUserRewards.decimals),
                        highlighted = !feed.meows.isNullOrEmpty(),
                        enabled = !isProfileFeed,
                        onAddMeowToFeed = onAddMeowToFeed
                    )
                }
                FeedActionButton(
                    iconResId = R.drawable.ic_post_arweave,
                    onClick = openArweaveLink
                )
            }
        }
    }
}

@Composable
private fun FeedActionButton(
    @DrawableRes iconResId: Int,
    supportingText: String? = null,
    highlighted: Boolean = false,
    onClick: () -> Unit = {}
) {
    val tint = if (highlighted) ElementTheme.colors.zeroBrandColor
    else ElementTheme.colors.textSecondary
    Row(
        modifier = Modifier.clickable { onClick() },
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

private fun ZeroFeedAuthor.avatarData() = AvatarData(
    id = id,
    name = profileSummary.name,
    url = profileSummary.profileImage,
    size = AvatarSize.RoomDirectoryItem
)

private fun ZeroFeed.annotatedText(highlightColor: Color): AnnotatedString {
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
        }
    }
}

private val ZeroFeed.arweaveLink: String get() = buildString {
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
        onAddMeowToFeed = {}
    )
}
