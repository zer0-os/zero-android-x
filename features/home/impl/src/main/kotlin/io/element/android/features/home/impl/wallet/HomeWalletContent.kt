/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.model.WalletContentTab
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.support.zero.R
import io.element.android.support.zero.common.ui.theme.SPACING_2X

@Composable
fun HomeWalletContent(
    modifier: Modifier = Modifier,
    state: WalletContentState,
    onSendWalletToken: () -> Unit = {},
    onReceiveWalletToken: () -> Unit = {},
) {
    var selectedWalletTab by rememberSaveable { mutableStateOf(WalletContentTab.TOKENS) }
    Column(
        modifier = modifier
            .background(color = ElementTheme.colors.bgCanvasDefault)
    ) {
        ZeroWalletCard(
            walletUserName = state.userName,
            walletBalance = state.userWalletBalance,
            showWalletBalance = state.showWalletBalance,
            onToggleWalletBalance = {
                state.eventSink(HomeEvents.ToggleWalletBalance)
            }
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            /*WalletActionButton(
                icon = CompoundIcons.ArrowUpRight(),
                text = "Receive",
                onClick = onReceiveWalletToken
            )*/
            WalletActionButton(
                icon = CompoundIcons.ArrowUpRight(),
                text = "Send",
                onClick = onSendWalletToken
            )
        }
        WalletContentTabView(
            modifier = Modifier.padding(horizontal = 16.dp),
            onTabSelected = { walletTab ->
                selectedWalletTab = walletTab
            }
        )
        when (selectedWalletTab) {
            WalletContentTab.TOKENS -> WalletTokensList(state)
            WalletContentTab.STAKING -> WalletStakingList(state)
            WalletContentTab.TRANSACTIONS -> WalletTransactionsList(state)
        }
    }
}

@Composable
private fun ZeroWalletCard(
    walletUserName: String,
    walletBalance: String,
    showWalletBalance: Boolean,
    onToggleWalletBalance: () -> Unit
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
                    onClick = onToggleWalletBalance,
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = if (showWalletBalance) {
                            CompoundIcons.VisibilityOff()
                        } else {
                            CompoundIcons.VisibilityOn()
                        },
                        contentDescription = null,
                        tint = ElementTheme.colors.textSecondary
                    )
                }
            }
            Text(
                text = walletBalance,
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

@Composable
fun RowScope.WalletActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .weight(1f)
            .border(
                width = 0.5.dp,
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElementTheme.colors.zeroBrandColor
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = text,
                style = ElementTheme.typography.fontBodyLgMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                color = ElementTheme.colors.zeroBrandColor
            )
        }
    }
}

@PreviewsDayNight
@Composable
private fun HomeWalletContentPreview(
    @PreviewParameter(WalletContentStateProvider::class) state: WalletContentState
) = ElementTheme {
    HomeWalletContent(state = state)
}
