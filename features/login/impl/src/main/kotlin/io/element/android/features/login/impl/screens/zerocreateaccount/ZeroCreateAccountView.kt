/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.zerocreateaccount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.reown.appkit.ui.components.internal.AppKitComponent
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.error.loginError
import io.element.android.features.login.impl.login.LoginMode
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.autofill
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.ZeroAuthScreensBackground
import io.element.android.support.zero.common.ui.animation.FadeExpandAnimation
import io.element.android.support.zero.common.ui.component.ErrorTextBox
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.component.SuccessTextBox
import io.element.android.support.zero.common.ui.component.ZImageButton
import io.element.android.support.zero.common.ui.component.passwordinput.PasswordTextField
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_5X
import io.element.android.support.zero.common.ui.theme.SPACING_6X
import io.element.android.support.zero.screens.login.AuthenticationTypeSegmentedControl
import io.element.android.support.zero.screens.login.util.ZeroAuthenticationFlowType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroCreateAccountView(
    modifier: Modifier = Modifier,
    state: ZeroCreateAccountState,
    onProceedToLoginScreen: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    BackHandler { onBackClick() }

    val isLoading by remember(state.createAccountAction) {
        derivedStateOf {
            state.createAccountAction is AsyncData.Loading
        }
    }
    val focusManager = LocalFocusManager.current

    fun submit() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)

        state.eventSink(ZeroCreateAccountEvents.Submit)
    }

    val loginFlow: MutableState<ZeroAuthenticationFlowType> = remember { mutableStateOf(ZeroAuthenticationFlowType.EMAIL) }
    val showWeb3LoginUI = loginFlow.value == ZeroAuthenticationFlowType.WEB3

    ZeroAuthScreensBackground(isLoading = isLoading) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(io.element.android.support.zero.R.string.create_account)) },
                    navigationIcon = { BackButton(onClick = onBackClick) },
                    colors = TopAppBarDefaults
                        .centerAlignedTopAppBarColors()
                        .copy(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                if (state.showWeb3Modal) {
                    AppKitComponent(
                        shouldOpenChooseNetwork = false,
                        closeModal = {
                            state.eventSink(ZeroCreateAccountEvents.ToggleWeb3Modal(false))
                        }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(state = scrollState)
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
            ) {
                Spacer(modifier = Modifier.size(SPACING_10X.dp))
                Spacer(modifier = Modifier.size(SPACING_5X.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AuthenticationTypeSegmentedControl(
                        defaultSelectedItemIndex = ZeroAuthenticationFlowType.indexOf(loginFlow.value),
                        controlWidth = 230.dp,
                        items =
                        listOf(
                            stringResource(io.element.android.support.zero.R.string.web3),
                            stringResource(io.element.android.support.zero.R.string.email)
                        ),
                        onItemSelection = { loginFlow.value = ZeroAuthenticationFlowType.get(it) }
                    )
                }
                if (showWeb3LoginUI) {
                    Spacer(modifier = Modifier.size(SPACING_6X.dp))
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    // Wallet Connection Login UI
                    FadeExpandAnimation(visible = showWeb3LoginUI) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.size(SPACING_4X.dp))
                            ZImageButton(
                                image = io.element.android.support.zero.R.drawable.img_btn_connect_wallet,
                                text = stringResource(id = io.element.android.support.zero.R.string.connect_a_wallet)
                            ) {
                                state.eventSink(ZeroCreateAccountEvents.ToggleWeb3Modal(true))
                            }
                            /*if (uiState is AuthUiState.Error && uiState.isWalletConnectionError) {
                                Spacer(modifier = Modifier.size(SPACING_6X.dp))
                                ErrorTextBox(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 65.dp),
                                    text = stringResource(uiState.error)
                                )
                            }
                            if (isWalletConnected) {
                                Spacer(modifier = Modifier.size(SPACING_6X.dp))
                                SuccessTextBox(text = stringResource(id = R.string.wallet_connected))
                            }*/
                        }
                    }

                    // Email Create Account UI
                    FadeExpandAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        visible = !showWeb3LoginUI
                    ) {
                        ZeroCreateAccountForm(
                            state = state,
                            onSubmit = ::submit
                        )
                    }
                }

                if (state.createAccountAction is AsyncData.Failure) {
                    ZeroCreateAccountErrorDialog(error = state.createAccountAction.error, onDismiss = {
                        state.eventSink(ZeroCreateAccountEvents.ClearError)
                    })
                }
            }

            when (state.loginFlow) {
                is AsyncData.Success -> {
                    when (state.loginFlow.data) {
                        LoginMode.PasswordLogin -> onProceedToLoginScreen()
                        else -> {}
                    }
                }
                else -> {}
            }
        }
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

    Column(
        modifier = Modifier.padding(horizontal = PADDING_4X.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(SPACING_10X.dp))

        SimpleInputField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .autofill(
                    autofillTypes = listOf(AutofillType.EmailAddress),
                    onFill = {
                        val sanitized = it.sanitize()
                        emailFieldState = sanitized
                        eventSink(ZeroCreateAccountEvents.SetEmail(sanitized))
                    }
                ),
            text = emailFieldState,
            placeholder = io.element.android.support.zero.R.string.email_address,
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
                text = stringResource(io.element.android.support.zero.R.string.error_invalid_email)
            )
        }

        Spacer(modifier = Modifier.size(SPACING_6X.dp))

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .autofill(
                    autofillTypes = listOf(AutofillType.Password),
                    onFill = {
                        val sanitized = it.sanitize()
                        passwordFieldState = sanitized
                        eventSink(ZeroCreateAccountEvents.SetPassword(sanitized))
                    }
                ),
            placeHolder = io.element.android.support.zero.R.string.password,
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

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .autofill(
                    autofillTypes = listOf(AutofillType.Password),
                    onFill = {
                        val sanitized = it.sanitize()
                        confirmPasswordFieldState = sanitized
                        eventSink(ZeroCreateAccountEvents.SetConfirmPassword(sanitized))
                    }
                ),
            placeHolder = io.element.android.support.zero.R.string.confirm_password,
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
                onDone = { onSubmit() }
            ),
            iconTint = Color.White.copy(alpha = 0.75f)
        )

        if (confirmPasswordFieldState.isNotBlank()) {
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            if (state.formState.isConfirmPasswordValid()) {
                SuccessTextBox(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(io.element.android.support.zero.R.string.password_match)
                )
            } else {
                ErrorTextBox(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(io.element.android.support.zero.R.string.password_mismatch_error)
                )
            }
        }

        Spacer(modifier = Modifier.size(SPACING_10X.dp))
        ZImageButton(
            image = io.element.android.support.zero.R.drawable.img_btn_create_account,
            text = stringResource(id = io.element.android.support.zero.R.string.create_account),
            enabled = state.submitEnabled,
            onClick = { onSubmit() }
        )

        Spacer(modifier = Modifier.size(SPACING_10X.dp))
        Spacer(modifier = Modifier.size(SPACING_10X.dp))
        LoginNavigationFooter(
            onClick = { eventSink(ZeroCreateAccountEvents.OpenLogin) }
        )
    }
}

@Composable
private fun LoginNavigationFooter(
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier.clickable {
            onClick.invoke()
        },
        text =
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = ElementTheme.colors.textSecondary
                )
            ) {
                append(stringResource(io.element.android.support.zero.R.string.already_on_zero))
            }
            append(" ")
            withStyle(
                style = SpanStyle(
                    color = ElementTheme.colors.zeroBrandColor,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append(stringResource(io.element.android.support.zero.R.string.log_in))
            }
        },
        style = ElementTheme.zeroTypography.fontBodyMdRegular,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ZeroCreateAccountErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    ErrorDialog(
        title = stringResource(id = CommonStrings.dialog_title_error),
        content = stringResource(loginError(error)),
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
