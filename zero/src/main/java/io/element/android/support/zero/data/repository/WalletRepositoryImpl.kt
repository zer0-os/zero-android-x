/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.request.TransferWalletTokenRequest
import io.element.android.support.zero.network.model.response.ApiTransactionPerformed
import io.element.android.support.zero.network.model.response.ApiWalletRecipient
import io.element.android.support.zero.network.model.response.ApiWalletTokens
import io.element.android.support.zero.network.model.response.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.ApiWalletTransactions
import io.element.android.support.zero.network.model.response.NextPageParams
import io.element.android.support.zero.network.model.response.TransactionNextPageParams
import io.element.android.support.zero.network.model.response.toQueryMap
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
}
