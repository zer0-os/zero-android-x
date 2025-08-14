package io.element.android.support.zero.data.conversion

import io.element.android.support.zero.data.model.MessengerInvite
import io.element.android.support.zero.network.model.response.auth.ApiInvite

internal fun ApiInvite.toModel() = MessengerInvite(
    slug = slug,
    remainingInvites = remainingInvites,
    invitesUsed = invitesUsed ?: 0,
    proSubscriptionsCount = proSubscriptions ?: 0
)
