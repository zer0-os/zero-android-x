/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.model.HomeStakePool
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.support.zero.common.util.wallet.WalletChainsUtil
import io.element.android.support.zero.common.ui.WalletChainIcon

@Composable
fun WalletStakingList(
    state: WalletContentState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        // Header Row
        item {
            Row(modifier = Modifier.padding(8.dp)) {
                StakingContentHeading(
                    modifier = Modifier.weight(1f),
                    text = "Pool Name"
                )
                StakingContentHeading(
                    modifier = Modifier.weight(0.5f),
                    text = "TVL"
                )
                StakingContentHeading(
                    modifier = Modifier.weight(0.5f),
                    text = "Your Stake",
                    textAlign = TextAlign.End
                )
            }
        }
        items(state.stakePools, key = { pool -> pool.poolAddress }) {
            StakePoolCell(
                pool = it,
                onClick = {
                    state.eventSink(HomeEvents.StakePoolSelected(it))
                }
            )
        }
    }
}

@Composable
fun StakingContentHeading(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        text = text,
        style = ElementTheme.typography.fontBodyMdMedium,
        color = ElementTheme.colors.textSecondary,
        textAlign = textAlign
    )
}

@Composable
fun StakePoolCell(
    pool: HomeStakePool,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                        .clip(CircleShape),
                    model = pool.poolIcon,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    contentDescription = null,
                    error = painterResource(R.drawable.ic_zero_avatar_default)
                )
                val chain = WalletChainsUtil.getChain(pool.chainId)
                if (chain != null) {
                    WalletChainIcon(icon = ImageVector.vectorResource(chain.logo))
                }
            }
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = pool.poolDisplayName,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            modifier = Modifier.weight(0.5f),
            text = "$${pool.totalStakedAmountFormatted}",
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val textColor = if (pool.myStakeAmount > 0) ElementTheme.colors.textPrimary
        else ElementTheme.colors.textSecondary
        Text(
            modifier = Modifier.weight(0.5f),
            text = "$${pool.myStakeAmountFormatted}",
            style = ElementTheme.typography.fontBodyLgRegular,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End
        )
    }
}

@PreviewsDayNight
@Composable
fun WalletStakingListPreview(
    @PreviewParameter(WalletContentStateProvider::class) state: WalletContentState
) = ElementPreview {
    WalletStakingList(state = state)
}
