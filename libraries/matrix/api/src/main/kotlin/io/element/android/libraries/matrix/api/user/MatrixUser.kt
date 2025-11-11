/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.user

import android.os.Parcelable
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MatrixUser(
    val userId: UserId,
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val primaryZeroId: String? = null,
    val primaryWalletAddress: String? = null,
    val zeroWalletAddress: String? = null,
    val thirdWebWalletAddress: String? = null,
    val isZeroProSubscriber: Boolean = false
) : Parcelable

val MatrixUser.walletAddress
    get() = zeroWalletAddress

val MatrixUser.primaryZIdOrWalletAddress
    get() = primaryZeroId ?: walletAddress

val MatrixUser.zIdOrWalletAddressDisplay
    get() = primaryZeroId ?: ZeroWalletUtil.walletAddressDisplayText(walletAddress)
