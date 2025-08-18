/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.wallet.ApiTransactionPerformed
import io.element.android.support.zero.network.model.response.wallet.ApiWalletRecipient
import io.element.android.support.zero.network.model.response.wallet.ApiWalletStakingApprovalResponse
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenBalance
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenInfo
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokens
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactions
import io.element.android.support.zero.network.model.response.wallet.NextPageParams
import io.element.android.support.zero.network.model.response.wallet.TransactionNextPageParams

interface WalletRepository {
    suspend fun checkAndInitializeThirdWeb()

    suspend fun getTokens(walletAddress: String, nextPageParams: NextPageParams?): ApiWalletTokens

    suspend fun getTransactions(walletAddress: String, nextPageParams: TransactionNextPageParams?): ApiWalletTransactions

    suspend fun getTransactionReceipt(transactionHash: String): ApiWalletTransactionReceipt

    suspend fun claimRewards(walletAddress: String): ApiTransactionPerformed

    suspend fun searchRecipients(query: String): List<ApiWalletRecipient>

    suspend fun transferToken(sender: String, recipient: String, amount: String, token: String): ApiWalletTransactionReceipt

    suspend fun getTokenInfo(tokenAddress: String): ApiWalletTokenInfo

    suspend fun getTokenBalance(userAddress: String, tokenAddress: String): ApiWalletTokenBalance

    suspend fun approveERC20(userAddress: String, amount: String, poolAddress: String, tokenAddress: String): ApiTransactionPerformed

    suspend fun verifyERC20Approval(userAddress: String, poolAddress: String, tokenAddress: String): ApiWalletStakingApprovalResponse
}
