/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response.wallet

import kotlinx.serialization.Serializable

@Serializable
data class ApiWalletNFTs(
    val nfts: List<ApiWalletNFT>,
    val nextPageParams: NextPageParams?
)

@Serializable
data class ApiWalletNFT(
    val animationUrl: String?,
    val collectionAddress: String,
    val collectionName: String?,
    val id: String,
    val imageUrl: String?,
    val isUnique: Boolean,
    val tokenType: String?,
    val quantity: Int?,
    val metadata: ApiNFTMetadata,
)

@Serializable
data class ApiNFTMetadata(
    val attributes: List<ApiNFTAttribute>,
    val name: String?,
    val description: String?
)

@Serializable
data class ApiNFTAttribute(
    val traitType: String,
    val value: String
)
