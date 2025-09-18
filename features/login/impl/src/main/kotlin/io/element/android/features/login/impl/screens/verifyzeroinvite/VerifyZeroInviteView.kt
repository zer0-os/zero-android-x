/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.verifyzeroinvite

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.element.android.features.login.impl.screens.extendedOnboarding.views.ExtendedViewErrorDialog
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_8X
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@Composable
fun VerifyZeroInviteView(
    modifier: Modifier = Modifier,
    state: VerifyZeroInviteState,
    onBackClick: () -> Unit,
    onCreateZeroAccount: (inviteCode: String) -> Unit,
) {
    val isInviteCodeVerified by remember(state.actionState) {
        derivedStateOf {
            state.actionState is AsyncAction.Success
        }
    }
    if (isInviteCodeVerified) {
        onCreateZeroAccount(state.inviteCode)
    }

    Scaffold(modifier = modifier) { padding ->
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {
            ZeroOnboardingPage(
                showBackButton = true,
                onBackClick = onBackClick,
                content = {
                    VerifyInviteForm(
                        state = state,
                        scrollState = scrollState
                    )
                },
                footer = {
                    ZeroPrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(24.dp),
                        text = "Continue",
                        enabled = state.submitEnabled,
                        onClick = {
                            state.eventSink(VerifyZeroInviteEvents.Submit)
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
                    onDismiss = { state.eventSink(VerifyZeroInviteEvents.ClearError) }
                )
            }
        }
    }
}

@Composable
fun VerifyInviteForm(
    state: VerifyZeroInviteState, scrollState: ScrollState
) {
    var inviteFieldState by textFieldState(stateValue = state.inviteCode)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        OnboardingScreenHeader(
            title = "Enter Invite Code",
            subTitle = "Please enter your invite code to start using ZERO.",
            titleColor = ElementTheme.colors.zeroBrandColor
        )

        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        Text(
            text = "Invite Code",
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
            text = inviteFieldState,
            placeholder = R.string.enter_invite_code,
            onTextChanged = {
                val sanitized = it.sanitize()
                inviteFieldState = sanitized
                state.eventSink(VerifyZeroInviteEvents.OnInviteEdit(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    state.eventSink(VerifyZeroInviteEvents.Submit)
                    focusManager.clearFocus()
                }
            )
        )
    }
}

@PreviewsDayNight
@Composable
fun VerifyZeroInviteViewPreview(
    @PreviewParameter(VerifyZeroInviteStateProvider::class) state: VerifyZeroInviteState
) = ElementTheme {
    VerifyZeroInviteView(
        state = state,
        onBackClick = {},
        onCreateZeroAccount = {}
    )
}
