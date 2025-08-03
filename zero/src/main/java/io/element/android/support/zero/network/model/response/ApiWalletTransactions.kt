/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiWalletTransactions(
    val transactions: List<ApiWalletTransaction>,
    val nextPageParams: TransactionNextPageParams?
)

@Serializable
data class ApiWalletTransaction(
    val hash: String,
    val from: String,
    val to: String,
    val action: String,
    val token: ApiTransactionToken,
    val amount: String?,
    val timestamp: String,
    val tokenId: String?,
    val type: String
)

@Serializable
data class ApiTransactionToken(
    val symbol: String,
    val name: String,
    val logo: String?,
    val decimals: Int
)

@Serializable
data class TransactionNextPageParams(
    @SerialName("block_number")
    val blockNumber: Int,
    val index: Int
)

fun TransactionNextPageParams.toQueryMap(): Map<String, Any> {
    return mapOf(
        "block_number" to this.blockNumber,
        "index" to this.index
    )
}
