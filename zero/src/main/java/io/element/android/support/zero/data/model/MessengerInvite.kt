package io.element.android.support.zero.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MessengerInvite(
    val slug: String,
    val remainingInvites: Int,
    val invitesUsed: Int
) {
    companion object {
        fun empty() = MessengerInvite(slug = "", remainingInvites = 0, invitesUsed = 0)
    }
}
