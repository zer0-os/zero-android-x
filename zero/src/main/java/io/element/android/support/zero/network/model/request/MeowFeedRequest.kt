/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.request

import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class MeowFeedRequest(
    val amount: String
) {
    constructor(amount: Int) : this(
        (BigInteger.valueOf(amount.toLong()) * BigInteger.TEN.pow(18)).toString()
    )
}
