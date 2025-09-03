/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.request.ApproveERC20Request
import io.element.android.support.zero.network.model.request.TransferWalletTokenRequest
import io.element.android.support.zero.network.model.response.wallet.ApiTransactionPerformed
import io.element.android.support.zero.network.model.response.wallet.ApiWalletRecipientsResponse
import io.element.android.support.zero.network.model.response.wallet.ApiWalletStakingApprovalResponse
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenBalance
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenInfo
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokens
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactions
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ZeroWalletService {

    @POST(value = "thirdweb/initialize-wallet")
    fun initializeThirdWebWallet()

    @GET(value = "api/wallet/{wallet_address}/tokens")
    suspend fun getTokens(
        @Path("wallet_address") walletAddress: String,
        @Query("chainId") chainIds: List<String>,
        @QueryMap nextPageParams: Map<String, @JvmSuppressWildcards Any> = emptyMap()
    ): ApiWalletTokens

    @GET(value = "api/wallet/{wallet_address}/transactions")
    suspend fun getTransactions(
        @Path("wallet_address") walletAddress: String,
        @Query("chainId") chainIds: List<String>,
        @QueryMap nextPageParams: Map<String, @JvmSuppressWildcards Any> = emptyMap()
    ): ApiWalletTransactions

    @GET(value = "api/wallet/transaction/{transaction_hash}/receipt")
    suspend fun getTransactionReceipt(
        @Path("transaction_hash") transactionHash: String,
        @Query("chainId") chainIds: List<String>,
    ): ApiWalletTransactionReceipt

    @POST(value = "api/wallet/{wallet_address}/claim-rewards")
    suspend fun claimRewards(
        @Path("wallet_address") walletAddress: String
    ): ApiTransactionPerformed

    @GET(value = "api/wallet/search-recipients")
    suspend fun searchRecipient(
        @Query("query") searchQuery: String
    ): ApiWalletRecipientsResponse

    @POST(value = "api/wallet/{sender_address}/transactions/transfer-token")
    suspend fun transferToken(
        @Path("sender_address") senderWalletAddress: String,
        @Body request: TransferWalletTokenRequest
    ): ApiTransactionPerformed

    @GET(value = "api/tokens/{token_address}/info")
    suspend fun getTokenInfo(
        @Path("token_address") tokenAddress: String
    ): ApiWalletTokenInfo

    @GET(value = "api/wallet/{user_address}/token/{token_address}/balance")
    suspend fun getTokenBalance(
        @Path("user_address") userAddress: String,
        @Path("token_address") tokenAddress: String
    ): ApiWalletTokenBalance

    @POST(value = "api/wallet/{user_address}/transactions/approve-erc20")
    suspend fun approveERC20(
        @Path("user_address") userAddress: String,
        @Body request: ApproveERC20Request
    ): ApiTransactionPerformed

    @GET(value = "api/wallet/{user_address}/token/{token_address}/approval/{pool_address}")
    suspend fun verifyERC20Approval(
        @Path("user_address") userAddress: String,
        @Path("token_address") tokenAddress: String,
        @Path("pool_address") poolAddress: String,
    ): ApiWalletStakingApprovalResponse
}
