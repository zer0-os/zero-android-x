package io.element.android.support.zero.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MessengerInvite(
    val slug: String,
    val remainingInvites: Int,
    val invitesUsed: Int,
    val proSubscriptionsCount: Int
) {
    companion object {
        fun empty() = MessengerInvite(slug = "", remainingInvites = 0, invitesUsed = 0, proSubscriptionsCount = 0)
    }
}
