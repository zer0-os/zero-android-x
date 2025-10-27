/*
 * Copyright 2023, 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
========
import androidx.compose.foundation.layout.height
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
========
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.element.android.appconfig.RoomListConfig
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.filters.RoomListFiltersState
import io.element.android.features.home.impl.filters.RoomListFiltersView
import io.element.android.features.home.impl.roomlist.RoomListMenuAction
import io.element.android.libraries.designsystem.atomic.atoms.RedIndicatorAtom
import io.element.android.libraries.designsystem.components.TopAppBarScrollBehaviorLayout
import io.element.android.libraries.designsystem.components.avatar.Avatar
import io.element.android.libraries.designsystem.components.avatar.AvatarData
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.designsystem.components.avatar.AvatarType
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
import io.element.android.libraries.designsystem.text.applyScaleDown
import io.element.android.libraries.designsystem.text.toSp
========
import io.element.android.libraries.designsystem.theme.aliasScreenTitle
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
import io.element.android.libraries.designsystem.theme.components.DropdownMenu
import io.element.android.libraries.designsystem.theme.components.DropdownMenuItem
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha20
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColorAlpha50
import io.element.android.libraries.designsystem.theme.zero.color.zeroDialogBackgroundColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
========
import io.element.android.libraries.designsystem.theme.components.TopAppBar
import io.element.android.libraries.matrix.api.core.SessionId
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
import io.element.android.libraries.matrix.api.core.UserId
import io.element.android.libraries.matrix.api.user.MatrixUser
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.getBestName
import io.element.android.libraries.testtags.TestTags
import io.element.android.libraries.testtags.testTag
import io.element.android.libraries.ui.strings.CommonStrings
import io.element.android.support.zero.R
import io.element.android.support.zero.data.model.helper.RewardsUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
fun HomeScreenTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    matrixUser: MatrixUser,
========
fun HomeTopBar(
    title: String,
    currentUserAndNeighbors: ImmutableList<MatrixUser>,
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
    showAvatarIndicator: Boolean,
    customTabs: (@Composable () -> Unit)? = null,
    areSearchResultsDisplayed: Boolean,
    onToggleSearch: () -> Unit,
    onOpenProfile: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    onOpenSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    displayMenuItems: Boolean,
    canReportBug: Boolean,
    displayFilters: Boolean,
    filtersState: RoomListFiltersState,
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
    canReportBug: Boolean,
    shouldShowNewRewardsIntimation: Boolean,
    userRewards: ZeroUserRewards,
    onDismissRewardsTooltip: (Boolean) -> Unit,
) {
    ZeroStyledTopBar(
        matrixUser = matrixUser,
        showAvatarIndicator = showAvatarIndicator,
        customTabs = customTabs,
        onOpenSettings = onOpenSettings,
        onSearchClick = onToggleSearch,
        onOpenProfile = onOpenProfile,
        scrollBehavior = scrollBehavior,
        displayMenuItems = displayMenuItems,
        modifier = modifier,
        shouldShowNewRewardsIntimation = shouldShowNewRewardsIntimation,
        userRewards = userRewards,
        onDismissRewardsTooltip = onDismissRewardsTooltip
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZeroStyledTopBar(
    matrixUser: MatrixUser,
    showAvatarIndicator: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    customTabs: (@Composable () -> Unit)? = null,
    onOpenSettings: () -> Unit,
    onSearchClick: () -> Unit,
    onOpenProfile: () -> Unit,
    displayMenuItems: Boolean,
    modifier: Modifier = Modifier,
    shouldShowNewRewardsIntimation: Boolean,
    userRewards: ZeroUserRewards,
    onDismissRewardsTooltip: (Boolean) -> Unit = {},
) {
    val avatarData by remember(matrixUser) {
        derivedStateOf {
            matrixUser.getAvatarData(size = AvatarSize.CurrentUserTopBar)
        }
    }

    Box(modifier = modifier) {
        Column {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.home_zero_title),
                        contentDescription = "User Profile",
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                navigationIcon = {
                    NavigationIcon(
                        avatarData = avatarData,
                        showAvatarIndicator = showAvatarIndicator,
                        onClick = onOpenSettings,
                        shouldShowNewRewardsIntimation = shouldShowNewRewardsIntimation,
                        userRewards = userRewards,
                        onDismissRewardsTooltip = onDismissRewardsTooltip
                    )
                },
                actions = {
                    if (displayMenuItems) {
                        IconButton(
                            onClick = onSearchClick,
                        ) {
                            Icon(
                                imageVector = CompoundIcons.Search(),
                                contentDescription = stringResource(CommonStrings.action_search),
                            )
                        }
                    }
                    IconButton(
                        onClick = onOpenProfile,
                    ) {
                        Icon(
                            painter = painterResource( R.drawable.home_tab_profile_icon),
                            contentDescription = "User Profile",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                windowInsets = WindowInsets(0.dp),
            )
            customTabs?.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultRoomListTopBar(
    title: String,
    matrixUser: MatrixUser,
    showAvatarIndicator: Boolean,
    areSearchResultsDisplayed: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onOpenSettings: () -> Unit,
    onSearchClick: () -> Unit,
    onMenuActionClick: (RoomListMenuAction) -> Unit,
    displayMenuItems: Boolean,
    displayFilters: Boolean,
    filtersState: RoomListFiltersState,
    canReportBug: Boolean,
    modifier: Modifier = Modifier,
    shouldShowNewRewardsIntimation: Boolean,
    userRewards: ZeroUserRewards,
    onDismissRewardsTooltip: (Boolean) -> Unit = {},
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val avatarData by remember(matrixUser) {
        derivedStateOf {
            matrixUser.getAvatarData(size = AvatarSize.CurrentUserTopBar)
        }
    }

    Box(modifier = modifier) {
        val collapsedTitleTextStyle = ElementTheme.zeroTypography.aliasScreenTitle
        val expandedTitleTextStyle = ElementTheme.zeroTypography.fontHeadingLgBold.copy(
            // Due to a limitation of MediumTopAppBar, and to avoid the text to be truncated,
            // ensure that the font size will never be bigger than 28.dp.
            fontSize = 28.dp.applyScaleDown().toSp()
        )
        MaterialTheme(
            colorScheme = ElementTheme.materialColors,
            shapes = MaterialTheme.shapes,
            typography = ElementTheme.materialTypography.copy(
                headlineSmall = expandedTitleTextStyle,
                titleLarge = collapsedTitleTextStyle
            ),
        ) {
            Column {
                MediumTopAppBar(
                    modifier = Modifier
//                        .backgroundVerticalGradient(
//                            isVisible = !areSearchResultsDisplayed,
//                        )
                        .statusBarsPadding(),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                    ),
                    title = {
                        Column {
                            Text(
                                text = matrixUser.getBestName(),
                                maxLines = 1,
                                style = ElementTheme.zeroTypography.fontHeadingSmMedium,
                            )
                            if (matrixUser.primaryZeroId.isNullOrEmpty().not()) {
                                Text(
                                    text = matrixUser.primaryZeroId!!,
                                    maxLines = 1,
                                    style = ElementTheme.zeroTypography.fontBodySmRegular,
                                    color = ElementTheme.colors.textSecondary
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        NavigationIcon(
                            avatarData = avatarData,
                            showAvatarIndicator = showAvatarIndicator,
                            onClick = onOpenSettings,
                            shouldShowNewRewardsIntimation = shouldShowNewRewardsIntimation,
                            userRewards = userRewards,
                            onDismissRewardsTooltip = onDismissRewardsTooltip
========
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        TopAppBar(
            modifier = Modifier
                .backgroundVerticalGradient(
                    isVisible = !areSearchResultsDisplayed,
                )
                .statusBarsPadding(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            title = {
                Text(
                    modifier = Modifier.semantics {
                        heading()
                    },
                    style = ElementTheme.typography.aliasScreenTitle,
                    text = title,
                )
            },
            navigationIcon = {
                NavigationIcon(
                    currentUserAndNeighbors = currentUserAndNeighbors,
                    showAvatarIndicator = showAvatarIndicator,
                    onAccountSwitch = onAccountSwitch,
                    onClick = onOpenSettings,
                )
            },
            actions = {
                if (displayMenuItems) {
                    IconButton(
                        onClick = onToggleSearch,
                    ) {
                        Icon(
                            imageVector = CompoundIcons.Search(),
                            contentDescription = stringResource(CommonStrings.action_search),
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
                        )
                    }
                    if (RoomListConfig.HAS_DROP_DOWN_MENU) {
                        var showMenu by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { showMenu = !showMenu }
                        ) {
                            Icon(
                                imageVector = CompoundIcons.OverflowVertical(),
                                contentDescription = null,
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (RoomListConfig.SHOW_INVITE_MENU_ITEM) {
                                DropdownMenuItem(
                                    onClick = {
                                        showMenu = false
                                        onMenuActionClick(RoomListMenuAction.InviteFriends)
                                    },
                                    text = { Text(stringResource(id = CommonStrings.action_invite)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = CompoundIcons.ShareAndroid(),
                                            tint = ElementTheme.colors.iconSecondary,
                                            contentDescription = null,
                                        )
                                    }
                                )
                            }
                            if (RoomListConfig.SHOW_REPORT_PROBLEM_MENU_ITEM && canReportBug) {
                                DropdownMenuItem(
                                    onClick = {
                                        showMenu = false
                                        onMenuActionClick(RoomListMenuAction.ReportBug)
                                    },
                                    text = { Text(stringResource(id = CommonStrings.common_report_a_problem)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = CompoundIcons.ChatProblem(),
                                            tint = ElementTheme.colors.iconSecondary,
                                            contentDescription = null,
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            },
            // We want a 16dp left padding for the navigationIcon :
            // 4dp from default TopAppBarHorizontalPadding
            // 8dp from AccountIcon default padding (because of IconButton)
            // 4dp extra padding using left insets
            windowInsets = WindowInsets(left = 4.dp),
        )
        if (displayFilters) {
            TopAppBarScrollBehaviorLayout(scrollBehavior = scrollBehavior) {
                RoomListFiltersView(
                    state = filtersState,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(
    avatarData: AvatarData,
    showAvatarIndicator: Boolean,
    shouldShowNewRewardsIntimation: Boolean,
    userRewards: ZeroUserRewards,
    onClick: () -> Unit,
    onDismissRewardsTooltip: (Boolean) -> Unit,
) {
    LaunchedEffect(userRewards) {
        if (shouldShowNewRewardsIntimation) {
            onDismissRewardsTooltip(false)
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            modifier = Modifier.testTag(TestTags.homeScreenSettings),
            onClick = onClick,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (shouldShowNewRewardsIntimation) {
                    NewRewardsIntimationGlow(avatarSize = avatarData.size.dp)
                }
                Avatar(
                    avatarData = avatarData,
                    avatarType = AvatarType.User, contentDescription = stringResource(CommonStrings.common_settings),
                )
                if (showAvatarIndicator) {
                    RedIndicatorAtom(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                }
            }
        }

        if (shouldShowNewRewardsIntimation) {
            UserRewardsToolTip(userRewards) {
                onDismissRewardsTooltip(true)
            }
        }
    }
}

@Composable
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
private fun NewRewardsIntimationGlow(avatarSize: Dp) {
    Box {
        Box(
            modifier = Modifier
                .size((avatarSize.value.plus(8)).dp)
                .align(Alignment.Center)
                .background(color = Color.Transparent, shape = CircleShape)
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.zeroBrandColorAlpha20,
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size((avatarSize.value.plus(4)).dp)
                .align(Alignment.Center)
                .background(color = Color.Transparent, shape = CircleShape)
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.zeroBrandColorAlpha50,
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun UserRewardsToolTip(
    userRewards: ZeroUserRewards,
    onDismissRewardsTooltip: () -> Unit,
) {
    Row(
        modifier = Modifier.offset(x = -16.dp),
        verticalAlignment = Alignment.CenterVertically
========
private fun AccountIcon(
    matrixUser: MatrixUser,
    isCurrentAccount: Boolean,
    showAvatarIndicator: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val testTag = if (isCurrentAccount) Modifier.testTag(TestTags.homeScreenSettings) else Modifier
    IconButton(
        modifier = modifier.then(testTag),
        onClick = onClick,
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
    ) {
        val earnedRewards = RewardsUtil.getRefPrice(
            zero = userRewards.zero,
            decimals = userRewards.decimals,
            refPrice = userRewards.price
        )
        Icon(
            modifier = Modifier
                .rotate(180f)
                .size(32.dp)
                .offset(x = -12.dp),
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = ElementTheme.colors.zeroDialogBackgroundColor
        )
        Row(
            modifier = Modifier
                .background(
                    color = ElementTheme.colors.zeroDialogBackgroundColor,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "You earned \$$earnedRewards",
                style = ElementTheme.zeroTypography.fontBodyLgRegular,
                color = ElementTheme.colors.zeroBrandColor
            )
            Spacer(Modifier.size(12.dp))
            IconButton(
                onClick = onDismissRewardsTooltip
            ) {
                Icon(
                    imageVector = CompoundIcons.Close(),
                    contentDescription = null,
                    tint = ElementTheme.colors.textPrimary
                )
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarPreview() = ElementPreview {
    HomeTopBar(
        title = stringResource(R.string.screen_roomlist_main_space_title),
        matrixUser = MatrixUser(UserId("@id:domain"), "Alice"),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
        onSearchClick = {},
========
        onAccountSwitch = {},
        onToggleSearch = {},
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
        displayMenuItems = true,
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        onMenuActionClick = {},
        shouldShowNewRewardsIntimation = true,
        userRewards = ZeroUserRewards.empty()
    )
}*/

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
<<<<<<<< HEAD:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeScreenTopBar.kt
internal fun DefaultRoomListTopBarWithIndicatorPreview() = ElementPreview {
    ZeroStyledTopBar(
        matrixUser = MatrixUser(UserId("@id:domain"), "Alice"),
        showAvatarIndicator = true,
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onSearchClick = {},
        displayMenuItems = true,
        onOpenProfile = {},
        shouldShowNewRewardsIntimation = true,
        userRewards = ZeroUserRewards.empty(),
========
internal fun HomeTopBarWithIndicatorPreview() = ElementPreview {
    HomeTopBar(
        title = stringResource(R.string.screen_roomlist_main_space_title),
        currentUserAndNeighbors = persistentListOf(MatrixUser(UserId("@id:domain"), "Alice")),
        showAvatarIndicator = true,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        displayMenuItems = true,
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        onMenuActionClick = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewsDayNight
@Composable
internal fun HomeTopBarMultiAccountPreview() = ElementPreview {
    HomeTopBar(
        title = stringResource(R.string.screen_roomlist_main_space_title),
        currentUserAndNeighbors = aMatrixUserList().take(3).toImmutableList(),
        showAvatarIndicator = false,
        areSearchResultsDisplayed = false,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        onOpenSettings = {},
        onAccountSwitch = {},
        onToggleSearch = {},
        displayMenuItems = true,
        canReportBug = true,
        displayFilters = true,
        filtersState = aRoomListFiltersState(),
        onMenuActionClick = {},
>>>>>>>> origin/develop-copy:features/home/impl/src/main/kotlin/io/element/android/features/home/impl/components/HomeTopBar.kt
    )
}
