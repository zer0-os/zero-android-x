package io.element.android.libraries.matrix.api.zero.user

data class ZeroUser(
    val id: String,
    val matrixId: String,
    val name: String,
    val avatarUrl: String? = null,
    val primaryZeroId: String? = null
)

fun ZeroUser.nameIsMatrixHex(): Boolean {
    val regex = "^[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+$".toRegex()
    return name.matches(regex)
}

fun ZeroUser.primaryZIdCoreChannel(): String? {
    return primaryZeroId?.split(".")?.firstOrNull()
}
