/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding

import io.element.android.libraries.architecture.AsyncAction

data class ExtendedOnboardingState(
    val forgotPasswordEmail: String,
    val forgotPasswordAction: AsyncAction<Unit>,
    val eventSink: (ExtendedOnboardingEvents) -> Unit
) {
    val submitEnabled: Boolean
        get() = forgotPasswordAction !is AsyncAction.Failure &&
            forgotPasswordEmail.isNotBlank()
}
