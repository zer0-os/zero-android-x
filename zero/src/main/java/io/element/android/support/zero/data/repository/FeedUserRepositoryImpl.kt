/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.libraries.core.extensions.replacePrefix
import io.element.android.support.zero.common.ZERO_CHANNEL_PREFIX
import io.element.android.support.zero.common.ZERO_WALLET_ADDRESS_PREFIX
import io.element.android.support.zero.network.model.response.ApiFeedUserProfileView
import io.element.android.support.zero.network.service.ZeroFeedUserService

class FeedUserRepositoryImpl(
    private val feedUserService: ZeroFeedUserService
) : FeedUserRepository {

    override suspend fun fetchUserProfile(key: String): ApiFeedUserProfileView? {
        return runCatching {
            if (isKeyAWalletAddress(key)) {
                feedUserService.fetchUserProfileByAddress(key)
            } else {
                val cleanedUserZId = key.replacePrefix(ZERO_CHANNEL_PREFIX, "")
                feedUserService.fetchUserProfile(cleanedUserZId)
            }
        }.getOrNull()
    }

    private fun isKeyAWalletAddress(key: String): Boolean {
        return key.startsWith(ZERO_WALLET_ADDRESS_PREFIX)
    }

    override suspend fun fetchUserFollowingStatus(userId: String): Boolean {
        return runCatching {
            feedUserService.fetchUserFollowingStatus(userId).isFollowing
        }.getOrDefault(false)
    }

    override suspend fun followUser(userId: String): Boolean {
        return runCatching {
            feedUserService.followUser(userId).isSuccessful
        }.getOrDefault(false)
    }

    override suspend fun unFollowUser(userId: String): Boolean {
        return runCatching {
            feedUserService.unFollowUser(userId).isSuccessful
        }.getOrDefault(false)
    }
}
