/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet

sealed interface ManageWalletsEvents {
    data class ShowWallet(val wallet: ZeroWallet) : ManageWalletsEvents
    data class ConfirmLinkWallet(val address: String, val enableLoggingIn: Boolean): ManageWalletsEvents
    data class RemoveWallet(val wallet: ZeroWallet): ManageWalletsEvents
    data class ConfirmDeleteWallet(val walletId: String): ManageWalletsEvents
    data object CheckAndLinkWallet : ManageWalletsEvents
    data object WalletLinkingCancelled: ManageWalletsEvents
    data object WalletRemovingCancelled: ManageWalletsEvents
    data object HideError : ManageWalletsEvents
}
