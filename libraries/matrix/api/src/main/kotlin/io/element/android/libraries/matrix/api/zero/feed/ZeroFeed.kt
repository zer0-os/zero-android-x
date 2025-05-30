/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.api.zero.feed

import android.os.Parcelable
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.user.walletAddress
import io.element.android.libraries.matrix.api.zero.ZeroWalletUtil
import io.element.android.libraries.matrix.api.zero.metadata.ZeroLinkPreview
import kotlinx.parcelize.Parcelize
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
    val replyToPost: ReplyToFeed? = null,
    val media: FeedMedia? = null,
    val linkMetaData: ZeroLinkPreview? = null,
    val userProfileView: FeedUserProfileView? = null
) : Parcelable {

    private fun updatedAtDate(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        return try {
            val instant = Instant.from(formatter.parse(updatedAt))
            instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (e: DateTimeParseException) {
            LocalDateTime.now()
        }
    }

    fun updatedAtTimeAgo(): String {
        val now = LocalDateTime.now()
        val updatedAt = updatedAtDate()
        val duration = Duration.between(updatedAt, now)
        val seconds = duration.seconds.toInt()

        return when {
            seconds <= 0 -> "Just now"
            seconds < 60 -> "${seconds}s ago"
            seconds < 3600 -> "${seconds / 60}m ago"
            seconds < 86400 -> "${seconds / 3600}h ago"
            else -> updatedAt.format(DateTimeFormatter.ofPattern("MMM dd"))
        }
    }

    fun completeDateAndTime(): String {
        val updatedAt = updatedAtDate()
        return updatedAt.format(DateTimeFormatter.ofPattern("h:mm a • MMM d, yyyy"))
    }

    companion object {
        val placeholder = ZeroFeed(
            id = UUID.randomUUID().toString(),
            userId = UUID.randomUUID().toString(),
            zid = UUID.randomUUID().toString(),
            createdAt = "",
            updatedAt = "",
            signedMessage = "placeholder signed message",
            unsignedMessage = "placeholder un-signed message",
            text = "Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text Placeholder feed text...",
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
            ),
            media = FeedMedia.placeholder
        )
    }
}

fun ZeroFeed.totalMeowCount(decimal: Int): String {
    val value = postsMeowsSummary?.totalMeowAmount ?: "0"
    return try {
        val bigIntValue = BigInteger(value)
        val divisor = BigInteger.TEN.pow(decimal)

        (bigIntValue / divisor).toString()
    } catch (e: NumberFormatException) {
        value
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
) : Parcelable {
    val name
        get() = "$firstName $lastName".trim()
}

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

@Parcelize
data class FeedMedia(
    val id: String,
    val width: Float,
    val height: Float,
    val mimeType: String?,
    val url: String?
) : Parcelable {
    companion object {
        val placeholder: FeedMedia = FeedMedia("placeholder_id", 1080f, 720f, "image/png", "")
    }
}

@Parcelize
data class FeedUserProfileView(
    val userId: String,
    val primaryZid: String?,
    val firstName: String,
    val profileImage: String?,
    val publicAddress: String?,
    val followersCount: String?,
    val followingCount: String?,
): Parcelable {
    companion object {
        val placeholder: FeedUserProfileView = FeedUserProfileView(
            userId = "placeholder_id",
            primaryZid = "0://placeholder_id",
            firstName = "placeholder name",
            profileImage = null,
            publicAddress = null,
            followersCount = "0",
            followingCount = "0")
    }
}

val FeedUserProfileView.primaryZIdOrWalletAddress
    get() = primaryZid ?: publicAddress

val FeedUserProfileView.zIdOrWalletAddressDisplay
    get() = primaryZid ?: ZeroWalletUtil.walletAddressDisplayText(publicAddress)

val FeedMedia.aspectRatio
    get() = width.div(height)

val FeedMedia.isVideo
    get() = mimeType?.contains("video") == true

fun ZeroFeedAuthor.toZeroProfile(userZId: String) = FeedUserProfileView(
    userId = id,
    primaryZid = userZId,
    firstName = profileSummary.firstName,
    profileImage = profileSummary.profileImage,
    publicAddress = null,
    followersCount = null,
    followingCount = null
)

fun MatrixUser.toZeroProfile() = FeedUserProfileView(
    userId = userId.extractedDisplayName,
    primaryZid = primaryZeroId.orEmpty(),
    firstName = displayName.orEmpty(),
    profileImage = avatarUrl,
    publicAddress = walletAddress,
    followersCount = null,
    followingCount = null
)

val ZeroFeed.userProfile
    get() = userProfileView ?: user.toZeroProfile(zid)
