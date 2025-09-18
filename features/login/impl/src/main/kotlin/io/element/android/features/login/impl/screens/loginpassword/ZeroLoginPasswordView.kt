package io.element.android.features.login.impl.screens.loginpassword

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofillManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.login.impl.error.loginError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.components.TextButton
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.R
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.OnboardingScreenHeader
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.animation.FadeExpandAnimation
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.component.passwordinput.PasswordTextField
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_8X
import io.element.android.support.zero.screens.onboarding.components.ZeroOnboardingPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroLoginPasswordView(
    state: LoginPasswordState,
    onBackClick: () -> Unit,
    onVerifyOtp: () -> Unit,
    onForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val autofillManager = LocalAutofillManager.current

    BackHandler {
        autofillManager?.cancel()
        onBackClick()
    }

    val showSingleSignOnForm = rememberSaveable { mutableStateOf(true) }

    val isLoading by remember(state.loginAction) {
        derivedStateOf {
            state.loginAction is AsyncData.Loading
        }
    }
    val focusManager = LocalFocusManager.current

    fun submit() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)
        autofillManager?.commit()
        state.eventSink(LoginPasswordEvents.Submit)
    }

    fun submitSSO() {
        // Clear focus to prevent keyboard issues with textfields
        focusManager.clearFocus(force = true)
        autofillManager?.commit()
        state.eventSink(LoginPasswordEvents.SubmitSSO)
    }

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
                        ZeroLoginForm(
                            state = state,
                            showSingleSignOnForm = showSingleSignOnForm.value,
                            onForgotPassword = onForgotPassword,
                            onSubmit = {
                                if (showSingleSignOnForm.value) {
                                    submitSSO()
                                } else {
                                    submit()
                                }
                            }
                        )
                    }
                },
                footer = {
                    if (showSingleSignOnForm.value) {
                        SingleSignOnLoginFooter(
                            modifier = Modifier
                                .imePadding()
                                .padding(24.dp),
                            canSubmit = state.submitOtpEnabled,
                            onSubmit = { submitSSO() },
                            onLoginWithPassword = {
                                showSingleSignOnForm.value = false
                            }
                        )
                    } else {
                        LoginPasswordFooter(
                            modifier = Modifier
                                .imePadding()
                                .padding(24.dp),
                            canSubmit = state.submitEnabled,
                            onSubmit = { submit() },
                            onLoginWithOtp = {
                                showSingleSignOnForm.value = true
                            }
                        )
                    }
                }
            )
            if (isLoading) {
                ProgressDialog()
            }
            if (state.loginAction is AsyncData.Failure) {
                LoginErrorDialog(error = state.loginAction.error, onDismiss = {
                    state.eventSink(LoginPasswordEvents.ClearError)
                })
            }
        }
    }
}

@Composable
private fun ZeroLoginForm(
    state: LoginPasswordState,
    showSingleSignOnForm: Boolean,
    onForgotPassword: () -> Unit,
    onSubmit: () -> Unit,
) {
    var loginFieldState by textFieldState(stateValue = state.formState.login)

    val focusManager = LocalFocusManager.current
    val eventSink = state.eventSink

    val subTitle = if (showSingleSignOnForm) {
        "Enter Email ID to generate a one-time OTP to login."
    } else "Enter your credentials."
    val imeAction = if (showSingleSignOnForm) {
        ImeAction.Done
    } else ImeAction.Next

    Column(horizontalAlignment = Alignment.Start) {
        OnboardingScreenHeader(
            title = "Continue with Email",
            subTitle = subTitle
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
                eventSink(LoginPasswordEvents.SetLogin(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { onSubmit() }
            )
        )

        FadeExpandAnimation(visible = !showSingleSignOnForm) {
            Column {
                Spacer(modifier = Modifier.size(SPACING_8X.dp))

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
                        .testTag(TestTags.loginPassword)
                        .semantics {
                            contentType = ContentType.Password
                        },
                    placeHolder = R.string.enter_password,
                    onTextChanged = {
                        val sanitized = it.sanitize()
                        eventSink(LoginPasswordEvents.SetPassword(sanitized))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSubmit() }
                    ),
                    iconTint = Color.White.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.size(SPACING_2X.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Forgot Password?",
                        onClick = onForgotPassword,
                        size = ButtonSize.Medium,
                        contentColor = ElementTheme.colors.zeroBrandColor
                    )
                }
            }
        }
    }
}

@Composable
fun SingleSignOnLoginFooter(
    modifier: Modifier = Modifier,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onLoginWithPassword: () -> Unit,
) {
    Column(modifier = modifier.navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        ZeroPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Generate Link",
            enabled = canSubmit,
            onClick = onSubmit
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        TextButton(
            text = "Login with Password?",
            onClick = onLoginWithPassword,
            size = ButtonSize.Medium,
            contentColor = ElementTheme.colors.zeroBrandColor
        )
    }
}

@Composable
fun LoginPasswordFooter(
    modifier: Modifier = Modifier,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onLoginWithOtp: () -> Unit,
) {
    Column(modifier = modifier.navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        ZeroPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Login",
            enabled = canSubmit,
            onClick = onSubmit
        )
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        TextButton(
            text = "Use OTP instead?",
            onClick = onLoginWithOtp,
            size = ButtonSize.Medium,
            contentColor = ElementTheme.colors.zeroBrandColor
        )
    }
}

@Composable
private fun LoginErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    ErrorDialog(
        title = stringResource(id = CommonStrings.dialog_title_error),
        content = stringResource(loginError(error)),
        onSubmit = onDismiss
    )
}

@PreviewsDayNight
@Composable
internal fun ZeroLoginPasswordViewPreview(@PreviewParameter(LoginPasswordStateProvider::class) state: LoginPasswordState) = ElementPreview {
    ZeroLoginPasswordView(
        state = state,
        onBackClick = {},
        onVerifyOtp = {},
        onForgotPassword = {}
    )
}
