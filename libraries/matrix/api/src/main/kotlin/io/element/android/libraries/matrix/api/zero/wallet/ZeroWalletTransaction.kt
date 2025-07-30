/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroWalletTransactionsResponse(
    val transactions: List<ZeroWalletTransaction>,
    val paginationParams: ZeroWalletTransactionsPaginationParams?
): Parcelable

@Parcelize
data class ZeroWalletTransaction(
    val hash: String,
    val from: String,
    val to: String,
    val action: String,
    val token: ZeroWalletTransactionToken,
    val amount: String?,
    val timestamp: String,
    val tokenId: String?,
    val type: String
) : Parcelable

@Parcelize
data class ZeroWalletTransactionToken(
    val symbol: String,
    val name: String,
    val logo: String?,
    val decimals: Int
) : Parcelable

@Parcelize
data class ZeroWalletTransactionsPaginationParams(
    val blockNumber: Int,
    val index: Int
) : Parcelable
