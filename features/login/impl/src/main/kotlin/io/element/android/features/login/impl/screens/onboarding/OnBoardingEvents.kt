/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.onboarding

import android.app.Activity

sealed interface OnBoardingEvents {
    data class OnSignIn(
        val defaultAccountProvider: String
    ) : OnBoardingEvents

    data object OnVersionClick : OnBoardingEvents
    data object ClearError : OnBoardingEvents

    data class OnLoginWithX(val activity: Activity) : OnBoardingEvents
    data class OnLoginWithEpic(val activity: Activity) : OnBoardingEvents
}
