/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFT
import io.element.android.libraries.matrix.api.zero.wallet.tokenUrl
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.openExternalUri
import io.element.android.support.zero.common.ui.theme.SPACING_1X

@Composable
fun WalletNFTsList(
    modifier: Modifier = Modifier,
    state: WalletContentState,
    onNFTClick: (ZeroWalletNFT) -> Unit = {}
) {
    val contentState = state.nftsListState
    Box(modifier = modifier.padding(16.dp)) {
        when (contentState) {
            is WalletNFTsListState.Skeleton,
            is WalletNFTsListState.Empty -> {
                //TODO: handle skeleton state, need to make placeholder skeleton view
                HomeTabContentEmptyView(modifier = modifier, text = "No NFTs")
            }
            is WalletNFTsListState.NFTs -> {
                NFTsList(
                    state = contentState,
                    hasNextPage = state.nftsPaginationParams != null,
                    onLoadMoreNFTs = {
                        state.eventSink(
                            WalletEvents.LoadMoreNFTs(contentState.nfts)
                        )
                    },
                    onNFTClick = onNFTClick
                )
            }
        }
    }
}

@Composable
fun NFTsList(
    state: WalletNFTsListState.NFTs,
    hasNextPage: Boolean,
    onLoadMoreNFTs: () -> Unit,
    onNFTClick: (ZeroWalletNFT) -> Unit = {}
) {
    var isLoadingMoreItems by remember(state) { mutableStateOf(false) }

    val lazyGridState = rememberLazyGridState()

    val shouldLoadMoreFeed by remember(state) {
        derivedStateOf {
            val lastVisibleItemIndex = lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            // Start loading next page when 2nd last item is visible
            lastVisibleItemIndex == state.nfts.lastIndex - 1
        }
    }
    // Load more items when second last item becomes visible
    LaunchedEffect(shouldLoadMoreFeed) {
        if (shouldLoadMoreFeed && !isLoadingMoreItems && hasNextPage) {
            isLoadingMoreItems = true
            onLoadMoreNFTs()
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(items = state.nfts, key = { nft -> nft.id }) { nft ->
            NFTRow(nft = nft, onTap = { onNFTClick(nft) })
        }
        item {
            Spacer(Modifier.size(100.dp))
        }
    }
}

@Composable
private fun NFTRow(
    modifier: Modifier = Modifier,
    nft: ZeroWalletNFT,
    onTap: () -> Unit = {}
) {
    val corner = RoundedCornerShape(18.dp)
    var didCopy by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(corner)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.White.copy(alpha = 0.04f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), corner)
            .clickable { onTap() }
    ) {
        val clipboard: ClipboardManager = LocalClipboardManager.current
        val context: Context = LocalContext.current

        Column {
            WalletNFTImage(
                imageUrl = nft.imageUrl,
                collectionName = nft.collectionName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            WalletNFTInfo(
                title = nft.metadata?.name?.takeIf { it.isNotBlank() } ?: (nft.collectionName ?: "—"),
                id = nft.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .padding(end = 60.dp)
            )
        }

        val qty = nft.quantity ?: 0
        if (qty > 1) {
            WalletNFTPill(
                text = "Qty x $qty",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }

        val token = nft.tokenType.orEmpty()
        if (token.isNotBlank()) {
            WalletNFTPill(
                text = token,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WalletNFTActionButton(
                onClick = {
                    clipboard.setText(AnnotatedString(nft.id))
                    didCopy = true
                },
                modifier = Modifier.semantics { contentDescription = "Copy ID" }
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = if (didCopy) ImageVector.vectorResource(io.element.android.compound.R.drawable.ic_compound_check)
                    else ImageVector.vectorResource(io.element.android.compound.R.drawable.ic_compound_copy),
                    contentDescription = null,
                    tint = ElementTheme.colors.iconSecondary
                )
            }

            WalletNFTActionButton(
                onClick = {
                    context.openExternalUri(nft.tokenUrl)
                },
                modifier = Modifier.semantics { contentDescription = "Open" }
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_post_arweave),
                    contentDescription = null,
                    tint = ElementTheme.colors.iconSecondary
                )
            }
        }
    }

    LaunchedEffect(didCopy) {
        if (didCopy) {
            kotlinx.coroutines.delay(1100)
            didCopy = false
        }
    }
}

@Composable
private fun WalletNFTImage(
    imageUrl: String?,
    collectionName: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            val context = LocalContext.current

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier.size(50.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_default_nft),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = ElementTheme.colors.textPrimary.copy(alpha = 0.6f)
                    )
                )
                collectionName?.let {
                    Spacer(Modifier.size(SPACING_1X.dp))
                    Text(
                        text = it,
                        style = ElementTheme.zeroTypography.fontBodyMdMedium,
                        color = ElementTheme.colors.textPrimary.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WalletNFTInfo(
    title: String,
    id: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            color = ElementTheme.colors.textPrimary,
            style = ElementTheme.zeroTypography.fontBodyLgMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "ID: $id",
            color = ElementTheme.colors.textSecondary,
            style = ElementTheme.zeroTypography.fontBodySmRegular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WalletNFTPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(ElementTheme.colors.zeroBrandColor.copy(alpha = 0.3f))
            .border(1.dp, ElementTheme.colors.zeroBrandColor.copy(alpha = 0.5f), RoundedCornerShape(40.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = ElementTheme.colors.zeroBrandColor,
            style = ElementTheme.zeroTypography.fontBodySmRegular
        )
    }
}

@Composable
private fun WalletNFTActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.06f))
            .combinedClickable(onClick = onClick, onLongClick = onClick),
        contentAlignment = Alignment.Center,
        content = content
    )
}
