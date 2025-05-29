package io.element.android.support.zero.data.conversion

import io.element.android.libraries.matrix.api.zero.user.ZeroUser
import io.element.android.support.zero.network.model.response.ApiUser

internal fun ApiUser.toModel() = ZeroUser(
    id = id,
    matrixId = matrixId.orEmpty(),
    name = name.orEmpty(),
    avatarUrl = avatar,
    primaryZeroId = primaryZID,
    primaryWalletAddress = primaryWalletAddress,
    thirdWebWalletAddress = thirdWebWallet?.publicAddress
)
