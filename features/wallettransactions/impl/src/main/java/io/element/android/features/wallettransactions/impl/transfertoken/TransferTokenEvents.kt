/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken

import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken

sealed interface TransferTokenEvents {
    data class ToState(val state: TransferTokenFlowStep) : TransferTokenEvents
    data class SearchRecipient(val query: String) : TransferTokenEvents
    data class RecipientSelected(val recipient: ZeroWalletRecipient) : TransferTokenEvents
    data class TokenSelected(val token: ZeroWalletToken) : TransferTokenEvents
    data class LoadMoreTokens(val currentTokens: List<ZeroWalletToken>) : TransferTokenEvents
    data class ConfirmAmount(val amount: String) : TransferTokenEvents
    data object ConfirmTransaction : TransferTokenEvents
    data class ViewTransaction(val url: String) : TransferTokenEvents
}
