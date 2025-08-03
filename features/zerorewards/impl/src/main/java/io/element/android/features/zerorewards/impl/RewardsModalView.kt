package io.element.android.features.zerorewards.impl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.element.android.compound.theme.ElementTheme
import io.element.android.libraries.designsystem.components.button.BackButton
import io.element.android.libraries.designsystem.preview.ElementPreview
import io.element.android.libraries.designsystem.preview.PreviewsDayNight
import io.element.android.libraries.designsystem.theme.components.Icon
import io.element.android.libraries.designsystem.theme.components.Scaffold
import io.element.android.libraries.designsystem.theme.components.Text
import io.element.android.libraries.designsystem.theme.zero.color.zeroBrandColor
import io.element.android.libraries.designsystem.theme.zero.typography.zeroTypography
import io.element.android.libraries.matrix.api.zero.rewards.ZeroUserRewards
import io.element.android.support.zero.common.ui.theme.PADDING_3X
import io.element.android.support.zero.common.ui.theme.PADDING_8X
import io.element.android.support.zero.common.ui.theme.SPACING_10X
import io.element.android.support.zero.common.ui.theme.SPACING_2X
import io.element.android.support.zero.common.ui.theme.SPACING_4X
import io.element.android.support.zero.data.model.helper.RewardsUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsModalView(
    modifier: Modifier = Modifier,
    state: RewardsModalState,
    onBackClick: () -> Unit = {}
) {
    val showRewardsFAQ = remember { mutableStateOf(false) }
    val handleBackPress: () -> Unit = {
        if (showRewardsFAQ.value) {
            showRewardsFAQ.value = false
        } else {
            onBackClick()
        }
    }

    BackHandler { handleBackPress() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    BackButton(onClick = handleBackPress)
                },
                title = {
                    Text(
                        text = if (showRewardsFAQ.value) {
                            stringResource(R.string.rewards_screen_faq_title)
                        } else stringResource(R.string.rewards_screen_title),
                        style = ElementTheme.zeroTypography.aliasScreenTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        },
        content = {
            val parentModifier: Modifier = if (showRewardsFAQ.value) {
                Modifier
                    .padding(it)
                    .consumeWindowInsets(it)
                    .verticalScroll(state = rememberScrollState())
            } else {
                Modifier
                    .padding(it)
                    .consumeWindowInsets(it)
            }
            val parentArrangement = if (showRewardsFAQ.value) {
                Arrangement.Top
            } else Arrangement.SpaceBetween
            Column(
                modifier = parentModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = parentArrangement
            ) {
                if (showRewardsFAQ.value) {
                    RewardsFAQView()
                } else {
                    RewardCredits(
                        rewards = state.userRewards,
                        onViewRewardsFaq = {
                            showRewardsFAQ.value = true
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun ColumnScope.RewardCredits(
    rewards: ZeroUserRewards,
    onViewRewardsFaq: () -> Unit = {}
) {
    // credits
    Column(
        modifier = Modifier.fillMaxWidth()
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(SPACING_2X.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_rewards_header),
                contentDescription = null
            )
            Icon(
                modifier = Modifier.size(50.dp).align(Alignment.Center).padding(top = PADDING_3X.dp),
                painter = painterResource(id = io.element.android.support.zero.R.drawable.zero_logo_icon),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.size(SPACING_10X.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val refPrice = RewardsUtil.getRefPrice(
                zero = rewards.zero,
                decimals = rewards.decimals,
                refPrice = rewards.price
            )
            Text(
                text = "\$$refPrice".trim(),
                style = ElementTheme.zeroTypography.fontHeadingLgMediumRoboto,
                color = ElementTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.size(SPACING_2X.dp))
            val credits = RewardsUtil.getEarnedRewardsFormatted(
                zero = rewards.zero,
                decimals = rewards.decimals
            )
            Text(
                text = "$credits MEOW",
                style = ElementTheme.zeroTypography.fontBodyMdRegularRoboto,
                color = ElementTheme.colors.textSecondary
            )

            if (rewards.hasUnclaimedRewards) {
                Spacer(modifier = Modifier.size(SPACING_4X.dp))
                val unclaimedRewardsPrice = RewardsUtil.getRefPrice(
                    zero = rewards.unclaimedRewards,
                    decimals = rewards.decimals,
                    refPrice = rewards.price
                )
                Text(
                    text = "You can now claim $$unclaimedRewardsPrice".trim(),
                    style = ElementTheme.zeroTypography.fontBodyMdRegularRoboto,
                    color = ElementTheme.colors.textSecondary
                )
            }
        }
    }

    Column {
        val infoText = buildAnnotatedString {
            withStyle(SpanStyle(color = ElementTheme.colors.textPrimary)) {
                append(stringResource(id = R.string.earn_by_messaging))
            }
            append(" ")
            withStyle(SpanStyle(color = ElementTheme.colors.zeroBrandColor)) { append("More ->") }
        }
        ClickableText(
            modifier = Modifier.fillMaxWidth().padding(horizontal = PADDING_8X.dp),
            text = infoText,
            style = ElementTheme.zeroTypography.fontBodyMdRegular,
            onClick = { onViewRewardsFaq.invoke() }
        )
        Spacer(modifier = Modifier.size(SPACING_10X.dp))
    }
}

@PreviewsDayNight
@Composable
fun RewardsModalViewPreview() = ElementPreview {
    RewardsModalView(state = RewardsModalState(userRewards = ZeroUserRewards.empty()))
}
