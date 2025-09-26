/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ZeroWalletRecipient(
    val userId: String,
    val matrixId: String,
    val publicAddress: String,
    val name: String? = null,
    val profileImage: String? = null,
    val primaryZid: String? = null
): Parcelable {

    companion object {
        fun fromAddress(walletAddress: String): ZeroWalletRecipient {
            val customId = UUID.randomUUID().toString()
            return ZeroWalletRecipient(
                userId = customId,
                matrixId = customId,
                publicAddress = walletAddress,
                name = "External Wallet",
                profileImage = null,
                primaryZid = null
            )
        }
    }
}

val ZeroWalletRecipient.displayName: String
    get() = run {
        val postFix = primaryZid ?: publicAddress
        if (!name.isNullOrBlank()) {
            return@run name
        } else {
            return@run postFix
        }
    }
