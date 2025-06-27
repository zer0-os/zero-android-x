/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.preferences.impl.root

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.preferences.impl.R
import io.element.android.features.preferences.impl.user.UserPreferences
import io.element.android.libraries.architecture.coverage.ExcludeFromCoverage
import io.element.android.libraries.designsystem.components.list.ListItemContent
import io.element.android.libraries.designsystem.components.preferences.PreferencePage
import io.element.android.libraries.designsystem.preview.ElementPreviewDark
import io.element.android.libraries.designsystem.preview.ElementPreviewLight
import io.element.android.libraries.designsystem.preview.PreviewWithLargeHeight
import io.element.android.libraries.designsystem.theme.components.HorizontalDivider
import io.element.android.libraries.designsystem.theme.components.IconSource
import io.element.android.libraries.designsystem.theme.components.ListItem
import io.element.android.libraries.designsystem.theme.components.ListItemStyle
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha20
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha50
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.designsystem.utils.snackbar.SnackbarHost
import io.element.android.libraries.designsystem.utils.snackbar.rememberSnackbarHostState
import io.element.android.libraries.matrix.api.core.DeviceId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.ui.components.MatrixUserProvider
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import io.element.android.support.zero.data.model.helper.RewardsUtil

@Composable
fun PreferencesRootView(
    state: PreferencesRootState,
    onBackClick: () -> Unit,
    onSecureBackupClick: () -> Unit,
    onManageAccountClick: (url: String) -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenRageShake: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenDeveloperSettings: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenUserProfile: (MatrixUser) -> Unit,
    onOpenBlockedUsers: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    onOpenRewards: () -> Unit,
    onInviteFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSnackbarHostState(snackbarMessage = state.snackbarMessage)

    // Include pref from other modules
    PreferencePage(
        modifier = modifier,
        onBackClick = onBackClick,
        // title = stringResource(id = CommonStrings.common_settings),
        title = "",
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        UserPreferences(
            modifier = Modifier.clickable {
                onOpenUserProfile(state.myUser)
            },
            user = state.myUser,
        )

        // 'Zero User Rewards' section
        RewardsSection(
            shouldShowNewRewardsIntimation = state.shouldShowNewRewardsIntimation,
            userRewards = state.userRewards,
            onRewardsClicked = onOpenRewards,
            onDismissRewardsIntimation = {
                state.eventSink(PreferencesRootEvents.DismissRewardsIntimation)
            }
        )

        // 'Manage my app' section
        ManageAppSection(
            state = state,
            onOpenNotificationSettings = onOpenNotificationSettings,
            onOpenLockScreenSettings = onOpenLockScreenSettings,
            onSecureBackupClick = onSecureBackupClick,
            onInviteFriend = onInviteFriend
        )

        /*// 'Account' section
        ManageAccountSection(
            state = state,
            onManageAccountClick = onManageAccountClick,
            onOpenBlockedUsers = onOpenBlockedUsers
        )*/

        // General section
        GeneralSection(
            state = state,
            onOpenAbout = onOpenAbout,
            onOpenAnalytics = onOpenAnalytics,
            onOpenRageShake = onOpenRageShake,
            onOpenAdvancedSettings = onOpenAdvancedSettings,
            onOpenDeveloperSettings = onOpenDeveloperSettings,
            onSignOutClick = onSignOutClick,
            onDeactivateClick = onDeactivateClick,
        )

        /*Footer(
            version = state.version,
            deviceId = state.deviceId,
            onClick = if (!state.showDeveloperSettings) {
                { state.eventSink(PreferencesRootEvents.OnVersionInfoClick) }
            } else {
                null
            }
        )*/
    }
}

@Composable
private fun ColumnScope.RewardsSection(
    shouldShowNewRewardsIntimation: Boolean,
    userRewards: ZeroUserRewards,
    onRewardsClicked: () -> Unit,
    onDismissRewardsIntimation: () -> Unit
) {
    LaunchedEffect(userRewards) {
        if (shouldShowNewRewardsIntimation) {
            onDismissRewardsIntimation()
        }
    }
    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
        Card(
            modifier = Modifier
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, ElementTheme.colors.zeroBrandColorAlpha20),
            onClick = onRewardsClicked,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 80.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    if (shouldShowNewRewardsIntimation) {
                        Box(modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(start = 80.dp, bottom = 30.dp)
                        ) {
                            Box(
                                modifier =
                                Modifier
                                    .size(18.dp)
                                    .align(Alignment.Center)
                                    .background(color = Color.Transparent, shape = CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = ElementTheme.colors.zeroBrandColorAlpha20,
                                        shape = CircleShape
                                    )
                            )
                            Box(
                                modifier =
                                Modifier
                                    .size(14.dp)
                                    .align(Alignment.Center)
                                    .background(color = Color.Transparent, shape = CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = ElementTheme.colors.zeroBrandColorAlpha50,
                                        shape = CircleShape
                                    )
                            )
                            Box(
                                modifier =
                                Modifier
                                    .size(10.dp)
                                    .align(Alignment.Center)
                                    .background(color = ElementTheme.colors.zeroBrandColor, shape = CircleShape)
                            )
                        }
                    }

                    val refPrice = RewardsUtil.getRefPrice(
                        zero = userRewards.zero,
                        decimals = userRewards.decimals,
                        refPrice = userRewards.price
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "\$$refPrice".trim(),
                        style = ElementTheme.zeroTypography.fontHeadingSmMediumRoboto,
                        color = ElementTheme.colors.textPrimary
                    )
                }

                val credits = RewardsUtil.getEarnedRewardsFormatted(
                    zero = userRewards.zero,
                    decimals = userRewards.decimals
                )
                Text(
                    text = "$credits MEOW",
                    style = ElementTheme.zeroTypography.fontBodySmRegularRoboto,
                    color = ElementTheme.colors.textSecondary
                )
            }
        }
    }

    Spacer(Modifier.size(SPACING_4X.dp))
}

@Composable
private fun ColumnScope.ManageAppSection(
    state: PreferencesRootState,
    onOpenNotificationSettings: () -> Unit,
    onOpenLockScreenSettings: () -> Unit,
    onSecureBackupClick: () -> Unit,
    onInviteFriend: () -> Unit,
) {
    ListItem(
        headlineContent = { Text("Refer A Friend") },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Plus())),
        onClick = onInviteFriend,
    )

    if (state.showNotificationSettings) {
        ListItem(
            headlineContent = { Text(stringResource(id = R.string.screen_notification_settings_title)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Notifications())),
            onClick = onOpenNotificationSettings,
        )
    }
    /*if (state.showLockScreenSettings) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_screen_lock)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Lock())),
            onClick = onOpenLockScreenSettings,
        )
    }*/
    /*if (state.showSecureBackup) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_encryption)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
            trailingContent = ListItemContent.Badge.takeIf { state.showSecureBackupBadge },
            onClick = onSecureBackupClick,
        )
    }*/
    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.common_encryption)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Key())),
        trailingContent = ListItemContent.Badge.takeIf { state.showSecureBackupBadge },
        onClick = onSecureBackupClick,
    )
    if (state.showNotificationSettings || state.showLockScreenSettings || state.showSecureBackup) {
        HorizontalDivider()
    }
}

@Composable
private fun ColumnScope.ManageAccountSection(
    state: PreferencesRootState,
    onManageAccountClick: (url: String) -> Unit,
    onOpenBlockedUsers: () -> Unit,
) {
    state.accountManagementUrl?.let { url ->
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_account)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.UserProfile())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.PopOut())),
            onClick = { onManageAccountClick(url) },
        )
    }

    state.devicesManagementUrl?.let { url ->
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_manage_devices)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Devices())),
            trailingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.PopOut())),
            onClick = { onManageAccountClick(url) },
        )
    }

    if (state.showBlockedUsersItem) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_blocked_users)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Block())),
            onClick = onOpenBlockedUsers,
        )
    }

    if (state.accountManagementUrl != null || state.devicesManagementUrl != null || state.showBlockedUsersItem) {
        HorizontalDivider()
    }
}

@Composable
private fun ColumnScope.GeneralSection(
    state: PreferencesRootState,
    onOpenAbout: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenRageShake: () -> Unit,
    onOpenAdvancedSettings: () -> Unit,
    onOpenDeveloperSettings: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeactivateClick: () -> Unit,
) {
    /*    ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_about)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Info())),
            onClick = onOpenAbout,
        )
        if (state.canReportBug) {ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_report_a_problem)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.ChatProblem())),
            onClick = onOpenRageShake)
        }
        if (state.showAnalyticsSettings) {
            ListItem(
                headlineContent = { Text(stringResource(id = CommonStrings.common_analytics)) },
                leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Chart())),
                onClick = onOpenAnalytics,
            )
        }
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.common_advanced_settings)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Settings())),
            onClick = onOpenAdvancedSettings,
        )*/
    if (state.showDeveloperSettings) {
        DeveloperPreferencesView(onOpenDeveloperSettings)
    }
    ListItem(
        headlineContent = { Text(stringResource(id = CommonStrings.action_signout)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.SignOut())),
        style = ListItemStyle.Destructive,
        onClick = onSignOutClick,
    )
    /*if (state.canDeactivateAccount) {
        ListItem(
            headlineContent = { Text(stringResource(id = CommonStrings.action_deactivate_account)) },
            leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Warning())),
            style = ListItemStyle.Destructive,
            onClick = onDeactivateClick,
        )
    }*/
}

@Composable
private fun ColumnScope.Footer(
    version: String,
    deviceId: DeviceId?,
    onClick: (() -> Unit)?,
) {
    val text = remember(version, deviceId) {
        buildString {
            append(version)
            if (deviceId != null) {
                append("\n")
                append(deviceId)
            }
        }
    }
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 16.dp)
            .clickable(enabled = onClick != null, onClick = onClick ?: {})
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
        textAlign = TextAlign.Center,
        text = text,
        style = ElementTheme.zeroTypography.fontBodySmRegular,
        color = ElementTheme.colors.textSecondary,
    )
}

@Composable
private fun DeveloperPreferencesView(onOpenDeveloperSettings: () -> Unit) {
    ListItem(
        //headlineContent = { Text(stringResource(id = CommonStrings.common_developer_options)) },
        headlineContent = { Text(stringResource(id = CommonStrings.common_advanced_settings)) },
        leadingContent = ListItemContent.Icon(IconSource.Vector(CompoundIcons.Code())),
        onClick = onOpenDeveloperSettings
    )
}

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewLightPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) =
    ElementPreviewLight { ContentToPreview(matrixUser) }

@PreviewWithLargeHeight
@Composable
internal fun PreferencesRootViewDarkPreview(@PreviewParameter(MatrixUserProvider::class) matrixUser: MatrixUser) =
    ElementPreviewDark { ContentToPreview(matrixUser) }

@ExcludeFromCoverage
@Composable
private fun ContentToPreview(matrixUser: MatrixUser) {
    PreferencesRootView(
        state = aPreferencesRootState(myUser = matrixUser),
        onBackClick = {},
        onOpenAnalytics = {},
        onOpenRageShake = {},
        onOpenDeveloperSettings = {},
        onOpenAdvancedSettings = {},
        onOpenAbout = {},
        onSecureBackupClick = {},
        onManageAccountClick = {},
        onOpenNotificationSettings = {},
        onOpenLockScreenSettings = {},
        onOpenUserProfile = {},
        onOpenBlockedUsers = {},
        onSignOutClick = {},
        onDeactivateClick = {},
        onOpenRewards = {},
        onInviteFriend = {}
    )
}
