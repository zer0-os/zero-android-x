package io.element.android.features.login.impl.screens.loginpassword

import androidx.activity.compose.BackHandler
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
import com.reown.appkit.ui.components.internal.AppKitComponent
import io.element.android.features.login.impl.error.loginError
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.components.form.textFieldState
import io.element.android.libraries.designsystem.modifiers.onTabOrEnterKeyFocusNext
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.extension.sanitize
import io.element.android.support.zero.common.ui.ZeroAuthScreensBackground
import io.element.android.support.zero.common.ui.animation.FadeExpandAnimation
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.component.ZImageButton
import io.element.android.support.zero.common.ui.component.passwordinput.PasswordTextField
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_5X
import io.element.android.support.zero.common.ui.theme.SPACING_6X
import io.element.android.support.zero.screens.login.AuthenticationTypeSegmentedControl
import io.element.android.support.zero.screens.login.util.ZeroAuthenticationFlowType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroLoginPasswordView(
    state: LoginPasswordState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val autofillManager = LocalAutofillManager.current

    BackHandler {
        autofillManager?.cancel()
        onBackClick()
    }

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

    val loginFlow: MutableState<ZeroAuthenticationFlowType> = remember { mutableStateOf(ZeroAuthenticationFlowType.EMAIL) }
    val showWeb3LoginUI = loginFlow.value == ZeroAuthenticationFlowType.WEB3

    ZeroAuthScreensBackground(isLoading = isLoading) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(io.element.android.support.zero.R.string.log_in)) },
                    navigationIcon = { BackButton(onClick = {
                        autofillManager?.cancel()
                        onBackClick()
                    }) },
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
                            state.eventSink(LoginPasswordEvents.ToggleWeb3Modal(false))
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
                                state.eventSink(LoginPasswordEvents.ToggleWeb3Modal(true))
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

                    // Email Login UI
                    FadeExpandAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        visible = !showWeb3LoginUI
                    ) {
                        ZeroLoginForm(
                            state = state,
                            onSubmit = ::submit
                        )
                    }
                }

                if (state.loginAction is AsyncData.Failure) {
                    LoginErrorDialog(error = state.loginAction.error, onDismiss = {
                        state.eventSink(LoginPasswordEvents.ClearError)
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ZeroLoginForm(
    state: LoginPasswordState,
    onSubmit: () -> Unit,
) {
    var loginFieldState by textFieldState(stateValue = state.formState.login)
    var passwordFieldState by textFieldState(stateValue = state.formState.password)

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
                .testTag(TestTags.loginEmailUsername)
                .semantics {
                    contentType = ContentType.EmailAddress
                },
            text = loginFieldState,
            placeholder = io.element.android.support.zero.R.string.email_address,
            onTextChanged = {
                val sanitized = it.sanitize()
                loginFieldState = sanitized
                eventSink(LoginPasswordEvents.SetLogin(sanitized))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            })
        )

        Spacer(modifier = Modifier.size(SPACING_6X.dp))

        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onTabOrEnterKeyFocusNext(focusManager)
                .testTag(TestTags.loginPassword)
                .semantics {
                    contentType = ContentType.Password
                },
            placeHolder = io.element.android.support.zero.R.string.password,
            onTextChanged = {
                val sanitized = it.sanitize()
                passwordFieldState = sanitized
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

        Spacer(modifier = Modifier.size(SPACING_10X.dp))
        ZImageButton(
            image = io.element.android.support.zero.R.drawable.img_btn_login,
            text = stringResource(id = io.element.android.support.zero.R.string.log_in),
            enabled = state.submitEnabled,
            onClick = { onSubmit() }
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
    )
}
