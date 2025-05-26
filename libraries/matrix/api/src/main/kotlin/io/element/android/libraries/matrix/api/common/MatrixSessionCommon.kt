package io.element.android.libraries.matrix.api.common

object MatrixSessionCommon {
    private lateinit var homeServerPostfix: String

    fun setHomeServerUrl(url: String) {
        homeServerPostfix = url
    }

    fun getHomeServerPostfix() = homeServerPostfix

    fun matrixUserIdFromIdHex(userIdHex: String): String {
        return if (userIdHex.contains(getHomeServerPostfix())) userIdHex
        else {
            buildString {
                append("@")
                append(userIdHex)
                append(":")
                append(getHomeServerPostfix())
            }
        }
    }
}
