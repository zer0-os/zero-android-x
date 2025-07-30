/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.ApiWalletTokens
import io.element.android.support.zero.network.model.response.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.ApiWalletTransactions
import io.element.android.support.zero.network.model.response.NextPageParams
import io.element.android.support.zero.network.model.response.TransactionNextPageParams

interface WalletRepository {
    suspend fun checkAndInitializeThirdWeb()

    suspend fun getTokens(walletAddress: String, nextPageParams: NextPageParams?): ApiWalletTokens

    suspend fun getTransactions(walletAddress: String, nextPageParams: TransactionNextPageParams?): ApiWalletTransactions

    suspend fun getTransactionReceipt(transactionHash: String): ApiWalletTransactionReceipt
}
