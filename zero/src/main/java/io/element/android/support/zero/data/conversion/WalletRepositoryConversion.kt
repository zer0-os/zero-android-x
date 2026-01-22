/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.wallet.ZeroAvaxTokenPrice
import io.element.android.libraries.matrix.api.zero.wallet.ZeroNFTAttribute
import io.element.android.libraries.matrix.api.zero.wallet.ZeroNFTMetadata
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFT
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletNFTsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletRecipient
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenBalance
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokenInfo
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTokensResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransaction
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionReceipt
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionToken
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsPaginationParams
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletTransactionsResponse
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.network.model.response.wallet.ApiAvaxTokenPrice
import io.element.android.support.zero.network.model.response.wallet.ApiWalletNFTs
import io.element.android.support.zero.network.model.response.wallet.ApiWalletRecipient
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenBalance
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokenInfo
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTokens
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactionReceipt
import io.element.android.support.zero.network.model.response.wallet.ApiWalletTransactions
import io.element.android.support.zero.network.model.response.wallet.NextPageParams
import io.element.android.support.zero.network.model.response.wallet.TransactionNextPageParams

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
                token.decimals,
                token.chainId,
                token.percentChange,
                token.price
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

fun ApiWalletNFTs.toModel(): ZeroWalletNFTsResponse {
    return ZeroWalletNFTsResponse(
        nfts = nfts.map { nft ->
            ZeroWalletNFT(
                animationUrl = nft.animationUrl,
                collectionAddress = nft.collectionAddress,
                collectionName = nft.collectionName,
                id = nft.id,
                imageUrl = nft.imageUrl,
                isUnique = nft.isUnique,
                tokenType = nft.tokenType,
                quantity = nft.quantity,
                metadata = ZeroNFTMetadata(
                    attributes = nft.metadata.attributes.map { nFTAttribute ->
                        ZeroNFTAttribute(traitType = nFTAttribute.traitType, value = nFTAttribute.value)
                    },
                    name = nft.metadata.name,
                    description = nft.metadata.description,
                )
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
                    transaction.token.decimals,
                    transaction.token.chainId
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

fun ApiWalletTokenInfo.toModel() = ZeroWalletTokenInfo(name, symbol, decimals, address)

fun ApiWalletTokenBalance.toModel() = ZeroWalletTokenBalance(balance)

fun ZeroWalletTokensPaginationParams.toApi() = NextPageParams(itemsCount, tokenName, tokenType, value)
fun ZeroWalletTransactionsPaginationParams.toApi() = TransactionNextPageParams(blockNumber, index)

fun ApiAvaxTokenPrice.toModel() = ZeroAvaxTokenPrice(
    usd = usd,
    marketCap = marketCap,
    volume24h = volume24h,
    change24h = change24h,
    lastUpdatedAt = lastUpdatedAt
)
