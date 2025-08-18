/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.feed.FeedMedia
import io.element.android.libraries.matrix.api.zero.feed.FeedUserProfileView
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeed
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeedAuthor
import io.element.android.libraries.matrix.api.zero.feed.ZeroFeedAuthorProfileSummary
import io.element.android.support.zero.network.model.response.feed.ApiFeed
import io.element.android.support.zero.network.model.response.feed.ApiFeedMedia
import io.element.android.support.zero.network.model.response.feed.ApiFeedMediaResponse
import io.element.android.support.zero.network.model.response.feed.ApiFeedUserProfileView
import io.element.android.support.zero.network.model.response.feed.FeedUser
import io.element.android.support.zero.network.model.response.feed.FeedUserProfileSummary
import io.element.android.support.zero.network.model.response.feed.Meow
import io.element.android.support.zero.network.model.response.feed.PostsMeowsSummary
import io.element.android.support.zero.network.model.response.feed.Reply
import io.element.android.support.zero.network.model.response.feed.ReplyToFeed

fun ApiFeed.toModel() = ZeroFeed(
    id = id,
    userId = userId,
    zid = zid,
    createdAt = createdAt,
    updatedAt = updatedAt,
    signedMessage = signedMessage,
    unsignedMessage = unsignedMessage,
    text = text,
    walletAddress = walletAddress,
    worldZid = worldZid,
    imageUrl = imageUrl,
    arweaveId = arweaveId,
    replyTo = replyTo,
    conversationId = conversationId,
    user = user.toModel(),
    postsMeowsSummary = postsMeowsSummary?.toModel(),
    meows = meows?.map { it.toModel() },
    replies = replies?.map { it.toModel() },
    replyToPost = replyToPost?.toModel(),
    media = media?.toModel(),
    userProfileView = userProfileView?.toModel()
)

fun FeedUser.toModel() = ZeroFeedAuthor(
    id = id,
    profileId = profileId,
    handle = handle,
    profileSummary = profileSummary.toModel()
)

fun FeedUserProfileSummary.toModel() = ZeroFeedAuthorProfileSummary(
    id = id,
    firstName = firstName,
    lastName = lastName,
    primaryEmail = primaryEmail,
    profileImage = profileImage
)

fun PostsMeowsSummary.toModel() = io.element.android.libraries.matrix.api.zero.feed.PostsMeowsSummary(
    postId = postId,
    totalMeowAmount = totalMeowAmount
)

fun Meow.toModel() = io.element.android.libraries.matrix.api.zero.feed.Meow(
    id = id,
    postId = postId,
    amount = amount,
    createdAt = createdAt,
    userId = userId
)

fun Reply.toModel() = io.element.android.libraries.matrix.api.zero.feed.Reply(
    id = id,
    replyTo = replyTo
)

fun ReplyToFeed.toModel() = io.element.android.libraries.matrix.api.zero.feed.ReplyToFeed(
    id = id,
    userId = userId,
    zid = zid,
    createdAt = createdAt,
    text = text,
    arweaveId = arweaveId,
    user = user.toModel()
)

fun ApiFeedMedia.toModel() = FeedMedia(
    id = id,
    width = width,
    height = height,
    mimeType = mimeType,
    url = null
)

fun ApiFeedMediaResponse.toModel() = FeedMedia(
    id = media.id,
    width = media.width,
    height = media.height,
    mimeType = media.mimeType,
    url = signedUrl
)

fun ApiFeedUserProfileView.toModel() = FeedUserProfileView(
    userId = userId,
    primaryZid = primaryZid,
    firstName = firstName,
    profileImage = profileImage,
    publicAddress = publicAddress,
    followersCount = followersCount,
    followingCount = followingCount,
    isZeroProSubscriber = isZeroProSubscriber
)
