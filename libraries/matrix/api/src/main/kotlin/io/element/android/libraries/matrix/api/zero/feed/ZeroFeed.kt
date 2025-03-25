/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.feed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ZeroFeed(
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
    val user: ZeroFeedAuthor,
    val postsMeowsSummary: PostsMeowsSummary? = null,
    val meows: List<Meow>? = null,
    val replies: List<Reply>? = null,
    val replyToPost: ReplyToFeed? = null
) : Parcelable {

    companion object {
        val placeholder = ZeroFeed(
            id = UUID.randomUUID().toString(),
            userId = UUID.randomUUID().toString(),
            zid = UUID.randomUUID().toString(),
            createdAt = "",
            updatedAt = "",
            signedMessage = "placeholder signed message",
            unsignedMessage = "placeholder un-signed message",
            text = "Placeholder feed text",
            walletAddress = UUID.randomUUID().toString(),
            arweaveId = UUID.randomUUID().toString(),
            user = ZeroFeedAuthor(
                id = UUID.randomUUID().toString(),
                profileId = UUID.randomUUID().toString(),
                handle = UUID.randomUUID().toString(),
                profileSummary = ZeroFeedAuthorProfileSummary(
                    id = UUID.randomUUID().toString(),
                    firstName = "placeholder first name",
                    lastName = "placeholder last name"
                )
            )
        )
    }
}

@Parcelize
data class ZeroFeedAuthor(
    val id: String,
    val profileId: String,
    val handle: String,
    val profileSummary: ZeroFeedAuthorProfileSummary
) : Parcelable

@Parcelize
data class ZeroFeedAuthorProfileSummary(
    val id: String,
    val firstName: String,
    val lastName: String,
    val primaryEmail: String? = null,
    val profileImage: String? = null
) : Parcelable

@Parcelize
data class PostsMeowsSummary(
    val postId: String,
    val totalMeowAmount: String
) : Parcelable

@Parcelize
data class Meow(
    val id: String,
    val postId: String,
    val amount: String,
    val createdAt: String? = null,
    val userId: String? = null
) : Parcelable

@Parcelize
data class Reply(
    val id: String,
    val replyTo: String
) : Parcelable

@Parcelize
data class ReplyToFeed(
    val id: String,
    val userId: String,
    val zid: String,
    val createdAt: String,
    val text: String,
    val arweaveId: String,
    val user: ZeroFeedAuthor
) : Parcelable
