/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.theme.SPACING_2X

@Composable
fun HomeWalletContent(
    state: WalletContentState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(color = ElementTheme.colors.bgCanvasDefault)
    ) {
        ZeroWalletCard(
            walletUserName = "Lefty Wilder",
            walletBalance = "0",
            onToggleWalletBalance = {}
        )
        WalletContentTabView(
            modifier = Modifier.padding(horizontal = 16.dp),
            onTabSelected = { walletTab ->

            }
        )
    }
}

@Composable
private fun ZeroWalletCard(
    walletUserName: String,
    walletBalance: String,
    onToggleWalletBalance: (Boolean) -> Unit
) {
    Box {
        Image(
            painter = painterResource(R.drawable.frame_zero_wallet_card),
            contentDescription = "frame_zero_wallet_card"
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Balance",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                )

                Spacer(Modifier.size(SPACING_2X.dp))

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = CompoundIcons.VisibilityOn(),
                        contentDescription = null,
                        tint = ElementTheme.colors.textSecondary
                    )
                }
            }
            Text(
                text = "$${walletBalance}",
                style = ElementTheme.typography.fontHeadingMdBold,
                color = ElementTheme.colors.textPrimary,
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 32.dp, vertical = 40.dp),
            text = walletUserName,
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
        )
    }
}

@PreviewsDayNight
@Composable
private fun HomeWalletContentPreview(
    @PreviewParameter(WalletContentStateProvider::class) state: WalletContentState
) = ElementTheme {
    HomeWalletContent(state = state)
}
