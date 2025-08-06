/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.rewards.ZeroMeowPrice
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.R
import io.element.android.support.zero.common.state.StateBus
import io.element.android.support.zero.data.model.helper.RewardsUtil

@Composable
fun ClaimRewardsSheet(
    modifier: Modifier = Modifier,
    userRewards: ZeroUserRewards,
    meowPrice: ZeroMeowPrice?,
    actionState: AsyncAction<String>,
    onViewTransaction: (String) -> Unit = {},
    onClaimRewards: () -> Unit = {},
    onRewardsClaimed: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        onClaimRewards()
    }

    val unclaimedRewards = RewardsUtil.getEarnedRewardsFormatted(userRewards.unclaimedRewards, userRewards.decimals)
    val unclaimedRewardsRefPrice = meowPrice?.let {
        ZeroWalletUtil.getMeowTokenPriceFormatted(
            tokenAmount = userRewards.unclaimedRewards.toDoubleOrNull() ?: 0.0,
            meowPrice = it
        )
    } ?: 0
    val headerText = when (actionState) {
        AsyncAction.Loading -> "Processing Claim..."
        is AsyncAction.Failure -> "Claim Failed"
        else -> "$unclaimedRewards MEOW"
    }
    val headerTextColor = when (actionState) {
        AsyncAction.Loading -> ElementTheme.colors.textPrimary
        is AsyncAction.Failure -> ElementTheme.colors.textCriticalPrimary
        else -> ElementTheme.colors.zeroBrandColor
    }
    val descriptionText = when (actionState) {
        AsyncAction.Loading -> "Please wait while we process your claim."
        is AsyncAction.Failure -> "No rewards available to claim at this time."
        else -> "$$unclaimedRewardsRefPrice"
    }
    val secondaryButtonText = when (actionState) {
        is AsyncAction.Failure -> "Try Again"
        else -> "View"
    }
    val onSecondaryButtonClick: () -> Unit = {
        when (actionState) {
            is AsyncAction.Failure -> onClaimRewards()
            is AsyncAction.Success -> {
                StateBus.onRewardsClaimed()
                onViewTransaction(actionState.data)
            }
            else -> {}
        }
    }

    if (actionState is AsyncAction.Success) {
        onRewardsClaimed()
    }

    Box(
        modifier = modifier
            .height(400.dp)
            .padding(horizontal = 12.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.3f),
                clip = true
            )
    ) {
        ClaimRewardsSheetBackground()

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Claim Earnings",
                        style = ElementTheme.typography.fontHeadingSmMedium,
                        color = ElementTheme.colors.textPrimary
                    )

                    IconButton(
                        modifier = Modifier.background(
                            color = ElementTheme.colors.bgCanvasDefault,
                            shape = CircleShape
                        ),
                        onClick = {
                            StateBus.onRewardsClaimed()
                        }
                    ) { Icon(imageVector = CompoundIcons.Close(), contentDescription = null) }
                }

                if (actionState is AsyncAction.Success) {
                    Text(
                        text = "Your daily earnings have been added to you Wallet.",
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(100.dp)
                        .background(
                            color = ElementTheme.colors.bgCanvasDefault,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = ElementTheme.colors.zeroBrandColor,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_post_meow),
                        contentDescription = null,
                        tint = ElementTheme.colors.zeroBrandColor
                    )
                }
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = headerText,
                    style = ElementTheme.typography.fontHeadingSmRegular,
                    color = headerTextColor
                )
                Text(
                    text = descriptionText,
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textSecondary
                )
            }

            Row(Modifier.fillMaxWidth()) {
                if (actionState is AsyncAction.Success || actionState is AsyncAction.Failure) {
                    SecondaryButton(
                        text = secondaryButtonText,
                        onClick = onSecondaryButtonClick
                    )
                    Spacer(Modifier.size(12.dp))
                    CloseButton()
                }
            }
        }
    }
}

@Composable
fun RowScope.SecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .weight(1f)
            .border(
                width = 1.dp,
                color = ElementTheme.colors.zeroBrandColor,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        Text(
            text,
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = ElementTheme.colors.textPrimary
        )
    }
}

@Composable
fun RowScope.CloseButton(
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.weight(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor
        ),
        onClick = { StateBus.onRewardsClaimed() }
    ) {
        Text(
            "Close",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ClaimRewardsSheetBackground(
    accentColor: Color = ElementTheme.colors.zeroBrandColor
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.75f),
                        Color.Black,
                        Color.Black,
                        accentColor.copy(alpha = 0.5f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.5f),
                        accentColor.copy(alpha = 0.25f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    )
}

@PreviewsDayNight
@Composable
fun ClaimRewardsSheetPreview() = ElementTheme {
    ClaimRewardsSheet(
        userRewards = ZeroUserRewards.empty(),
        actionState = AsyncAction.Uninitialized,
        meowPrice = null
    )
}
