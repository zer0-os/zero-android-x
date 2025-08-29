/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.wallet

data class ZeroWallet(
    val id: String,
    val isDefault: Boolean,
    val publicAddress: String,
    val userId: String? = null,
    val isThirdWeb: Boolean,
    val canAuthenticate: Boolean? = null
) {
    val zScanUrl: String
        get() = buildString {
            append("https://zscan.live/")
            append("address/")
            append(publicAddress)
        }
}
