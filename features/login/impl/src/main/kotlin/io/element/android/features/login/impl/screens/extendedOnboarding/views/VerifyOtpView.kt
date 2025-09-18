/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.extendedOnboarding.views

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingEvents
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingState
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingStateProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.common.ui.OtpInputField
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_8X
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@Composable
fun VerifyOtpView(
    modifier: Modifier = Modifier,
    state: ExtendedOnboardingState,
    onBackClick: () -> Unit
) {
    Scaffold(modifier = modifier) { padding ->
        val scrollState = rememberScrollState()
        val otpValue = remember { mutableStateOf("") }

        fun canSubmitOtp(): Boolean {
            return otpValue.value.isNotBlank() && otpValue.value.length == 6
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ZeroOnboardingPage(
                showBackButton = true,
                onBackClick = onBackClick,
                content = {
                    VerifyOtpForm(otpValue, scrollState) {
                        state.eventSink(ExtendedOnboardingEvents.SubmitSSO(otpValue.value))
                    }
                },
                footer = {
                    VerifyOtpFooter(
                        modifier = Modifier
                            .imePadding()
                            .padding(24.dp),
                        canSubmit = canSubmitOtp(),
                        onSubmit = {
                            state.eventSink(ExtendedOnboardingEvents.SubmitSSO(otpValue.value))
                        },
                        onResendOtp = {
                            state.eventSink(ExtendedOnboardingEvents.ResendOTP)
                        }
                    )
                }
            )

            if (state.actionState is AsyncAction.Loading) {
                ProgressDialog()
            }

            if (state.actionState is AsyncAction.Failure) {
                ExtendedViewErrorDialog(
                    error = state.actionState.error,
                    onDismiss = { state.eventSink(ExtendedOnboardingEvents.ClearError) }
                )
            }
        }
    }
}

@Composable
private fun VerifyOtpForm(
    otpValue: MutableState<String>,
    scrollState: ScrollState,
    onSubmit: () -> Unit
) {
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

        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        Text(
            text = "Enter OTP",
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        OtpInputField(
            otp = otpValue,
            count = 6,
            otpTextType = KeyboardType.Number,
            otpBoxModifier = Modifier
                .width(50.dp)
                .height(65.dp)
                .background(
                    color = ElementTheme.colors.bgCanvasDefaultLevel1,
                    shape = RoundedCornerShape(11.dp)
                ),
            textColor = ElementTheme.colors.textPrimary,
            onSubmit = onSubmit
        )
    }
}

@Composable
fun VerifyOtpFooter(
    modifier: Modifier = Modifier,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onResendOtp: () -> Unit
) {
    Column(
        modifier = modifier.navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZeroPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Login",
            enabled = canSubmit,
            onClick = onSubmit
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        OtpTimerView(onResendClick = onResendOtp)
    }
}

@PreviewsDayNight
@Composable
internal fun VerifyOtpViewPreview(@PreviewParameter(ExtendedOnboardingStateProvider::class) state: ExtendedOnboardingState) = ElementPreview {
    VerifyOtpView(
        state = state,
        onBackClick = {},
    )
}
