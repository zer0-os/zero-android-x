/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.support.zero.data.repository

class ZeroCoreRepository(
    val account: AccountRepository,
    val auth: AuthRepository,
    val channel: ChannelRepository,
    val conversation: ConversationRepository,
    val feed: FeedRepository,
    val invite: InviteRepository,
    val rewards: RewardsRepository,
    val user: UserRepository
)
