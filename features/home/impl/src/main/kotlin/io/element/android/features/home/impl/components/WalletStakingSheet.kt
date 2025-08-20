/*
 * Copyright 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.tokens.generated.CompoundIcons
import io.element.android.features.home.impl.HomeEvents
import io.element.android.features.home.impl.model.SelectedStakePool
import io.element.android.libraries.architecture.AsyncAction
import io.element.android.libraries.designsystem.R
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.theme.components.ButtonSize
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.IconButton
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.matrix.api.zero.wallet.ZeroWalletUtil
import io.element.android.support.zero.common.ui.ClaimRewardsButton
import io.element.android.support.zero.common.ui.SwipeToConfirmButton
import io.element.android.support.zero.common.ui.TransactionInProgressView
import io.element.android.support.zero.common.ui.ZChainIcon
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_4X

@Composable
fun WalletStakingSheet(
    modifier: Modifier = Modifier,
    selectedPool: SelectedStakePool,
    actionState: AsyncAction<String>,
    eventSink: (HomeEvents.HomeWalletEvents) -> Unit
) {
    val stakeTokenName = selectedPool.stakeTokenInfo.name.uppercase()
    val transactionAmount = remember { mutableStateOf("0") }
    val isUserStaking: MutableState<Boolean?> = remember { mutableStateOf(null) }

    val onBackClick: () -> Unit = {
        isUserStaking.value = null
    }

    Box(
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .navigationBarsPadding()
    ) {
        when (actionState) {
            AsyncAction.Loading -> {
                TransactionInProgressView(
                    modifier = Modifier.size(400.dp),
                    size = 80.dp,
                    message = if (isUserStaking.value == true)
                        "Staking ${transactionAmount.value} $stakeTokenName"
                    else
                        "Unstaking ${transactionAmount.value} $stakeTokenName",
                    subMessage = "Please wait..."
                )
            }
            is AsyncAction.Success -> {
                TransactionSuccessOrFailureView(
                    pool = selectedPool,
                    isUserStaking = (isUserStaking.value == true),
                    transactionAmount = transactionAmount.value,
                    isSuccess = true,
                    onDismiss = {
                        eventSink(HomeEvents.DismissStakingSheet)
                    }
                )
            }
            is AsyncAction.Failure -> {
                TransactionSuccessOrFailureView(
                    pool = selectedPool,
                    isUserStaking = (isUserStaking.value == true),
                    transactionAmount = transactionAmount.value,
                    isSuccess = false,
                    onDismiss = {
                        eventSink(HomeEvents.DismissStakingSheet)
                    }
                )
            }
            else -> {
                when (isUserStaking.value) {
                    true, false -> {
                        val isStaking = isUserStaking.value == true
                        StakeUnstakeAmountView(
                            onBackClick = onBackClick,
                            isUserStaking = isStaking,
                            pool = selectedPool,
                            onConfirmTransaction = { amount ->
                                transactionAmount.value = amount
                                if (isStaking) {
                                    eventSink(HomeEvents.StakeAmount(amount))
                                } else {
                                    eventSink(HomeEvents.UnstakeAmount(amount))
                                }
                            }
                        )
                    }
                    else -> {
                        PoolDetailsView(
                            pool = selectedPool,
                            onStake = { isUserStaking.value = true },
                            onUnstake = { isUserStaking.value = false },
                            onClaimRewards = {
                                eventSink(HomeEvents.ClaimStakingRewards)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PoolDetailsView(
    pool: SelectedStakePool,
    onStake: () -> Unit,
    onUnstake: () -> Unit,
    onClaimRewards: () -> Unit,
) {
    val stakeTokenName = pool.stakeTokenInfo.name.uppercase()
    val rewardTokenName = pool.rewardsTokenInfo.name.uppercase()
    /// Need to check this value
    val claimableRewardsToken = pool.poolInfo.pendingRewards
    val hasClaimableRewards = claimableRewardsToken > 0

    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = "Pool Details",
            style = ElementTheme.typography.fontBodyMdMedium,
            color = ElementTheme.colors.textSecondary
        )

        Spacer(Modifier.size(SPACING_4X.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                        .clip(CircleShape),
                    model = pool.poolInfo.poolIcon,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    contentDescription = null,
                    error = painterResource(R.drawable.ic_zero_avatar_default)
                )
                ZChainIcon()
            }
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = pool.poolInfo.poolDisplayName,
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = "Reward: $rewardTokenName",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.size(SPACING_4X.dp))

        Text(
            text = "Stake your $stakeTokenName to earn $rewardTokenName rewards.",
            style = ElementTheme.typography.fontBodyLgRegular,
            color = ElementTheme.colors.textSecondary
        )

        Spacer(Modifier.size(SPACING_4X.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.align(Alignment.Center)) {
                DetailBorderedCell(
                    title = "Claimable Rewards $rewardTokenName",
                    subTitle = ZeroWalletUtil.getFormattedNumber(claimableRewardsToken)
                )
            }
            if (hasClaimableRewards) {
                ClaimRewardsButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 12.dp),
                    onClick = onClaimRewards,
                    size = ButtonSize.XSmall
                )
            }
        }

        Spacer(Modifier.size(SPACING_2X.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            DetailBorderedCell(
                title = "TVL",
                subTitle = "$${pool.poolInfo.totalStakedAmountFormatted}"
            )
            Spacer(Modifier.size(12.dp))
            DetailBorderedCell(
                title = "My Staked $stakeTokenName",
                subTitle = pool.myStakedTokensFormatted
            )
        }

        Spacer(Modifier.size(SPACING_4X.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            StakeButton(onClick = onStake)
            if (pool.myStakedTokens > 0) {
                Spacer(Modifier.size(12.dp))
                UnstakeButton(onClick = onUnstake)
            }
        }

        Spacer(Modifier.size(SPACING_4X.dp))
    }
}

@Composable
fun StakeUnstakeAmountView(
    onBackClick: () -> Unit,
    isUserStaking: Boolean = true,
    pool: SelectedStakePool,
    onConfirmTransaction: (String) -> Unit,
) {
    val transactionAmount: MutableState<String> = remember { mutableStateOf("") }

    val stakeTokenName = pool.stakeTokenInfo.name.uppercase()
    val rewardTokenName = pool.rewardsTokenInfo.name.uppercase()

    val isAmountValid: () -> Boolean = {
        val amount = transactionAmount.value.toDoubleOrNull() ?: 0.0
        amount > 0
    }

    Column(horizontalAlignment = Alignment.Start) {
        BackButton(onBackClick)

        Spacer(Modifier.size(SPACING_4X.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    modifier = Modifier
                        .size(40.dp)
                        .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                        .clip(CircleShape),
                    model = pool.poolInfo.poolIcon,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                    contentDescription = null,
                    error = painterResource(R.drawable.ic_zero_avatar_default)
                )
                ZChainIcon()
            }
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = pool.poolInfo.poolDisplayName,
                    style = ElementTheme.typography.fontBodyLgRegular,
                    color = ElementTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(vertical = 2.dp),
                    text = "Reward: $rewardTokenName",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.size(SPACING_4X.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Transparent, shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.bgCanvasDefaultLevel1,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
        ) {
            val title = if (isUserStaking) "Stake Amount" else "Unstake Amount"
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = title,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = ElementTheme.colors.textSecondary
            )

            Spacer(Modifier.size(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StakeUnstakeAmountTextField(
                    isUserStaking = isUserStaking,
                    pool = pool,
                    transferAmount = transactionAmount,
                    onAmountEntered = {
                        transactionAmount.value = it
                    }
                )

                Spacer(Modifier.size(SPACING_2X.dp))

                Text(
                    modifier = Modifier
                        .background(
                            color = ElementTheme.colors.bgCanvasDefaultLevel1,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, 8.dp),
                    text = stakeTokenName,
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.textPrimary
                )

                Spacer(Modifier.size(SPACING_2X.dp))

                Text(
                    modifier = Modifier
                        .clickable {
                            if (isUserStaking) {
                                transactionAmount.value = pool.totalAvailableTokenBalance.toString()
                            } else {
                                transactionAmount.value = pool.myStakedTokens.toString()
                            }
                        }
                        .border(
                            width = 1.dp,
                            color = ElementTheme.colors.zeroBrandColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = ElementTheme.colors.zeroBrandColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp, 8.dp),
                    text = "Max",
                    style = ElementTheme.typography.fontBodySmRegular,
                    color = ElementTheme.colors.zeroBrandColor
                )
            }

            Spacer(Modifier.size(12.dp))

            if (isUserStaking) {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "Available: ${pool.totalAvailableTokenBalanceFormatted}",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    text = "Staked: ${pool.myStakedTokensFormatted}",
                    style = ElementTheme.typography.fontBodyMdRegular,
                    color = ElementTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.size(SPACING_4X.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Transparent, shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = ElementTheme.colors.bgCanvasDefaultLevel1,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Lock Duration",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textSecondary
            )

            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "No Lock",
                style = ElementTheme.typography.fontBodyMdRegular,
                color = ElementTheme.colors.textPrimary
            )
        }

        Spacer(Modifier.size(SPACING_4X.dp))

        if (isAmountValid()) {
            SwipeToConfirmButton {
                onConfirmTransaction(transactionAmount.value)
            }
            Spacer(Modifier.size(SPACING_4X.dp))
        }
    }
}

@Composable
fun TransactionSuccessOrFailureView(
    pool: SelectedStakePool,
    isUserStaking: Boolean = true,
    transactionAmount: String,
    isSuccess: Boolean,
    onDismiss: () -> Unit,
) {
    val stakeTokenName = pool.stakeTokenInfo.name.uppercase()
    val rewardTokenName = pool.rewardsTokenInfo.name.uppercase()

    Box(Modifier.size(400.dp)) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onClick = onDismiss
        ) {
            Icon(imageVector = CompoundIcons.Close(), contentDescription = null)
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ElementTheme.colors.bgCanvasDefault, shape = CircleShape)
                            .clip(CircleShape),
                        model = pool.poolInfo.poolIcon,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                        contentDescription = null,
                        error = painterResource(R.drawable.ic_zero_avatar_default)
                    )
                    ZChainIcon()
                }
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = pool.poolInfo.poolDisplayName,
                        style = ElementTheme.typography.fontBodyLgRegular,
                        color = ElementTheme.colors.textPrimary
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 2.dp),
                        text = "Reward: $rewardTokenName",
                        style = ElementTheme.typography.fontBodyMdRegular,
                        color = ElementTheme.colors.textSecondary
                    )
                }
            }

            Spacer(Modifier.size(SPACING_4X.dp))

            val message = if (isUserStaking) {
                if (isSuccess) {
                    "You have successfully staked $transactionAmount $stakeTokenName without lock."
                } else {
                    "Failed to stake $transactionAmount $stakeTokenName without lock."
                }
            } else {
                if (isSuccess) {
                    "You have successfully unstaked $transactionAmount $stakeTokenName, and claimed your pool rewards."
                } else {
                    "Failed to unstake $transactionAmount $stakeTokenName, and claim your pool rewards."
                }
            }
            val color = if (isSuccess) {
                ElementTheme.colors.zeroBrandColor
            } else {
                ElementTheme.colors.textCriticalPrimary
            }
            Text(
                text = message,
                style = ElementTheme.typography.fontBodyLgRegular,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RowScope.DetailBorderedCell(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
) {
    Column(
        modifier = modifier
            .weight(1f)
            .background(
                color = Color.Transparent, shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = ElementTheme.colors.bgCanvasDefaultLevel1,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = ElementTheme.typography.fontBodyMdRegular,
            color = ElementTheme.colors.textSecondary,
        )

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = subTitle,
            style = ElementTheme.typography.fontHeadingMdBold,
            color = ElementTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun RowScope.UnstakeButton(
    modifier: Modifier = Modifier,
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
            "Unstake",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp),
            color = ElementTheme.colors.zeroBrandColor
        )
    }
}

@Composable
fun RowScope.StakeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier.weight(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = ElementTheme.colors.zeroBrandColor
        ),
        onClick = onClick
    ) {
        Text(
            "Stake",
            style = ElementTheme.typography.fontBodyLgMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun RowScope.StakeUnstakeAmountTextField(
    modifier: Modifier = Modifier,
    isUserStaking: Boolean,
    pool: SelectedStakePool,
    transferAmount: MutableState<String>,
    onAmountEntered: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val onAmountChanged: (String) -> Unit = {
        val maxAmount = if (isUserStaking) pool.totalAvailableTokenBalance else pool.myStakedTokens
        val enteredAmount = it.toDoubleOrNull() ?: 0.0
        if (enteredAmount > maxAmount) {
            onAmountEntered(maxAmount.toString())
        } else {
            onAmountEntered(it)
        }
    }

    TextField(
        modifier = modifier
            .weight(1f)
            .focusRequester(focusRequester),
        value = TextFieldValue(
            text = transferAmount.value,
            selection = TextRange(transferAmount.value.length)
        ),
        onValueChange = { value ->
            onAmountChanged(value.text)
        },
        placeholder = { Text("0", style = ElementTheme.typography.fontHeadingMdRegular) },
        singleLine = true,
        maxLines = 1,
        textStyle = ElementTheme.typography.fontHeadingMdRegular,
        shape = RectangleShape,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
    )
}
