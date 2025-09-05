/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallet.impl.manage

import androidx.compose.runtime.Immutable
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet

sealed interface ManageWalletUserAction {
    data object None : ManageWalletUserAction
    data object SelectWallet : ManageWalletUserAction
    data class LinkWallet(val address: String) : ManageWalletUserAction
    data class RemoveWallet(val id: String) : ManageWalletUserAction
}

@Immutable
data class ManageWalletsState(
    val userId: UserId,
    val wallets: List<ZeroWallet>,

    val userActionState: ManageWalletUserAction,
    val actionState: AsyncAction<Unit>,
    val eventSink: (ManageWalletsEvents) -> Unit
) {
    val selfCustodyWallets: List<ZeroWallet>
        get() = wallets.filter { !it.isThirdWeb }

    val selfCustodyWalletsCount: Int
        get() = selfCustodyWallets.count()


    val firstSelfCustodyWallet: ZeroWallet?
        get() = selfCustodyWallets.firstOrNull()

    val zeroWallets: List<ZeroWallet>
        get() = wallets.filter { it.isThirdWeb }
}
