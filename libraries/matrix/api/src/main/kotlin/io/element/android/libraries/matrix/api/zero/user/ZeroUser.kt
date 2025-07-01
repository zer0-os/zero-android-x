package io.element.android.libraries.matrix.api.zero.user

import io.element.android.libraries.matrix.api.zero.ZeroWalletUtil

data class ZeroUser(
    val id: String,
    val matrixId: String,
    val name: String,
    val avatarUrl: String? = null,
    val primaryZeroId: String? = null,
    val primaryWalletAddress: String? = null,
    val thirdWebWalletAddress: String? = null,
    val isZeroProSubscriber: Boolean = false
)

val ZeroUser.walletAddress
    get() = primaryWalletAddress ?: thirdWebWalletAddress

val ZeroUser.primaryZIdOrWalletAddress
    get() = primaryZeroId ?: walletAddress

val ZeroUser.zIdOrWalletAddressDisplay
    get() = primaryZeroId ?: ZeroWalletUtil.walletAddressDisplayText(walletAddress)

fun ZeroUser.nameIsMatrixHex(): Boolean {
    val regex = "^[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+$".toRegex()
    return name.matches(regex)
}

fun ZeroUser.primaryZIdCoreChannel(): String? {
    return primaryZeroId?.split(".")?.firstOrNull()
}
