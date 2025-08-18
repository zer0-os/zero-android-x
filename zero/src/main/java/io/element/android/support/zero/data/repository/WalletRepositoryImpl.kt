/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.ApproveERC20Request
import io.element.android.support.zero.network.model.request.TransferWalletTokenRequest
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
import io.element.android.support.zero.network.model.response.wallet.toQueryMap
import io.element.android.support.zero.network.service.ZeroUserService
import io.element.android.support.zero.network.service.ZeroWalletService

class WalletRepositoryImpl(
    private val zeroUserService: ZeroUserService,
    private val zeroWalletService: ZeroWalletService
): WalletRepository {

    override suspend fun checkAndInitializeThirdWeb() {
        runCatching {
            val currentUser = zeroUserService.getCurrentUser()
            if (currentUser.thirdWebWallet == null) {
                // Initialize thirdWeb wallet
                zeroWalletService.initializeThirdWebWallet()
            }
        }
    }

    override suspend fun getTokens(walletAddress: String, nextPageParams: NextPageParams?): ApiWalletTokens {
        return zeroWalletService.getTokens(
            walletAddress = walletAddress,
            nextPageParams = nextPageParams?.toQueryMap() ?: emptyMap()
        )
    }

    override suspend fun getTransactions(walletAddress: String, nextPageParams: TransactionNextPageParams?): ApiWalletTransactions {
        return zeroWalletService.getTransactions(
            walletAddress = walletAddress,
            nextPageParams = nextPageParams?.toQueryMap() ?: emptyMap()
        )
    }

    override suspend fun getTransactionReceipt(transactionHash: String): ApiWalletTransactionReceipt {
        return zeroWalletService.getTransactionReceipt(transactionHash)
    }

    override suspend fun claimRewards(walletAddress: String): ApiTransactionPerformed {
        return zeroWalletService.claimRewards(walletAddress)
    }

    override suspend fun searchRecipients(query: String): List<ApiWalletRecipient> {
        return zeroWalletService.searchRecipient(query).recipients
    }

    override suspend fun transferToken(sender: String, recipient: String, amount: String, token: String): ApiWalletTransactionReceipt {
        val transaction = zeroWalletService.transferToken(
            senderWalletAddress = sender,
            request = TransferWalletTokenRequest(to = recipient, amount = amount, tokenAddress = token)
        )
        return getTransactionReceipt(transaction.transactionHash)
    }

    override suspend fun getTokenInfo(tokenAddress: String): ApiWalletTokenInfo {
        return zeroWalletService.getTokenInfo(tokenAddress)
    }

    override suspend fun getTokenBalance(userAddress: String, tokenAddress: String): ApiWalletTokenBalance {
        return zeroWalletService.getTokenBalance(userAddress, tokenAddress)
    }

    override suspend fun approveERC20(userAddress: String, amount: String, poolAddress: String, tokenAddress: String): ApiTransactionPerformed {
        val request = ApproveERC20Request(amount = amount, spenderAddress = poolAddress, tokenAddress = tokenAddress)
        return zeroWalletService.approveERC20(userAddress, request)
    }

    override suspend fun verifyERC20Approval(userAddress: String, poolAddress: String, tokenAddress: String): ApiWalletStakingApprovalResponse {
        return zeroWalletService.verifyERC20Approval(userAddress, tokenAddress, poolAddress)
    }
}
