/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.reown.appkit.ui.components.internal.AppKitComponent
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.error.zeroAuthenticationError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.component.ErrorTextBox
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.component.SuccessTextBox
import io.element.android.support.zero.common.ui.component.passwordinput.PasswordTextField
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_6X
import io.element.android.support.zero.common.ui.theme.SPACING_8X
import io.element.android.support.zero.screens.login.ZeroAuthSegmentControl
import io.element.android.support.zero.screens.login.util.ZeroAuthenticationFlowType
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroCreateAccountView(
    modifier: Modifier = Modifier,
    state: ZeroCreateAccountState,
    onProceedToLoginScreen: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val autofillManager = LocalAutofillManager.current

    BackHandler {
        autofillManager?.cancel()
        onBackClick()
    }

    val isLoading by remember(state.createAccountAction) {
        derivedStateOf {
            state.createAccountAction is AsyncData.Loading
        }
    }
    val focusManager = LocalFocusManager.current

    fun submit() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)
        autofillManager?.commit()
        state.eventSink(ZeroCreateAccountEvents.Submit)
    }

    val loginFlow: MutableState<ZeroAuthenticationFlowType> = rememberSaveable { mutableStateOf(ZeroAuthenticationFlowType.EMAIL) }
    val showWeb3LoginUI = loginFlow.value == ZeroAuthenticationFlowType.WEB3

    Scaffold(modifier = modifier) { padding ->
        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {
            ZeroOnboardingPage(
                showBackButton = true,
                onBackClick = {
                    autofillManager?.cancel()
                    onBackClick()
                },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = scrollState)
                            .padding(24.dp),
                    ) {
                        ZeroAuthSegmentControl(
                            selectedTab = ZeroAuthenticationFlowType.indexOf(loginFlow.value),
                            onTabSelected = {
                                loginFlow.value = ZeroAuthenticationFlowType.get(it)
                            }
                        )
                        if (showWeb3LoginUI) {
                            Web3SignupForm(state = state)
                        } else {
                            ZeroCreateAccountForm(
                                state = state,
                                onSubmit = ::submit
                            )
                        }
                    }
                },
                footer = {
                    if (!showWeb3LoginUI) {
                        ZeroPrimaryButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .imePadding()
                                .padding(24.dp),
                            text = "Create Account",
                            enabled = state.submitEnabled,
                            onClick = ::submit
                        )
                    }
                }
            )
            if (isLoading) {
                ProgressDialog()
            }
            if (state.createAccountAction is AsyncData.Failure) {
                ZeroCreateAccountErrorDialog(error = state.createAccountAction.error, onDismiss = {
                    state.eventSink(ZeroCreateAccountEvents.ClearError)
                })
            }
            if (state.showWeb3Modal) {
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                ) {
                    AppKitComponent(
                        shouldOpenChooseNetwork = false,
                        closeModal = {
                            state.eventSink(ZeroCreateAccountEvents.ToggleWeb3Modal(false))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Web3SignupForm(state: ZeroCreateAccountState) {
    Column {
        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        OnboardingScreenHeader(
            title = "Continue with Web3",
            subTitle = "Connect your web3 wallet to continue signing up."
        )

        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        ZeroPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            text = "Connect a Wallet",
            trailingIcon = ImageVector.vectorResource(R.drawable.ic_logo_walletconnect),
            onClick = {
                state.eventSink(ZeroCreateAccountEvents.ToggleWeb3Modal(true))
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ZeroCreateAccountForm(
    state: ZeroCreateAccountState,
    onSubmit: () -> Unit,
) {
    var emailFieldState by textFieldState(stateValue = state.formState.email)
    var passwordFieldState by textFieldState(stateValue = state.formState.password)
    var confirmPasswordFieldState by textFieldState(stateValue = state.formState.confirmPassword)

    val focusManager = LocalFocusManager.current
    val eventSink = state.eventSink

    Column {
        Spacer(modifier = Modifier.size(SPACING_8X.dp))

        OnboardingScreenHeader(
            title = "Continue with Email",
            subTitle = "Enter your credentials to continue signing up."
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
                .semantics {
                    contentType = ContentType.EmailAddress
                },
            text = emailFieldState,
            placeholder = R.string.enter_email_address,
            onTextChanged = {
                val sanitized = it.sanitize()
                emailFieldState = sanitized
                eventSink(ZeroCreateAccountEvents.SetEmail(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            })
        )

        if (emailFieldState.isNotBlank() &&
            !state.formState.isEmailValid()) {
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            ErrorTextBox(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.error_invalid_email)
            )
        }

        Spacer(modifier = Modifier.size(SPACING_6X.dp))

        Text(
            text = "Password",
            style = ElementTheme.zeroTypography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .semantics {
                    contentType = ContentType.Password
                },
            placeHolder = R.string.enter_password,
            onTextChanged = {
                val sanitized = it.sanitize()
                passwordFieldState = sanitized
                eventSink(ZeroCreateAccountEvents.SetPassword(sanitized))
            },
            showPasswordCriteria = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            iconTint = Color.White.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.size(SPACING_6X.dp))

        Text(
            text = "Confirm Password",
            style = ElementTheme.zeroTypography.fontBodySmRegular,
            color = ElementTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .semantics {
                    contentType = ContentType.Password
                },
            placeHolder = R.string.confirm_your_password,
            onTextChanged = {
                val sanitized = it.sanitize()
                confirmPasswordFieldState = sanitized
                eventSink(ZeroCreateAccountEvents.SetConfirmPassword(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (state.submitEnabled) {
                        onSubmit()
                    }
                }
            ),
            iconTint = Color.White.copy(alpha = 0.75f)
        )

        if (confirmPasswordFieldState.isNotBlank()) {
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            if (state.formState.isConfirmPasswordValid()) {
                SuccessTextBox(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.password_match)
                )
            } else {
                ErrorTextBox(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.password_mismatch_error)
                )
            }
        }
    }
}

@Composable
private fun ZeroCreateAccountErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    ErrorDialog(
        title = stringResource(id = CommonStrings.dialog_title_error),
        content = zeroAuthenticationError(error),
        onSubmit = onDismiss
    )
}

@PreviewsDayNight
@Composable
internal fun ZeroCreateAccountViewPreview(@PreviewParameter(ZeroCreateAccountStateProvider::class) state: ZeroCreateAccountState) = ElementPreview {
    ZeroCreateAccountView(
        state = state,
        onBackClick = {},
    )
}
