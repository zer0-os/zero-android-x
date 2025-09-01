/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage.view

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog

@Composable
fun RemoveWalletAlert(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    ZeroAlertDialog(
        title = "Remove Wallet",
        message = "Are you sure you want to remove this wallet from your ZERO account?",
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Remove", color = ElementTheme.colors.textCriticalPrimary)
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
