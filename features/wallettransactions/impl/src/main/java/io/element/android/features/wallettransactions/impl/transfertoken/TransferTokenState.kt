/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken

import androidx.compose.runtime.Immutable
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TransferTokenState(
    val flowStep: TransferTokenFlowStep,
    val currentUser: MatrixUser,

    val recipientsListState: WalletRecipientsListState,
    val recipient: ZeroWalletRecipient?,

    val token: ZeroWalletToken?,
    val tokensListState: WalletTokensListState,
    val tokensPaginationParams: ZeroWalletTokensPaginationParams?,
    val meowPrice: ZeroMeowPrice?,

    val transferAmount: String? = null,
    val transactionReceipt: ZeroWalletTransactionReceipt? = null,

    val eventSink: (TransferTokenEvents) -> Unit
) {
    val showRecipientsResult: Boolean
        get() = (recipientsListState as? WalletRecipientsListState.Recipients)?.recipients?.isNotEmpty() == true
}

enum class TransferTokenFlowStep {
    RECIPIENT,
    TOKEN,
    CONFIRMATION,
    IN_PROGRESS,
    COMPLETED,
    ERROR
}

@Immutable
sealed interface WalletRecipientsListState {
    data object Skeleton : WalletRecipientsListState
    data object Empty : WalletRecipientsListState
    data object None : WalletRecipientsListState
    data class Recipients(
        val recipients: ImmutableList<ZeroWalletRecipient>
    ) : WalletRecipientsListState
}
