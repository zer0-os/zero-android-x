/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog

@Composable
fun ConnectWalletAlert(
    walletAddress: String,
    onConfirmLinking: (String, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val enabledLoggingIn = remember { mutableStateOf(true) }

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
                    text = "Your currently connected wallet has the address:",
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
                    text = "Do you want to link this wallet with your ZERO account?",
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = enabledLoggingIn.value,
                        onCheckedChange = {
                            enabledLoggingIn.value = it
                        },
                        colors = CheckboxDefaults.colors().copy(
                            checkedBoxColor = ElementTheme.colors.zeroBrandColor
                        )
                    )
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = {
                                enabledLoggingIn.value = !enabledLoggingIn.value
                            }),
                        text = "Enable logging into your zero account with this wallet?",
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textSecondary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmLinking(walletAddress, enabledLoggingIn.value) }) {
                Text(text = "Link", color = ElementTheme.colors.zeroBrandColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(CommonStrings.action_cancel), color = ElementTheme.colors.textPrimary)
            }
        },
        onDismiss = onCancel
    )
}
