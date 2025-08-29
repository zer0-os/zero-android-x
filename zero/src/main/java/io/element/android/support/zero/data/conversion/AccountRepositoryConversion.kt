/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.wallet.ZeroWallet
import io.element.android.support.zero.network.model.response.wallet.ApiWallet

fun ApiWallet.toModel() = ZeroWallet(
    id = id,
    isDefault = isDefault,
    publicAddress = publicAddress,
    userId = userId,
    isThirdWeb = isThirdWeb,
    canAuthenticate = canAuthenticate
)
