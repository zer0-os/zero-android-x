/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil

@Composable
fun WalletsListView(
    modifier: Modifier = Modifier,
    wallets: List<ZeroWallet>,
    canRemoveWallet: Boolean = false,
    onWalletTap: (ZeroWallet) -> Unit = {},
    onRemoveWallet: (ZeroWallet) -> Unit = {}
) {
    val listState = rememberLazyListState()
    if (wallets.isEmpty()) {
        NoWalletsInfotext(modifier)
    } else {
        LazyColumn(modifier = modifier, state = listState) {
            items(wallets, key = { wallet -> wallet.id }) { wallet ->
                WalletRow(
                    wallet = wallet,
                    canRemoveWallet = canRemoveWallet,
                    onWalletTap = { onWalletTap(wallet) },
                    onRemoveWallet = { onRemoveWallet(wallet) },
                )
            }
        }
    }
}

@Composable
fun NoWalletsInfotext(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CompoundIcons.InfoSolid(),
            contentDescription = null,
            tint = ElementTheme.colors.textSecondary
        )
        Text(
            modifier = Modifier.padding(horizontal = 6.dp),
            text = "No wallets found",
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary
        )
    }
}

@Composable
fun WalletRow(
    modifier: Modifier = Modifier,
    wallet: ZeroWallet,
    canRemoveWallet: Boolean = false,
    onWalletTap: () -> Unit = {},
    onRemoveWallet: () -> Unit = {}
) {
    Row(modifier = modifier
        .clickable { onWalletTap() }
        .fillMaxWidth()
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatarData = AvatarData(id = wallet.id, name = null, url = null, size = AvatarSize.UserListItem),
                avatarType = AvatarType.User
            )
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                ZeroWalletUtil.walletAddressDisplayText(wallet.publicAddress)?.let { address ->
                    Text(
                        text = address,
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textPrimary
                    )
                }
                if (wallet.canAuthenticate == true) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .background(
                                color = ElementTheme.colors.bgCanvasDefaultLevel1,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        text = "Authenticator",
                        style = ElementTheme.typography.fontBodySmRegular,
                        color = ElementTheme.colors.textPrimary
                    )
                }
            }
        }
        if (canRemoveWallet) {
            IconButton(onClick = onRemoveWallet) {
                Icon(imageVector = CompoundIcons.Close(), contentDescription = null)
            }
        }
    }
}

@PreviewsDayNight
@Composable
fun WalletsListViewPreview() = ElementPreview {
    WalletsListView(
        wallets = emptyList()
    )
}
