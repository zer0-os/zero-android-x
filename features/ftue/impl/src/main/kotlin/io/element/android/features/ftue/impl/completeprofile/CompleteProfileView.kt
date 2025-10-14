/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.features.ftue.impl.completeprofile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.async.AsyncActionView
import io.element.android.libraries.designsystem.components.async.AsyncActionViewDefaults
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.modifiers.clearFocusOnTap
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.ui.components.AvatarActionBottomSheet
import io.element.android.libraries.matrix.ui.components.EditableAvatarView
import io.element.android.libraries.permissions.api.PermissionsView
import io.element.android.support.zero.common.extension.getActivity
import io.element.android.support.zero.common.ui.ZeroPrimaryButton
import io.element.android.support.zero.common.ui.component.InfoBox
import io.element.android.support.zero.common.ui.component.SimpleInputField
import io.element.android.support.zero.common.ui.theme.PADDING_4X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_2X

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileView(
    state: CompleteProfileState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val exitApp: () -> Unit = {
        context.getActivity()?.finishAffinity()
    }

    val isAvatarActionsSheetVisible = remember { mutableStateOf(false) }

    fun onAvatarClick() {
        focusManager.clearFocus()
        isAvatarActionsSheetVisible.value = true
    }

    BackHandler { exitApp() }

    fun submit() {
        focusManager.clearFocus(force = true)
        state.eventSink(CompleteProfileEvents.Submit)
    }

    Box {
        Scaffold(
            modifier = modifier.clearFocusOnTap(focusManager),
            containerColor = Color.Black
        ) { padding ->
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(SPACING_10X.dp))
                Text(
                    text = "Enter your details",
                    style = ElementTheme.zeroTypography.fontHeadingMdBold,
                    color = ElementTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(SPACING_2X.dp))
                Text(
                    text = "Complete your profile",
                    style = ElementTheme.zeroTypography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(SPACING_10X.dp))
                EditableAvatarView(
                    matrixId = "",
                    displayName = state.displayName,
                    avatarUrl = state.userAvatarUrl?.toString(),
                    avatarSize = AvatarSize.CompleteProfileScreen,
                    onAvatarClick = { onAvatarClick() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    avatarType = AvatarType.User
                )

                Spacer(modifier = Modifier.height(SPACING_10X.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = "Display Name",
                        style = ElementTheme.zeroTypography.fontBodySmRegular,
                        color = ElementTheme.colors.textSecondary
                    )
                    SimpleInputField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = PADDING_4X.dp),
                        text = state.displayName,
                        placeholder = io.element.android.support.zero.R.string.enter_display_name,
                        onTextChanged = {
                            state.eventSink(CompleteProfileEvents.SetDisplayName(it))
                        },
                        maxInputLength = 24,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (state.saveButtonEnabled) {
                                    submit()
                                }
                            }
                        )
                    )
                    InfoBox(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "Name must be atleast 3 characters or more upto 24 characters"
                    )
                }

                ZeroPrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(24.dp),
                    text = "Continue",
                    enabled = state.saveButtonEnabled,
                    onClick = { submit() }
                )
            }

            AvatarActionBottomSheet(
                actions = state.avatarActions,
                isVisible = isAvatarActionsSheetVisible.value,
                onDismiss = { isAvatarActionsSheetVisible.value = false },
                onSelectAction = { state.eventSink(CompleteProfileEvents.HandleAvatarAction(it)) }
            )

            AsyncActionView(
                async = state.saveAction,
                progressDialog = { AsyncActionViewDefaults.ProgressDialog() },
                onSuccess = { state.eventSink(CompleteProfileEvents.ProfileUpdated) },
                errorMessage = { stringResource(id = io.element.android.support.zero.R.string.error_complete_profile) },
                onErrorDismiss = { state.eventSink(CompleteProfileEvents.Clear) }
            )
        }

        PermissionsView(
            state = state.cameraPermissionState,
        )
    }
}

@PreviewsDayNight
@Composable
internal fun CompleteProfileViewPreview(
    @PreviewParameter(CompleteProfileStateProvider::class) state: CompleteProfileState
) {
    ElementPreview {
        CompleteProfileView(
            state = state
        )
    }
}
