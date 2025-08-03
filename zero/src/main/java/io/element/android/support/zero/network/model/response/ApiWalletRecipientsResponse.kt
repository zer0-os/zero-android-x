/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiWalletRecipientsResponse(
    val recipients: List<ApiWalletRecipient>
)

@Serializable
data class ApiWalletRecipient(
    val userId: String,
    val matrixId: String,
    val publicAddress: String,
    val name: String? = null,
    val profileImage: String? = null,
    val primaryZid: String? = null
)
