/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import dev.zacsweers.metro.Inject
import androidx.core.net.toUri

@Inject
class SocialAuthHelper {
    private val redirectUri: String = "com.zero.android.messenger://oauth-callback"

    fun loginWithX(activity: Activity) {
        val encodedRedirect = Uri.encode(redirectUri)
        val authUrl =
            "https://zosapi.zero.tech/api/oauth/x/initiate?returnUrl=$encodedRedirect"
        launchBrowser(activity, authUrl)
    }

    fun loginWithEpicGames(activity: Activity) {
        val encodedRedirect = Uri.encode(redirectUri)
        val authUrl =
            "https://zosapi.zero.tech/api/oauth/epic-games/initiate?returnUrl=$encodedRedirect"
        launchBrowser(activity, authUrl)
    }

    private fun launchBrowser(activity: Activity, authUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
        activity.startActivity(intent)
    }
}

object SocialAuthResultHandler {
    private var listener: ((Result<String>) -> Unit)? = null

    fun setListener(callback: (Result<String>) -> Unit) {
        listener = callback
    }

    fun onMainActivityNewIntent(intent: Intent) {
        val uri: Uri = intent.data ?: return
        val scheme = uri.scheme
        val host = uri.host
        if (scheme == "com.zero.android.messenger" && host == "oauth-callback") {
            val token = uri.getQueryParameter("sessionEstablishmentToken")
            if (token != null) {
                onTokenReceived(token)
            } else {
                onError("No auth token in callback")
            }
        }
    }

    private fun onTokenReceived(token: String) {
        listener?.invoke(Result.success(token))
        listener = null
    }

    private fun onError(error: String) {
        listener?.invoke(Result.failure(Exception(error)))
        listener = null
    }

}
