package io.element.android.libraries.matrix.api.common

object MatrixSessionCommon {
    private lateinit var homeServerPostfix: String

    fun setHomeServerUrl(url: String) {
        homeServerPostfix = url
    }

    fun getHomeServerPostfix() = homeServerPostfix
}
