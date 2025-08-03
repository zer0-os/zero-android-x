/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.wallettransactions.impl.transfertoken

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.element.android.features.home.impl.wallet.WalletTokensListState
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser

open class TransferTokenStateProvider : PreviewParameterProvider<TransferTokenState> {
    override val values: Sequence<TransferTokenState>
        get() = sequenceOf(
            aTransferTokenState(),
        )
}

fun aTransferTokenState(
) = TransferTokenState(
    flowStep = TransferTokenFlowStep.RECIPIENT,
    currentUser = MatrixUser(userId = UserId("")),
    recipientsListState = WalletRecipientsListState.None,
    recipient = null,
    token = null,
    tokensListState = WalletTokensListState.Empty,
    tokensPaginationParams = null,
    meowPrice = null,
    transferAmount = null,
    eventSink = {}
)
