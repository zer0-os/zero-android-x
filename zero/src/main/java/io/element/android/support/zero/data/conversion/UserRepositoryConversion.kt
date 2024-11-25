package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import io.element.android.support.zero.network.model.response.ApiUser

internal fun ApiUser.toModel() = ZeroUser(
    id = id,
    matrixId = matrixId ?: "",
    name = name ?: "",
    avatarUrl = avatar,
    primaryZeroId = primaryZID
)
