/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.developer

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import io.element.android.compound.theme.ElementTheme
import io.element.android.features.preferences.impl.R
import io.element.android.features.preferences.impl.developer.tracing.LogLevelItem
import io.element.android.features.rageshake.api.preferences.RageshakePreferencesView
import io.element.android.libraries.designsystem.components.preferences.PreferenceCategory
import io.element.android.libraries.designsystem.components.preferences.PreferenceDropdown
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.components.preferences.PreferenceSwitch
import io.element.android.libraries.designsystem.components.preferences.PreferenceText
import io.element.android.libraries.designsystem.components.preferences.PreferenceTextField
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.featureflag.ui.FeatureListView
import io.element.android.libraries.featureflag.ui.model.FeatureUiModel
import io.element.android.libraries.ui.strings.CommonStrings
import kotlinx.collections.immutable.toPersistentList
import io.element.android.support.zero.common.ui.component.ZeroAlertDialog

@Composable
fun DeveloperSettingsView(
    state: DeveloperSettingsState,
    onOpenShowkase: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        //title = stringResource(id = CommonStrings.common_developer_options)
        title = stringResource(id = CommonStrings.common_advanced_settings)
    ) {
        // Note: this is OK to hardcode strings in this debug screen.
        /*SettingsCategory(state)
        PreferenceCategory(
            title = "Feature flags",
            showTopDivider = true,
        ) {
            FeatureListContent(state)
        }
        ElementCallCategory(state = state)
        PreferenceCategory(title = "Rust SDK") {
            PreferenceDropdown(
                title = "Tracing log level",
                supportingText = "Requires app reboot",
                selectedOption = state.tracingLogLevel.dataOrNull(),
                options = LogLevelItem.entries.toPersistentList(),
                onSelectOption = { logLevel ->
                     state.eventSink(DeveloperSettingsEvents.SetTracingLogLevel(logLevel))
                }
            )
        }
        PreferenceCategory(title = "Showkase") {
            PreferenceText(
                title = "Open Showkase browser",
                onClick = onOpenShowkase
            )
        }
        RageshakePreferencesView(
            state = state.rageshakeState,
        )
        PreferenceCategory(title = "Crash", showTopDivider = false) {
            PreferenceText(
                title = "Crash the app 💥",
                onClick = { error("This crash is a test.") }
            )
        }*/
        UserAccountCategory(state = state)

        val cache = state.cacheSize
        PreferenceCategory(title = "Cache", showTopDivider = false) {
            PreferenceText(
                title = "Clear cache",
                currentValue = cache.dataOrNull(),
                loadingCurrentValue = state.cacheSize.isLoading() || state.clearCacheAction.isLoading(),
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
private fun SettingsCategory(
    state: DeveloperSettingsState,
) {
    PreferenceCategory(title = "Preferences", showTopDivider = false) {
        PreferenceSwitch(
            title = "Hide image & video previews",
            subtitle = "When toggled image & video will not render in the timeline by default.",
            isChecked = state.hideImagesAndVideos,
            onCheckedChange = {
                state.eventSink(DeveloperSettingsEvents.SetHideImagesAndVideos(it))
            }
        )
    }
}

@Composable
private fun ElementCallCategory(
    state: DeveloperSettingsState,
) {
    PreferenceCategory(title = "Element Call", showTopDivider = true) {
        val callUrlState = state.customElementCallBaseUrlState
        fun isUsingDefaultUrl(value: String?): Boolean {
            return value.isNullOrEmpty() || value == callUrlState.defaultUrl
        }

        val supportingText = if (isUsingDefaultUrl(callUrlState.baseUrl)) {
            stringResource(R.string.screen_advanced_settings_element_call_base_url_description)
        } else {
            callUrlState.baseUrl
        }
        PreferenceTextField(
            headline = stringResource(R.string.screen_advanced_settings_element_call_base_url),
            value = callUrlState.baseUrl ?: callUrlState.defaultUrl,
            supportingText = supportingText,
            validation = callUrlState.validator,
            onValidationErrorMessage = stringResource(R.string.screen_advanced_settings_element_call_base_url_validation_error),
            displayValue = { value -> !isUsingDefaultUrl(value) },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = false, keyboardType = KeyboardType.Uri),
            onChange = { state.eventSink(DeveloperSettingsEvents.SetCustomElementCallBaseUrl(it)) }
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
        PreferenceText(
            title = "Delete account",
            loadingCurrentValue = state.isDeleteAccountInProgress,
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
        onBackClick = {}
    )
}
