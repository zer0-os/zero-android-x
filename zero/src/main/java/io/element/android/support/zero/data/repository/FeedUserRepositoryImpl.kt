/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

import io.element.android.support.zero.network.model.response.ApiFeedUserProfileView
import io.element.android.support.zero.network.service.ZeroFeedUserService

class FeedUserRepositoryImpl(
    private val feedUserService: ZeroFeedUserService
) : FeedUserRepository {

    override suspend fun fetchUserProfile(userZId: String): ApiFeedUserProfileView? {
        return runCatching {
            feedUserService.fetchUserProfile(userZId)
        }.getOrNull()
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
