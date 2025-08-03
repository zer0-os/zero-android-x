/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken.token

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.home.impl.components.HomeTabContentEmptyView
import io.element.android.features.home.impl.wallet.TokenSkeletonView
import io.element.android.features.home.impl.wallet.TokensList
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenEvents
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenState
import io.element.android.features.wallettransactions.impl.transfertoken.TransferTokenStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.wallet.displayName

@Composable
fun SelectTokenView(
    modifier: Modifier = Modifier,
    state: TransferTokenState
) {
    val contentState = state.tokensListState
    Box(modifier = modifier.fillMaxSize()) {
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
                            TransferTokenEvents.LoadMoreTokens(contentState.tokens)
                        )
                    },
                    onTokenClick = { token ->
                        state.eventSink(TransferTokenEvents.TokenSelected(token))
                    })
            }
        }

        state.recipient?.let {
            SelectedRecipientView(it)
        }
    }
}

@Composable
fun BoxScope.SelectedRecipientView(recipient: ZeroWalletRecipient) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(
                color = ElementTheme.colors.bgCanvasDefault,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sending To:",
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary
        )
        Text(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            text = recipient.displayName,
            style = ElementTheme.typography.fontBodyLgMedium,
            color = ElementTheme.colors.textPrimary
        )
        ZeroWalletUtil.walletAddressDisplayText(recipient.publicAddress)?.let {
            Text(
                text = it,
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )
        }
    }
}

@PreviewsDayNight
@Composable
fun SelectTokenViewPreview(
    @PreviewParameter(TransferTokenStateProvider::class) state: TransferTokenState
) = ElementPreview {
    SelectTokenView(state = state)
}
