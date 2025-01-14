/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.initializer.walletconnect

import com.reown.android.relay.ConnectionType
import io.element.android.x.BuildConfig.APPLICATION_ID

internal object WalletConnectMetaData {

    const val META_DATA_APP_NAME = "ZERO Messenger"
    const val META_DATA_APP_DESCRIPTION = ""
    const val META_DATA_URL = "https://zos.zero.tech/"
    const val META_DATA_ICONS_URL = "https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"
    const val APP_REDIRECT_URI = "$APPLICATION_ID://request"

    val WALLET_CONNECT_CONNECTION_TYPE = ConnectionType.AUTOMATIC
}
