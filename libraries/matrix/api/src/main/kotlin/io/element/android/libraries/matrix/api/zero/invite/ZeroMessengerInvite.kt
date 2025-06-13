package io.element.android.libraries.matrix.api.zero.invite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ZeroMessengerInvite(
    val slug: String,
    val remainingInvites: Int,
    val invitesUsed: Int
): Parcelable {
    companion object {
        fun empty() = ZeroMessengerInvite(slug = "XXXXXX", remainingInvites = 2, invitesUsed = 0)
    }
}
