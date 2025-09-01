/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog

@Composable
fun AlreadyConnectedWalletAlert(
    walletAddress: String,
    onCancel: () -> Unit
) {
    ZeroAlertDialog(
        title = {
            Text(
                text = "Link Wallet",
                style = ElementTheme.typography.fontHeadingSmMedium,
                color = ElementTheme.colors.textPrimary
            )
        },
        message = {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "This wallet is already linked to your ZERO account:",
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = walletAddress,
                    style = ElementTheme.typography.fontHeadingSmMedium,
                    color = ElementTheme.colors.textPrimary
                )
                Text(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = "Switch to another wallet to link a new one",
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(CommonStrings.action_ok))
            }
        },
        onDismiss = onCancel
    )
}
