/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.preferences.impl.R
import io.element.android.libraries.designsystem.components.ProgressDialog
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.components.preferences.PreferenceCategory
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.components.preferences.PreferenceTextField
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.CircularProgressIndicator
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.featureflag.ui.FeatureListView
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog

@Composable
fun DeveloperSettingsView(
    state: DeveloperSettingsState,
    onOpenShowkase: () -> Unit,
    onPushHistoryClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.showLoader) {
        ProgressDialog()
    }
    BackHandler(
        enabled = !state.showLoader,
        onBack = onBackClick,
    )
    PreferencePage(
        modifier = modifier,
        onBackClick = {
            if (!state.showLoader) {
                onBackClick()
            }
        },
        //title = stringResource(id = CommonStrings.common_developer_options)
        title = stringResource(id = CommonStrings.common_advanced_settings)
    ) {
        // Note: this is OK to hardcode strings in this debug screen.
        /*PreferenceCategory(
            title = "Feature flags",
            showTopDivider = true,
        ) {
            FeatureListContent(state)
        }
        NotificationCategory(onPushHistoryClick)
        ElementCallCategory(state = state)

        PreferenceCategory(title = "Rust SDK") {
            PreferenceDropdown(
                title = "Tracing log level",
                supportingText = "Requires app reboot",
                selectedOption = state.tracingLogLevel.dataOrNull(),
                options = LogLevelItem.entries.toImmutableList(),
                onSelectOption = { logLevel ->
                    state.eventSink(DeveloperSettingsEvents.SetTracingLogLevel(logLevel))
                }
            )
        }
        PreferenceCategory(title = "Enable trace logs per SDK feature") {
            Text(
                text = "Requires app reboot",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )
            for (logPack in TraceLogPack.entries) {
                PreferenceSwitch(
                    title = logPack.title,
                    isChecked = state.tracingLogPacks.contains(logPack),
                    onCheckedChange = { isChecked -> state.eventSink(DeveloperSettingsEvents.ToggleTracingLogPack(logPack, isChecked)) }
                )
            }
        }

        PreferenceCategory(title = "Showkase") {
            ListItem(
                headlineContent = {
                    Text("Open Showkase browser")
                },
                onClick = onOpenShowkase
            )
        }
        RageshakePreferencesView(
            state = state.rageshakeState,
        )
        PreferenceCategory(title = "Crash", showTopDivider = false) {
            ListItem(
                headlineContent = {
                    Text("Crash the app 💥")
                },
                onClick = { error("This crash is a test.") }
            )
        }*/
        UserAccountCategory(state = state)

        val cache = state.cacheSize
        PreferenceCategory(title = "Cache", showTopDivider = false) {
            ListItem(
                headlineContent = {
                    Text("Clear cache")
                },
                trailingContent = if (state.cacheSize.isLoading() || state.clearCacheAction.isLoading()) {
                    ListItemContent.Custom {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .progressSemantics()
                                .size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    ListItemContent.Text(cache.dataOrNull().orEmpty())
                },
                onClick = {
                    if (state.clearCacheAction.isLoading().not()) {
                        state.eventSink(DeveloperSettingsEvents.ClearCache)
                    }
                }
            )
        }
    }
}

@Composable
private fun ElementCallCategory(
    state: DeveloperSettingsState,
) {
    PreferenceCategory(title = "Element Call", showTopDivider = true) {
        val callUrlState = state.customElementCallBaseUrlState

        val supportingText = if (callUrlState.baseUrl.isNullOrEmpty()) {
            stringResource(R.string.screen_advanced_settings_element_call_base_url_description)
        } else {
            callUrlState.baseUrl
        }
        PreferenceTextField(
            headline = stringResource(R.string.screen_advanced_settings_element_call_base_url),
            value = callUrlState.baseUrl,
            placeholder = "https://.../room",
            supportingText = supportingText,
            validation = callUrlState.validator,
            onValidationErrorMessage = stringResource(R.string.screen_advanced_settings_element_call_base_url_validation_error),
            displayValue = { value -> !value.isNullOrEmpty() },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false, keyboardType = KeyboardType.Uri),
            onChange = { state.eventSink(DeveloperSettingsEvents.SetCustomElementCallBaseUrl(it)) }
        )
    }
}

@Composable
private fun NotificationCategory(onPushHistoryClick: () -> Unit) {
    PreferenceCategory(title = stringResource(id = R.string.screen_notification_settings_title)) {
        ListItem(
            headlineContent = {
                Text(stringResource(R.string.troubleshoot_notifications_entry_point_push_history_title))
            },
            onClick = onPushHistoryClick,
        )
    }
}

@Composable
private fun FeatureListContent(
    state: DeveloperSettingsState,
) {
    fun onFeatureEnabled(feature: FeatureUiModel, isEnabled: Boolean) {
        state.eventSink(DeveloperSettingsEvents.UpdateEnabledFeature(feature, isEnabled))
    }

    FeatureListView(
        features = state.features,
        onCheckedChange = ::onFeatureEnabled,
    )
}

@Composable
private fun UserAccountCategory(
    state: DeveloperSettingsState,
) {
    val showDeleteAccountConfirmation = remember { mutableStateOf(false) }

    val onDismiss: () -> Unit = {
        showDeleteAccountConfirmation.value = false
    }

    PreferenceCategory(title = "User Account", showTopDivider = false) {
        ListItem(
            headlineContent = {
                Text("Delete account")
            },
            trailingContent = if (state.isDeleteAccountInProgress) {
                ListItemContent.Custom {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .progressSemantics()
                            .size(20.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                null
            },
            onClick = {
                if (state.isDeleteAccountInProgress.not()) {
                    // show delete account confirmation dialog
                    showDeleteAccountConfirmation.value = true
                }
            }
        )
    }

    if (showDeleteAccountConfirmation.value) {
        ZeroAlertDialog(
            title = "Delete Account",
            message = "Are you sure you want to delete your account? You won't be able to recover your account later!",
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDismiss()
                    state.eventSink(DeveloperSettingsEvents.DeleteUserAccount)
                }) {
                    Text(text = "Confirm", color = ElementTheme.colors.textPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel", color = ElementTheme.colors.textPrimary)
                }
            }
        )
    }
}

@PreviewsDayNight
@Composable
internal fun DeveloperSettingsViewPreview(@PreviewParameter(DeveloperSettingsStateProvider::class) state: DeveloperSettingsState) = ElementPreview {
    DeveloperSettingsView(
        state = state,
        onOpenShowkase = {},
        onPushHistoryClick = {},
        onBackClick = {}
    )
}
