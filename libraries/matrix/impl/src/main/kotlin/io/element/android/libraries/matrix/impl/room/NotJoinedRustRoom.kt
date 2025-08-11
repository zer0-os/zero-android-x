/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.libraries.matrix.impl.room

import androidx.compose.runtime.Immutable
import io.element.android.libraries.core.extensions.runCatchingExceptions
import io.element.android.libraries.matrix.api.core.SessionId
import io.element.android.libraries.matrix.api.room.NotJoinedRoom
import io.element.android.libraries.matrix.api.room.RoomMembershipDetails
import io.element.android.libraries.matrix.api.room.preview.RoomPreviewInfo
import io.element.android.libraries.matrix.impl.room.member.RoomMemberMapper
import kotlinx.coroutines.flow.firstOrNull

@Immutable
class NotJoinedRustRoom(
    private val sessionId: SessionId,
    override val localRoom: RustBaseRoom?,
    override val previewInfo: RoomPreviewInfo,
) : NotJoinedRoom {
    override suspend fun membershipDetails(): Result<RoomMembershipDetails?> = runCatchingExceptions {
        val room = localRoom?.innerRoom ?: return@runCatchingExceptions null
        val (ownMember, senderInfo) = room.memberWithSenderInfo(sessionId.value)
        val apiOwnMember = localRoom.zeroUserRepository?.getUser(ownMember.userId)?.firstOrNull()
        val apiSenderInfo = senderInfo?.let {
            localRoom.zeroUserRepository?.getUser(it.userId)?.firstOrNull()
        }
        RoomMembershipDetails(
            currentUserMember = RoomMemberMapper.map(ownMember)
                .copy(
                    primaryZId = apiOwnMember?.primaryZeroId,
                    isZeroProSubscriber = apiOwnMember?.isZeroProSubscriber ?: false
                ),
            senderMember = senderInfo?.let {
                RoomMemberMapper.map(it).copy(
                    primaryZId = apiSenderInfo?.primaryZeroId,
                    isZeroProSubscriber = apiSenderInfo?.isZeroProSubscriber ?: false
                )
            },
        )
    }

    override fun close() {
        localRoom?.close()
    }
}
