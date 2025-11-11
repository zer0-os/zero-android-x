/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.walletconnect

import com.reown.appkit.client.AppKit
import com.reown.appkit.client.models.request.Request

object WalletConnectService {

    private const val WALLET_CONNECT_PERSONAL_SIGN_MESSAGE = "Sign with your wallet to log in to ZERO?"
    private const val WALLET_CONNECT_METHOD_ETH_PERSONAL_SIGN = "personal_sign"

    fun getCurrentWalletAddress(): String? {
        return AppKit.getAccount()?.address
    }

    fun requestPersonalSign(
        message: String = WALLET_CONNECT_PERSONAL_SIGN_MESSAGE,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        AppKit.getAccount()?.let { account ->
            val address = account.address
            val params = getPersonalSignBody(message, address)

            AppKit.request(
                request = Request(
                    method = WALLET_CONNECT_METHOD_ETH_PERSONAL_SIGN,
                    params = params
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    private fun getPersonalSignBody(message: String, account: String): String {
        val msg =
            message.encodeToByteArray().joinToString(separator = "", prefix = "0x") { eachByte ->
                "%02x".format(eachByte)
            }
        return "[\"$msg\", \"$account\"]"
    }
}
