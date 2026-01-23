/*
 * Copyright (c) 2026 Element Creations Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroWalletNFTsResponse(
    val nfts: List<ZeroWalletNFT>,
    val paginationParams: ZeroWalletTokensPaginationParams?
) : Parcelable

@Parcelize
data class ZeroWalletNFT(
    val animationUrl: String?,
    val collectionAddress: String,
    val collectionName: String?,
    val id: String,
    val imageUrl: String?,
    val tokenType: String?,
    val quantity: Int?,
    val metadata: ZeroNFTMetadata?,
) : Parcelable

@Parcelize
data class ZeroNFTMetadata(
    val attributes: List<ZeroNFTAttribute>?,
    val name: String?,
    val description: String?
) : Parcelable

@Parcelize
data class ZeroNFTAttribute(
    val traitType: String,
    val value: String
) : Parcelable

val ZeroWalletNFT.tokenUrl: String
    get() = buildString {
        append("https://zscan.live/token/")
        append(this@tokenUrl.collectionAddress)
        append("/instance/")
        append(this@tokenUrl.id)
    }
