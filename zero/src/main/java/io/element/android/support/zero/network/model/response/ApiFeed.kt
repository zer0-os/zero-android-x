/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Generic Feed response classes here
@Serializable
data class ApiFeeds(
    @SerialName("posts") val feeds: List<ApiFeed>
)

@Serializable
data class ApiFeedDetails(
    @SerialName("post") val feed: ApiFeed
)

@Serializable
data class ApiFeedReplies(
    @SerialName("replies") val feedReplies: List<ApiFeed>
)

// Actual Feed response
@Serializable
data class ApiFeed(
    val id: String,
    val userId: String,
    val zid: String,
    val createdAt: String,
    val updatedAt: String,
    val signedMessage: String,
    val unsignedMessage: String,
    val text: String,
    val walletAddress: String,
    val worldZid: String? = null,
    val imageUrl: String? = null,
    val arweaveId: String,
    val replyTo: String? = null,
    val conversationId: String? = null,
    val user: FeedUser,
    val postsMeowsSummary: PostsMeowsSummary? = null,
    val meows: List<Meow>? = null,
    val replies: List<Reply>? = null,
    val replyToPost: ReplyToFeed? = null,
    val mediaId: String? = null,
    val media: ApiFeedMedia? = null,
    val userProfileView: ApiFeedUserProfileView? = null,
)

@Serializable
data class FeedUser(
    val id: String,
    val profileId: String,
    val handle: String,
    val profileSummary: FeedUserProfileSummary
)

@Serializable
data class FeedUserProfileSummary(
    val id: String,
    val firstName: String,
    val lastName: String,
    val primaryEmail: String? = null,
    val profileImage: String? = null
)

@Serializable
data class PostsMeowsSummary(
    val postId: String,
    val totalMeowAmount: String
)

@Serializable
data class Meow(
    val id: String,
    val postId: String,
    val amount: String,
    val createdAt: String? = null,
    val userId: String? = null
)

@Serializable
data class Reply(
    val id: String,
    val replyTo: String
)

@Serializable
data class ReplyToFeed(
    val id: String,
    val userId: String,
    val zid: String,
    val createdAt: String,
    val text: String,
    val arweaveId: String,
    val user: FeedUser
)

@Serializable
data class ApiFeedMedia(
    val id: String,
    val mimeType: String?,
    val width: Float,
    val height: Float,
    val fileSize: String?,
)

@Serializable
data class ApiFeedUserProfileView(
    val userId: String,
    val createdAt: String? = null,
    val primaryZid: String? = null,
    val firstName: String,
    val profileImage: String? = null,
    val publicAddress: String? = null,
    val followersCount: String? = null,
    val followingCount: String? = null,
)
