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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.atomic.atoms.PlaceholderAtom
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.placeholderBackground
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.support.zero.common.util.wallet.WalletChainsUtil
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.isTransactionReceived
import io.element.android.support.zero.common.ui.WalletChainIcon

@Composable
fun WalletTransactionsList(
    state: WalletContentState,
    modifier: Modifier = Modifier,
) {
    val contentState = state.transactionsListState
    Box(modifier = modifier) {
        when (contentState) {
            is WalletTransactionsListState.Skeleton -> {
                SkeletonView(
                    count = contentState.count,
                )
            }
            is WalletTransactionsListState.Empty -> {
                HomeTabContentEmptyView(modifier = modifier, text = "No transactions")
            }
            is WalletTransactionsListState.Transactions -> {
                TransactionsList(
                    state = contentState,
                    meowPrice = state.meowPrice,
                    hasNextPage = state.transactionsPaginationParams != null,
                    onLoadMoreTransactions = {
                        state.eventSink(
                            HomeEvents.WalletEvents.LoadMoreTransactions(contentState.transactions)
                        )
                    },
                    onTransactionTapped = { transaction ->
                        state.eventSink(HomeEvents.WalletEvents.ViewWalletTransaction(transaction.hash, transaction.token.chainId))
                    })
            }
        }
    }
}

@Composable
private fun SkeletonView(count: Int, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        repeat(count) { index ->
            item {
                WalletTokenTransactionPlaceholderRow()
            }
        }
    }
}

@Composable
private fun WalletTokenTransactionPlaceholderRow(modifier: Modifier = Modifier) {
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
            Column {
                PlaceholderAtom(
                    width = 250.dp,
                    height = 7.dp
                )
                Spacer(Modifier.height(8.dp))
                PlaceholderAtom(
                    width = 250.dp,
                    height = 7.dp
                )
            }
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
private fun TransactionsList(
    state: WalletTransactionsListState.Transactions,
    meowPrice: ZeroMeowPrice?,
    hasNextPage: Boolean,
    onLoadMoreTransactions: () -> Unit,
    onTransactionTapped: (ZeroWalletTransaction) -> Unit
) {
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.transactions.lastIndex - 1
        }
    }
    // Load more items when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems && hasNextPage) {
            isLoadingMoreItems = true
            onLoadMoreTransactions()
        }
    }
    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = state.transactions, key = { trans -> trans.hash }) { transaction ->
            TransactionRow(transaction = transaction, meowPrice = meowPrice, onTransactionTapped = {
                onTransactionTapped(transaction)
            })
        }
        item {
            Spacer(Modifier.size(100.dp))
        }
    }
}

@Composable
private fun TransactionRow(
    modifier: Modifier = Modifier,
    transaction: ZeroWalletTransaction,
    meowPrice: ZeroMeowPrice?,
    onTransactionTapped: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTransactionTapped() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                    .clip(CircleShape),
                model = transaction.token.logo,
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center,
                contentDescription = null,
                error = painterResource(R.drawable.ic_zero_avatar_default),
            )
            transaction.token.chainId?.let { chainId ->
                val chain = WalletChainsUtil.getChain(chainId)
                if (chain != null) {
                    WalletChainIcon(icon = ImageVector.vectorResource(chain.logo))
                }
            } ?: WalletChainIcon(icon = ImageVector.vectorResource(WalletChainsUtil.z_chain.logo))
        }

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (transaction.isTransactionReceived) "Received from" else "Sent to",
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.textSecondary
                )
//                ZChainIcon(Modifier.padding(horizontal = 4.dp))
                val walletAddress = if (transaction.isTransactionReceived) {
                    ZeroWalletUtil.walletAddressDisplayText(transaction.from)
                } else {
                    ZeroWalletUtil.walletAddressDisplayText(transaction.to)
                }
                Text(
                    text = " $walletAddress",
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = transaction.token.name,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                ZeroWalletUtil.thousandSeparatedFormat(transaction.amount),
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textPrimary
            )

//            if (transaction.isClaimableTransaction && meowPrice != null) {
//                val meowPrice = ZeroWalletUtil.getMeowTokenPriceFormatted(transaction.tokenAmount, meowPrice)
//                Text(
//                    "$$meowPrice",
//                    style = ElementTheme.typography.fontBodyMdRegular,
//                    color = ElementTheme.colors.zeroBrandColor
//                )
//            }
            Text(
                "--",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
        }

        IconButton(onClick = onTransactionTapped, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = CompoundIcons.ChevronRight(), contentDescription = null)
        }
    }
}
