/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.initializer.walletconnect

import android.app.Application
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.presets.AppKitChainsPresets
import com.reown.appkit.utils.EthUtils
import com.reown.util.bytesToHex
import com.reown.util.randomBytes
import timber.log.Timber

internal object WalletConnectInitializer {

    private val appMetaData by lazy {
        Core.Model.AppMetaData(
            name = WalletConnectMetaData.META_DATA_APP_NAME,
            description = WalletConnectMetaData.META_DATA_APP_DESCRIPTION,
            url = WalletConnectMetaData.META_DATA_URL,
            icons = listOf(WalletConnectMetaData.META_DATA_ICONS_URL),
            redirect = WalletConnectMetaData.APP_REDIRECT_URI,
            linkMode = true,
        )
    }

    private val allowedChains by lazy {
        AppKitChainsPresets.ethChains.values.toList()
    }

    fun initialize(application: Application) {
        CoreClient.initialize(
            //projectId = "{project_id_here}",
            projectId = "1971c607a3c38cd80dab88290b95656c",
            connectionType = WalletConnectMetaData.WALLET_CONNECT_CONNECTION_TYPE,
            application = application,
            metaData = appMetaData,
        ) {
            Timber.e(it.throwable)
        }

        AppKit.initialize(
            Modal.Params.Init(core = CoreClient),
            onSuccess = {
                Timber.i("Wallet Connect initialised successfully")
            },
            onError = { error ->
                Timber.e(error.throwable.stackTraceToString())
            }
        )

        setChains()
        //TODO: need to check this if it is required
        setAuthParams()
    }

    private fun setChains() {
        AppKit.setChains(allowedChains)
    }

    private fun setAuthParams() {
        val authParams = Modal.Model.AuthPayloadParams(
            chains = allowedChains.map { it.id },
            domain = "sample.kotlin.modal",
            uri = "https://web3inbox.com/all-apps",
            nonce = randomBytes(12).bytesToHex(),
            statement = "I accept the Terms of Service: https://yourDappDomain.com/",
            methods = EthUtils.ethMethods
        )
        AppKit.setAuthRequestParams(authParams)
    }
}
