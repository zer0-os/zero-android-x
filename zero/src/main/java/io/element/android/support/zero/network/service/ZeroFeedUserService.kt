/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.service

import io.element.android.support.zero.network.model.response.ApiFeedUserFollowingStatus
import io.element.android.support.zero.network.model.response.ApiFeedUserProfileView
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ZeroFeedUserService {

    @GET(value = "api/v2/users/profile/zid/{user_zid}")
    suspend fun fetchUserProfile(
        @Path("user_zid") userZId: String,
    ): ApiFeedUserProfileView

    @GET(value = "api/v2/users/profile/address/{address}")
    suspend fun fetchUserProfileByAddress(
        @Path("address") userZId: String,
    ): ApiFeedUserProfileView

    @GET(value = "api/v2/user-follows/{user_id}/status")
    suspend fun fetchUserFollowingStatus(
        @Path("user_id") userId: String,
    ): ApiFeedUserFollowingStatus

    @POST(value = "api/v2/user-follows/{user_id}")
    suspend fun followUser(
        @Path("user_id") userId: String,
    ): Response<ResponseBody>

    @DELETE(value = "api/v2/user-follows/{user_id}")
    suspend fun unFollowUser(
        @Path("user_id") userId: String,
    ): Response<ResponseBody>
}
