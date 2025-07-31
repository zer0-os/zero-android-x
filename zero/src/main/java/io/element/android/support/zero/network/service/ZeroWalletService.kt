/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.response.ApiWalletTokens
import io.element.android.support.zero.network.model.response.ApiTransactionPerformed
import io.element.android.support.zero.network.model.response.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.ApiWalletTransactions
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ZeroWalletService {

    @POST(value = "thirdweb/initialize-wallet")
    fun initializeThirdWebWallet()

    @GET(value = "api/wallet/{wallet_address}/tokens")
    suspend fun getTokens(
        @Path("wallet_address") walletAddress: String,
        @QueryMap nextPageParams: Map<String, @JvmSuppressWildcards Any> = emptyMap()
    ): ApiWalletTokens

    @GET(value = "api/wallet/{wallet_address}/transactions")
    suspend fun getTransactions(
        @Path("wallet_address") walletAddress: String,
        @QueryMap nextPageParams: Map<String, @JvmSuppressWildcards Any> = emptyMap()
    ): ApiWalletTransactions

    @GET(value = "api/wallet/transaction/{transaction_hash}/receipt")
    suspend fun getTransactionReceipt(
        @Path("transaction_hash") transactionHash: String
    ): ApiWalletTransactionReceipt

    @POST(value = "api/wallet/{wallet_address}/claim-rewards")
    suspend fun claimRewards(
        @Path("wallet_address") walletAddress: String
    ): ApiTransactionPerformed
}
