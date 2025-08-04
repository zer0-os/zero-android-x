/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.network.model.response.ApiWalletRecipient
import io.element.android.support.zero.network.model.response.ApiWalletTokens
import io.element.android.support.zero.network.model.response.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.ApiWalletTransactions
import io.element.android.support.zero.network.model.response.NextPageParams
import io.element.android.support.zero.network.model.response.TransactionNextPageParams

fun ApiWalletTokens.toModel(): ZeroWalletTokensResponse {
    return ZeroWalletTokensResponse(
        tokens = tokens.map { token ->
            val tokenAmount = token.amount.toDoubleOrNull() ?: 0.0
            ZeroWalletToken(
                token.tokenAddress,
                token.symbol,
                token.name,
                ZeroWalletUtil.getFormattedNumber(tokenAmount, false),
                token.logo,
                token.decimals
            )
        },
        paginationParams = nextPageParams?.let { nextPageParams ->
            ZeroWalletTokensPaginationParams(
                nextPageParams.itemsCount,
                nextPageParams.tokenName,
                nextPageParams.tokenType,
                nextPageParams.value
            )
        }
    )
}

fun ApiWalletTransactions.toModel(): ZeroWalletTransactionsResponse {
    return ZeroWalletTransactionsResponse(
        transactions = transactions.map { transaction ->
            val transactionAmount = transaction.amount?.toDoubleOrNull() ?: 0.0
            ZeroWalletTransaction(
                transaction.hash,
                transaction.from,
                transaction.to,
                transaction.action,
                token = ZeroWalletTransactionToken(
                    transaction.token.symbol,
                    transaction.token.name,
                    transaction.token.logo,
                    transaction.token.decimals
                ),
                ZeroWalletUtil.getFormattedNumber(transactionAmount, false),
                transaction.timestamp,
                transaction.tokenId,
                transaction.type
            )
        },
        paginationParams = nextPageParams?.let { nextPageParams ->
            ZeroWalletTransactionsPaginationParams(
                blockNumber = nextPageParams.blockNumber,
                index = nextPageParams.index
            )
        }
    )
}

fun ApiWalletTransactionReceipt.toModel(): ZeroWalletTransactionReceipt {
    return ZeroWalletTransactionReceipt(status, blockExplorerUrl, transactionHash)
}

fun ApiWalletRecipient.toModel(): ZeroWalletRecipient {
    return ZeroWalletRecipient(userId, matrixId, publicAddress, name, profileImage, primaryZid)
}

fun ZeroWalletTokensPaginationParams.toApi() = NextPageParams(itemsCount, tokenName, tokenType, value)
fun ZeroWalletTransactionsPaginationParams.toApi() = TransactionNextPageParams(blockNumber, index)
