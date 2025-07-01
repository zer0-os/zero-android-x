package io.element.android.support.zero.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiInvite(
    val id: String,
    val slug: String,
    val inviteCount: String? = null,
    val invitesUsed: Int? = null,
    val maxInvitesPerUser: Int? = null,
    val proSubscriptions: Int? = null
) {
    val remainingInvites: Int
        get() = inviteCount?.toIntOrNull()
            ?: maxOf(((maxInvitesPerUser ?: 0) - (invitesUsed ?: 0)), 0)

    val maxInvites: Int
        get() = maxInvitesPerUser ?: 0
}
