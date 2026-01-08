package io.element.android.support.zero.config

import io.element.android.support.zero.BuildConfig

object ZeroConfig {
    val environment: EnvironmentConfig = EnvironmentConfig.Production

    const val ARWEAVE_BASE_URL = "https://of2ub4a2ai55lgpqj5z7so7j7v6uwjcruh6cdm3ojgnhqngahkwa.arweave.net/"

    const val ZSCAN_LIVE_URL = "https://zscan.live/"
    const val APPLICATION_IDENTIFIER = "com.zero.android.messenger"
}

sealed class EnvironmentConfig(
    val appName: String,
    val matrixHomeServerUrl: String,
    val matrixPushGateway: String,
    val zosUrl: String,
    val walletConnectKey: String,
) {
    val apiUrl = "${zosUrl}api/"

    data object Development : EnvironmentConfig(
        appName = "ZERO Dev",
        matrixHomeServerUrl = "https://synapse-dev.zero.tech",
        matrixPushGateway = "https://push-gateway-dev.zero.tech/_matrix/push/v1/notify",
        zosUrl = "https://zos-api-development-fb2c513ffa60.herokuapp.com/",
        walletConnectKey = BuildConfig.WALLET_CONNECT_PROJECT_ID
    )

    data object Production : EnvironmentConfig(
        appName = "ZERO",
        matrixHomeServerUrl = "https://synapse.zero.tech",
        matrixPushGateway = "https://push-gateway.zero.tech/_matrix/push/v1/notify",
        zosUrl = "https://zosapi.zero.tech/",
        walletConnectKey = BuildConfig.WALLET_CONNECT_PROJECT_ID
    )
}
