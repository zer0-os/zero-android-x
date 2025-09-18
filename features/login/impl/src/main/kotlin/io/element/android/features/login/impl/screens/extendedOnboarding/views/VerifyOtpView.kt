/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding.views

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingState
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingStateProvider
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@Composable
fun VerifyOtpView(
    modifier: Modifier = Modifier,
    state: ExtendedOnboardingState,
    onBackClick: () -> Unit
) {
    Scaffold(modifier = modifier) { padding ->
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {
            ZeroOnboardingPage(
                showBackButton = true,
                onBackClick = onBackClick,
                content = {
                    VerifyOtpForm(state, scrollState)
                },
                footer = {

                }
            )
        }
    }
}

@Composable
private fun VerifyOtpForm(state: ExtendedOnboardingState, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnboardingScreenHeader(
            title = "Continue with Email",
            subTitle = "You verification code has been sent to your Email ID.",
            titleColor = ElementTheme.colors.textPrimary
        )
    }
}

@PreviewsDayNight
@Composable
internal fun VerifyOtpViewPreview(@PreviewParameter(ExtendedOnboardingStateProvider ::class) state: ExtendedOnboardingState) = ElementPreview {
    VerifyOtpView(
        state = state,
        onBackClick = {},
    )
}
