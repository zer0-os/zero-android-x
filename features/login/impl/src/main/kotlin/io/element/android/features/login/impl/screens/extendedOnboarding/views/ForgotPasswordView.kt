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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingEvents
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingState
import io.element.android.features.login.impl.screens.extendedOnboarding.ExtendedOnboardingStateProvider
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_8X
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@Composable
fun ForgotPasswordView(
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
                    when (state.actionState) {
                        is AsyncAction.Success -> ForgotPasswordSuccessView(state.forgotPasswordEmail)
                        else -> ForgotPasswordForm(state, scrollState)
                    }
                },
                footer = {
                    when (state.actionState) {
                        is AsyncAction.Success -> {
                            ZeroPrimaryButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(24.dp),
                                text = "Go back",
                                onClick = onBackClick
                            )
                        }
                        else -> {
                            ForgotPasswordViewFooter(
                                modifier = Modifier
                                    .imePadding()
                                    .padding(24.dp),
                                canSubmit = state.submitEnabled,
                                onSubmit = {
                                    state.eventSink(ExtendedOnboardingEvents.Submit)
                                }
                            )
                        }
                    }
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
private fun ForgotPasswordForm(state: ExtendedOnboardingState, scrollState: ScrollState) {
    var loginFieldState by textFieldState(stateValue = state.forgotPasswordEmail)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnboardingScreenHeader(
            title = "Password Reset",
            subTitle = "Enter your registered Email ID and we'll email you a reset password link.",
            titleColor = ElementTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        Text(
            text = "Email",
            style = ElementTheme.zeroTypography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        SimpleInputField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .testTag(TestTags.loginEmailUsername)
                .semantics {
                    contentType = ContentType.EmailAddress
                },
            text = loginFieldState,
            placeholder = R.string.enter_email_address,
            onTextChanged = {
                val sanitized = it.sanitize()
                loginFieldState = sanitized
                state.eventSink(ExtendedOnboardingEvents.SetForgotPasswordEmail(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.submitEnabled) {
                        state.eventSink(ExtendedOnboardingEvents.Submit)
                        focusManager.clearFocus()
                    }
                }
            )
        )
    }
}

@Composable
fun ForgotPasswordSuccessView(userEmail: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "A reset link has been sent to",
                style = ElementTheme.zeroTypography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary
            )

            Spacer(Modifier.size(SPACING_4X.dp))

            Text(
                text = userEmail,
                style = ElementTheme.zeroTypography.fontHeadingMdBold,
                color = ElementTheme.colors.textPrimary
            )
        }
    }
}

@Composable
fun ForgotPasswordViewFooter(
    modifier: Modifier = Modifier,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = modifier.navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ZeroPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Send Reset Link",
            enabled = canSubmit,
            onClick = onSubmit
        )
    }
}

@PreviewsDayNight
@Composable
internal fun ForgotPasswordViewPreview(@PreviewParameter(ExtendedOnboardingStateProvider::class) state: ExtendedOnboardingState) = ElementPreview {
    ForgotPasswordView(
        state = state,
        onBackClick = {},
    )
}
