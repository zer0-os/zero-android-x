package io.element.android.libraries.matrix.api.zero.user

data class ZeroUser(
    val id: String,
    val matrixId: String,
    val name: String,
    val avatarUrl: String? = null,
    val primaryZeroId: String? = null
)
