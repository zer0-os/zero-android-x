package io.element.android.libraries.matrix.impl.common

internal object MatrixSessionCommon {
    private lateinit var homeServerPostfix: String

    fun setHomeServerUrl(url: String) {
        homeServerPostfix = url
    }

    fun getHomeServerPostfix() = homeServerPostfix
}
