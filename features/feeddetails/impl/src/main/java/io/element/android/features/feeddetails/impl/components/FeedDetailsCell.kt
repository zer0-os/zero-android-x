/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.feeddetails.impl.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.roomlist.impl.components.FeedActionButton
import io.element.android.features.roomlist.impl.components.FeedMeowActionButton
import io.element.android.features.roomlist.impl.components.annotatedText
import io.element.android.features.roomlist.impl.components.arweaveLink
import io.element.android.features.roomlist.impl.components.avatarData
import io.element.android.libraries.designsystem.components.avatar.CompositeAvatar
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.totalMeowCount
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX

@Composable
fun FeedDetailsCell(
    modifier: Modifier = Modifier,
    feed: ZeroFeed,
    zeroUserRewards: ZeroUserRewards,
    isMyOwnFeed: Boolean = false,
    onAddMeowToFeed: (Int) -> Unit,
) {
    val context = LocalContext.current
    val openArweaveLink: () -> Unit = {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, feed.arweaveLink.toUri())
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row {
            CompositeAvatar(avatarData = feed.user.avatarData())
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
            text = feed.annotatedText(ElementTheme.colors.zeroBrandColor),
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textPrimary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = feed.completeDateAndTime(),
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
            maxLines = 1
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.weight(1f)) {
                FeedActionButton(
                    iconResId = R.drawable.ic_post_reply,
                    supportingText = (feed.replies?.count() ?: 0).toString(),
                    enabled = false
                )
                Spacer(Modifier.width(60.dp))
                FeedMeowActionButton(
                    meowCount = feed.totalMeowCount(zeroUserRewards.decimals),
                    highlighted = !feed.meows.isNullOrEmpty(),
                    enabled = !isMyOwnFeed,
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

@PreviewsDayNight
@Composable
internal fun FeedDetailsCellPreview() = ElementPreview {
    FeedDetailsCell(
        feed = ZeroFeed.placeholder,
        zeroUserRewards = ZeroUserRewards.empty(),
        onAddMeowToFeed = {}
    )
}
