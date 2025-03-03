/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.login.impl.screens.confirmaccountprovider

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.support.zero.R
import io.element.android.features.login.impl.dialogs.SlidingSyncNotSupportedDialog
import io.element.android.features.login.impl.error.ChangeServerError
import io.element.android.features.login.impl.screens.createaccount.AccountCreationNotSupported
import io.element.android.libraries.architecture.AsyncData
import io.element.android.libraries.designsystem.components.dialogs.ErrorDialog
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.matrix.api.auth.OidcDetails
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.screens.confirmaccountprovider.ZeroConfirmAccountProviderView

@Composable
fun ConfirmAccountProviderView(
    state: ConfirmAccountProviderState,
    onOidcDetails: (OidcDetails) -> Unit,
    onNeedLoginPassword: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onCreateAccountContinue: (url: String) -> Unit,
    onChange: () -> Unit,
    onCreateZeroAccount: (inviteCode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLoading by remember(state.loginFlow) {
        derivedStateOf {
            state.loginFlow is AsyncData.Loading
        }
    }
    val eventSink = state.eventSink

    /*HeaderFooterPage(
        modifier = modifier,
        header = {
            IconTitleSubtitleMolecule(
                modifier = Modifier.padding(top = 60.dp),
                iconStyle = BigIcon.Style.Default(CompoundIcons.UserProfileSolid()),
                title = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_title
                    } else {
                        R.string.screen_account_provider_signin_title
                    },
                    state.accountProvider.title
                ),
                subTitle = stringResource(
                    id = if (state.isAccountCreation) {
                        R.string.screen_account_provider_signup_subtitle
                    } else {
                        R.string.screen_account_provider_signin_subtitle
                    },
                )
            )
        },
        footer = {
            ButtonColumnMolecule {
                Button(
                    text = stringResource(id = CommonStrings.action_continue),
                    showProgress = isLoading,
                    onClick = { eventSink.invoke(ConfirmAccountProviderEvents.Continue) },
                    enabled = state.submitEnabled || isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginContinue)
                )
                TextButton(
                    text = stringResource(id = R.string.screen_account_provider_change),
                    onClick = onChange,
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(TestTags.loginChangeServer)
                )
            }
        }
    )*/
    ZeroConfirmAccountProviderView(
        modifier = modifier,
        isLoading = isLoading,
        onInvoked = { eventSink.invoke(ConfirmAccountProviderEvents.Continue) },
        onValidateInviteCode = { inviteCode ->
            eventSink.invoke(ConfirmAccountProviderEvents.ValidateInvite(inviteCode))
        }
    ) {
        when (state.loginFlow) {
            is AsyncData.Failure -> {
                when (val error = state.loginFlow.error) {
                    is ChangeServerError.Error -> {
                        ErrorDialog(
                            content = error.message(),
                            onSubmit = {
                                eventSink.invoke(ConfirmAccountProviderEvents.ClearError)
                            }
                        )
                    }
                    is ChangeServerError.SlidingSyncAlert -> {
                        SlidingSyncNotSupportedDialog(
                            onLearnMoreClick = {
                                onLearnMoreClick()
                                eventSink(ConfirmAccountProviderEvents.ClearError)
                            },
                            onDismiss = {
                                eventSink(ConfirmAccountProviderEvents.ClearError)
                            }
                        )
                    }
                    is AccountCreationNotSupported -> {
                        ErrorDialog(
                            content = stringResource(CommonStrings.error_account_creation_not_possible),
                            onSubmit = {
                                eventSink.invoke(ConfirmAccountProviderEvents.ClearError)
                            }
                        )
                    }
                    is InvalidZeroInviteCode -> {
                        ErrorDialog(
                            content = stringResource(R.string.error_invalid_invite_code),
                            onSubmit = {
                                eventSink.invoke(ConfirmAccountProviderEvents.ClearError)
                            }
                        )
                    }
                }
            }
            is AsyncData.Loading -> Unit // The Continue button shows the loading state
            is AsyncData.Success -> {
                when (val loginFlowState = state.loginFlow.data) {
                    is LoginFlow.OidcFlow -> onOidcDetails(loginFlowState.oidcDetails)
                    LoginFlow.PasswordLogin -> onNeedLoginPassword()
                    is LoginFlow.AccountCreationFlow -> onCreateAccountContinue(loginFlowState.url)
                    is LoginFlow.ZeroCreateAccountFlow -> onCreateZeroAccount(loginFlowState.inviteCode)
                }
            }
            AsyncData.Uninitialized -> Unit
        }
    }
}

@PreviewsDayNight
@Composable
internal fun ConfirmAccountProviderViewPreview(
    @PreviewParameter(ConfirmAccountProviderStateProvider::class) state: ConfirmAccountProviderState
) = ElementPreview {
    ConfirmAccountProviderView(
        state = state,
        onOidcDetails = {},
        onNeedLoginPassword = {},
        onCreateAccountContinue = {},
        onLearnMoreClick = {},
        onChange = {},
        onCreateZeroAccount = {},
    )
}
