package io.element.android.libraries.matrix.impl.conversion

import io.element.android.libraries.matrix.api.zero.invite.ZeroMessengerInvite
import io.element.android.support.zero.data.model.MessengerInvite

fun MessengerInvite.map() =
    ZeroMessengerInvite(
        slug = this.slug,
        remainingInvites = this.remainingInvites,
        invitesUsed = this.invitesUsed,
        proSubscriptionsCount = proSubscriptionsCount
    )
