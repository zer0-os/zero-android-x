/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding

sealed interface ExtendedOnboardingEvents {
    data class SetForgotPasswordEmail(val email: String) : ExtendedOnboardingEvents
    data object Submit : ExtendedOnboardingEvents
    data object ClearError : ExtendedOnboardingEvents
}
