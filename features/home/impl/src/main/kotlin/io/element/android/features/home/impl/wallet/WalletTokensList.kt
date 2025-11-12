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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.libraries.core.extensions.toLocalizedDoubleOrZero
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.placeholderBackground
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.isClaimableToken
import io.element.android.libraries.matrix.api.zero.wallet.meowPriceFormatted
import io.element.android.libraries.matrix.api.zero.wallet.tokenPriceFormatted
import io.element.android.support.zero.common.extension.roundTo
import io.element.android.support.zero.common.ui.WalletChainIcon
import io.element.android.support.zero.common.util.wallet.WalletChainsUtil
import kotlin.math.abs

@Composable
fun WalletTokensList(
    state: WalletContentState,
    modifier: Modifier = Modifier,
) {
    val contentState = state.tokensListState
    Box(modifier = modifier) {
        when (contentState) {
            is WalletTokensListState.Skeleton -> {
                TokenSkeletonView(
                    count = contentState.count,
                )
            }
            is WalletTokensListState.Empty -> {
                HomeTabContentEmptyView(modifier = modifier, text = "No tokens")
            }
            is WalletTokensListState.Tokens -> {
                TokensList(
                    state = contentState,
                    meowPrice = state.meowPrice,
                    hasNextPage = state.tokensPaginationParams != null,
                    onLoadMoreTokens = {
                        state.eventSink(
                            WalletEvents.LoadMoreTokens(contentState.tokens)
                        )
                    })
            }
        }
    }
}

@Composable
fun TokenSkeletonView(count: Int, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(count) { index ->
            item {
                WalletTokenPlaceholderRow()
            }
        }
    }
}

@Composable
private fun WalletTokenPlaceholderRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AvatarSize.RoomListItem.dp)
                .background(color = ElementTheme.colors.placeholderBackground, shape = CircleShape)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            PlaceholderAtom(
                width = 200.dp,
                height = 7.dp
            )
            Spacer(Modifier.width(8.dp))
            Column {
                PlaceholderAtom(
                    width = 100.dp,
                    height = 7.dp
                )
                Spacer(Modifier.height(8.dp))
                PlaceholderAtom(
                    width = 100.dp,
                    height = 7.dp
                )
            }
        }
    }
}

@Composable
fun TokensList(
    state: WalletTokensListState.Tokens,
    meowPrice: ZeroMeowPrice?,
    hasNextPage: Boolean,
    onLoadMoreTokens: () -> Unit,
    onTokenClick: (ZeroWalletToken) -> Unit = {}
) {
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.tokens.lastIndex - 1
        }
    }
    // Load more items when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems && hasNextPage) {
            isLoadingMoreItems = true
            onLoadMoreTokens()
        }
    }
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = state.tokens, key = { token -> token.tokenAddress }) { token ->
            TokenRow(token = token, meowPrice = meowPrice, onTap = {
                onTokenClick(token)
            })
        }
        item {
            Spacer(Modifier.size(100.dp))
        }
    }
}

@Composable
private fun TokenRow(
    modifier: Modifier = Modifier,
    token: ZeroWalletToken,
    meowPrice: ZeroMeowPrice?,
    onTap: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                    .clip(CircleShape),
                model = token.logo,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                contentDescription = null,
                error = painterResource(R.drawable.ic_zero_avatar_default)
            )
            val chain = WalletChainsUtil.getChain(token.chainId)
            if (chain != null) {
                WalletChainIcon(icon = ImageVector.vectorResource(chain.logo))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = token.name,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val tokenAmount = "${ZeroWalletUtil.thousandSeparatedFormat(token.amount)} ${token.symbol.uppercase()}"
            Text(
                tokenAmount,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
        }

        if (token.isClaimableToken) {
            val refPrice = when {
                WalletChainsUtil.isAvaxChain(token.chainId) -> token.tokenPriceFormatted
                else -> if (meowPrice != null) { token.meowPriceFormatted(meowPrice) } else ""
            }

            val priceChange = (when {
                WalletChainsUtil.isAvaxChain(token.chainId) -> {
                    token.percentChange?.toLocalizedDoubleOrZero()
                }
                else -> meowPrice?.diff
            })?.roundTo(2)

            Column(horizontalAlignment = Alignment.End) {
                if (refPrice.isNotBlank()) {
                    Text(
                        "$$refPrice",
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textPrimary
                    )
                }
                if (priceChange != null) {
                    Text(
                        text = if (priceChange > 0) {
                            "+$priceChange%"
                        } else {
                            "-${abs(priceChange)}%"
                        },
                        style = ElementTheme.typography.fontBodyMdRegular,
                        color = if (priceChange > 0) ElementTheme.colors.zeroBrandColor else ElementTheme.colors.textCriticalPrimary
                    )
                }
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun WalletTokensListPreview(
    @PreviewParameter(WalletContentStateProvider::class) state: WalletContentState
) = ElementPreview {
    WalletTokensList(state = state)
}
