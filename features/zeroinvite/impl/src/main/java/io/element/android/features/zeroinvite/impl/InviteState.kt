package io.element.android.features.zeroinvite.impl

import androidx.compose.runtime.Immutable
import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite

@Immutable
data class InviteState(
    val messengerInvite: ZeroMessengerInvite = ZeroMessengerInvite.empty()
)
