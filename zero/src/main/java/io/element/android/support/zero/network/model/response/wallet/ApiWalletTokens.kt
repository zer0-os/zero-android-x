/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response.wallet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiWalletTokens(
    val tokens: List<ApiWalletToken>,
    val nextPageParams: NextPageParams?
)

@Serializable
data class ApiWalletToken(
    val tokenAddress: String,
    val symbol: String,
    val name: String,
    val amount: String,
    val logo: String? = null,
    val decimals: Int,
    val chainId: Int
)

@Serializable
data class NextPageParams(
    @SerialName("items_count")
    val itemsCount: Int,
    @SerialName("token_name")
    val tokenName: String? = null,
    @SerialName("token_type")
    val tokenType: String? = null,
    val value: Int
)

fun NextPageParams.toQueryMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["items_count"] = this.itemsCount
    tokenName?.let { map["token_name"] = it }
    tokenType?.let { map["token_type"] = it }
    map["value"] = this.value
    return map
}
