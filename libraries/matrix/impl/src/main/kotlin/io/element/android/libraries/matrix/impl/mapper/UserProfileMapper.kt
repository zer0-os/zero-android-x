/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.mapper

import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import org.matrix.rustcomponents.sdk.UserProfile

object UserProfileMapper {
    fun map(userProfile: UserProfile, zeroUser: ZeroUser? = null): MatrixUser =
        MatrixUser(
            userId = UserId(userProfile.userId),
            displayName = userProfile.displayName,
            avatarUrl = userProfile.avatarUrl,
            primaryZeroId = zeroUser?.primaryZeroId,
            zeroWalletAddress = zeroUser?.walletAddress,
            isZeroProSubscriber = zeroUser?.isZeroProSubscriber ?: false,
            primaryWalletAddress = null,
            thirdWebWalletAddress = null,
        )
}
